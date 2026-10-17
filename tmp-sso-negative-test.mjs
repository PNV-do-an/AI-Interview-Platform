// Test phụ trợ cho Enterprise SSO: drive trọn OIDC code flow bằng cookie jar sạch,
// không phụ thuộc phiên Keycloak của trình duyệt.
const KC = 'http://localhost:8180/realms/ai-interview';
const BACKEND = 'http://localhost:8080/api/v1/auth/sso';
const REDIRECT_URI = 'http://localhost:3000/login';

let jar = {};
function saveCookies(res) {
  for (const raw of res.headers.getSetCookie?.() ?? []) {
    const [kv] = raw.split(';');
    const idx = kv.indexOf('=');
    jar[kv.slice(0, idx).trim()] = kv.slice(idx + 1).trim();
  }
}
const cookieHeader = () => Object.entries(jar).map(([k, v]) => `${k}=${v}`).join('; ');

const decodeEntities = (s) => s.replace(/&amp;/g, '&').replace(/&#x2F;/g, '/').replace(/&quot;/g, '"');

async function runCase(label, username, password, authNonce, postNonce, expect) {
  jar = {};
  const state = 'state-' + Math.random().toString(36).slice(2);
  const authUrl = `${KC}/protocol/openid-connect/auth?client_id=ai-interview-backend`
    + `&redirect_uri=${encodeURIComponent(REDIRECT_URI)}&response_type=code`
    + `&scope=${encodeURIComponent('openid email profile')}&state=${state}&nonce=${authNonce}`;

  let res = await fetch(authUrl, { redirect: 'manual', headers: { cookie: cookieHeader() } });
  saveCookies(res);
  let html = await res.text();
  const formAction = decodeEntities(/<form[^>]*id="kc-form-login"[^>]*action="([^"]*)"/.exec(html)?.[1] || '');
  if (!formAction || !html.includes('name="username"')) {
    return console.log(`${label}: không đọc được form (status ${res.status})`);
  }

  const body = new URLSearchParams({ username, password, credentialId: '', login: 'Sign In' });
  res = await fetch(formAction, {
    method: 'POST',
    redirect: 'manual',
    headers: { 'content-type': 'application/x-www-form-urlencoded', cookie: cookieHeader() },
    body,
  });
  saveCookies(res);
  let loc = res.headers.get('location') || '';
  if (!loc.includes('code=')) {
    // KC có thể redirect qua 1-2 bước trung gian trước khi trả code
    for (let hop = 0; hop < 4 && loc && !loc.includes('code='); hop++) {
      res = await fetch(loc, { redirect: 'manual', headers: { cookie: cookieHeader() } });
      saveCookies(res);
      loc = res.headers.get('location') || '';
    }
  }
  const params = new URLSearchParams(loc.slice(loc.indexOf('?') + 1));
  const code = params.get('code');
  const err = params.get('error');
  if (!code) {
    return console.log(`${label}: không lấy được code :: error=${err} ${loc.slice(0, 160)}`);
  }

  const apiRes = await fetch(BACKEND, {
    method: 'POST',
    headers: { 'content-type': 'application/json' },
    body: JSON.stringify({ code, redirectUri: REDIRECT_URI, nonce: postNonce ?? authNonce }),
  });
  const text = await apiRes.text();
  const ok = text.includes(expect);
  console.log(`${ok ? 'PASS' : 'FAIL'} ${label}: HTTP ${apiRes.status} :: ${text.slice(0, 200)}`);
}

const freshNonce = () => 'nonce-' + Math.random().toString(36).slice(2);

await runCase('HAPPY PATH (hr@acme.com)', 'hr@acme.com', 'SsoDev@12345',
  freshNonce(), null, '"success":true');
await runCase('DOMAIN BLOCKED (candidate@gmail.com)', 'candidate@gmail.com', 'SsoDev@12345',
  freshNonce(), null, 'domain');
// authorize bằng nonce A, nhưng POST nonce B về backend -> phải bị từ chối
const mismatchNonce = freshNonce();
await runCase('NONCE MISMATCH (hr@acme.com)', 'hr@acme.com', 'SsoDev@12345',
  mismatchNonce, 'nonce-khac-bien', 'Nonce');
