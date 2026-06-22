async function callApi(url, options = {}) {

    let accessToken = localStorage.getItem("accessToken");

    const response = await fetch(url, {
        ...options,
        headers: {
            ...options.headers,
            "Authorization": "Bearer " + accessToken
        }
    });

    if (response.status === 401) {

        localStorage.setItem(
            "redirectAfterLogin",
            window.location.pathname
        );

        const newToken = await refreshAccessToken();
        if (!newToken) {
            return response;
        }

        return fetch(url, {
            ...options,
            headers: {
                ...options.headers,
                "Authorization": "Bearer " + newToken
            }
        });
    }

    return response;
}
