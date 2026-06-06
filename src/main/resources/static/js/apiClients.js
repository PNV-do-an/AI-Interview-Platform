async function callApi(url, options = {}) {

    const response = await fetch(url, {

        ...options,

        headers: {
            ...options.headers,
            "Authorization":
                "Bearer " +
                localStorage.getItem("accessToken")
        }
    });

    if (response.status === 401) {

        localStorage.setItem(
            "redirectAfterLogin",
            window.location.pathname
        );

        await refreshAccessToken();

        return callApi(url, options);
    }

    return response;
}
// function callApi() {
//     // gửi để token mỗi lần để có thể xác minh ( mỗi requset với mỗi chức năng )
//     fetch("/api/test", {
//
//         method: "GET",
//
//         headers: {
//             "Authorization":
//                 "Bearer " + localStorage.getItem("accessToken")
//         }
//
//     })
//         .then( async res => {
//
//             if (res.status === 401) {
//
//                 localStorage.setItem("redirectAfterLogin",window.location.pathname)
//
//                 await refreshAccessToken(); // if refreshToken expired  -> call to refreshAccessToken -> it will call to login -> user login
//                                             // -> after that user will back to home page not feature page how to when user completed login
//                                             // will back this feature. => set Item : ("redirectAfterLogin",window.location.pathname)
//
//                 return callApi();
//             }
//
//             return res.text();
//
//         })
//         .then(data => {
//
//             document.getElementById("result").innerText = data;
//
//         });
//
// }