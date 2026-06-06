async function refreshAccessToken() {

    const refreshToken = localStorage.getItem("refreshToken");

    const response = fetch("/api/refresh", {
        method : "POST",

        headers :  {
            "Content-Type" : "application/json"
        },

        body: JSON.stringify({
            refreshToken: refreshToken
        })
    })

        // const data = await response.json(); // <- tại sao như này mà ko phải
        .then(res => {
            if(res.status === 401) { //
                document.getElementById("result").innerText = "Refresh Token is expired" // ví dụ lỗi 401 -> refreshToken : hết hạn đăng nhập lại
                return login();
            }
        })

        .then( res => res.json())

        .then(data => {

            localStorage.setItem("accessToken", data.accessToken);
        })
}

function login() {
    const account = document.getElementById("account").value;
    const password = document.getElementById("password").value;

    fetch("/api/auth/login", {

        method: "POST",

        headers: {
            "Content-Type": "application/json"
        },

        body: JSON.stringify({
            account: account,
            password: password
        })

    })

        .then(res => res.json())
        .then(data => {

            const accessToken = data.accessToken;
            const refreshToken = data.refreshToken;
            // const name = data.

            localStorage.setItem("accessToken", accessToken);
            localStorage.setItem("refreshToken", refreshToken);

            document.getElementById("accessToken").innerText = accessToken;
            document.getElementById("refreshToken").innerText = refreshToken;

        });
    window.location.href = localStorage.getItem("redirectAfterLogin") // after login if before user use another page like profile, cart, ...
    // when user's refeshToken is epxired so user have to login again and item "redirectAfterLogin" will call back page before

}