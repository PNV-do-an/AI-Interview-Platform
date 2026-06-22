async function refreshAccessToken() {

    const refreshToken = localStorage.getItem("refreshToken");
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
        localStorage.setItem("accessToken", data.accessToken);
        return data.accessToken;
    } catch (error) {
        redirectToLogin();
        return null;
    }
}

function redirectToLogin() {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
    localStorage.setItem("redirectAfterLogin", window.location.pathname);
    window.location.href = "/indexLogin.html";
}

async function login() {
    const account = document.getElementById("account").value;
    const password = document.getElementById("password").value;
    const errorEl = document.getElementById("error");

    try {
        const response = await fetch("/api/auth/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ account: account, password: password })
        });

        if (!response.ok) {
            const errorData = await response.json();
            errorEl.innerText = errorData.message || "Login failed";
            return;
        }

        const data = await response.json();

        localStorage.setItem("accessToken", data.accessToken);
        localStorage.setItem("refreshToken", data.refreshToken);

        document.getElementById("accessToken").innerText = data.accessToken;
        document.getElementById("refreshToken").innerText = data.refreshToken;

        const redirectUrl = localStorage.getItem("redirectAfterLogin");
        localStorage.removeItem("redirectAfterLogin");
        window.location.href = redirectUrl || "/indexLogin.html";
    } catch (error) {
        errorEl.innerText = "Error: " + error.message;
    }
}
