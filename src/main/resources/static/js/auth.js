const TOKEN_STORE = {
    getAccessToken() {
        return localStorage.getItem("accessToken") || sessionStorage.getItem("accessToken");
    },
    setAccessToken(token, rememberMe) {
        if (rememberMe) {
            localStorage.setItem("accessToken", token);
        } else {
            sessionStorage.setItem("accessToken", token);
        }
    },
    getRefreshToken() {
        return localStorage.getItem("refreshToken") || sessionStorage.getItem("refreshToken");
    },
    setRefreshToken(token, rememberMe) {
        if (rememberMe) {
            localStorage.setItem("refreshToken", token);
        } else {
            sessionStorage.setItem("refreshToken", token);
        }
    },
    clearTokens() {
        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");
        sessionStorage.removeItem("accessToken");
        sessionStorage.removeItem("refreshToken");
    }
};

async function refreshAccessToken() {
    const refreshToken = TOKEN_STORE.getRefreshToken();
    if (!refreshToken) {
        redirectToLogin();
        return null;
    }

    try {
        const response = await fetch("/api/refresh", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ refreshToken: refreshToken })
        });

        if (response.status === 401) {
            redirectToLogin();
            return null;
        }

        const data = await response.json();
        const rememberMe = localStorage.getItem("rememberMe") === "true";
        TOKEN_STORE.setAccessToken(data.accessToken, rememberMe);
        return data.accessToken;
    } catch (error) {
        redirectToLogin();
        return null;
    }
}

function redirectToLogin() {
    TOKEN_STORE.clearTokens();
    localStorage.setItem("redirectAfterLogin", window.location.pathname);
    window.location.href = "/indexLogin.html";
}

async function login() {
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const rememberMe = document.getElementById("rememberMe").checked;
    const errorEl = document.getElementById("error");

    if (!email || !password) {
        errorEl.innerText = "Vui lòng nhập email và mật khẩu";
        return;
    }

    try {
        const response = await fetch("/api/auth/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, password })
        });

        const data = await response.json();

        if (!response.ok) {
            errorEl.innerText = data.message || "Đăng nhập thất bại";
            return;
        }

        TOKEN_STORE.setAccessToken(data.accessToken, rememberMe);
        TOKEN_STORE.setRefreshToken(data.refreshToken, rememberMe);
        localStorage.setItem("rememberMe", rememberMe);

        document.getElementById("accessToken").innerText = data.accessToken;
        document.getElementById("refreshToken").innerText = data.refreshToken;

        const redirectUrl = localStorage.getItem("redirectAfterLogin");
        localStorage.removeItem("redirectAfterLogin");

        if (redirectUrl && redirectUrl !== "/indexLogin.html") {
            window.location.href = redirectUrl;
        } else {
            window.location.href = "/Profile.html";
        }
    } catch (error) {
        errorEl.innerText = "Lỗi: " + error.message;
    }
}
