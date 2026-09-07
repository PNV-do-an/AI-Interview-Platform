async function callApi(url, options = {}) {

    let accessToken = TOKEN_STORE.getAccessToken();

    if (!accessToken) {
        const newToken = await refreshAccessToken();
        if (!newToken) {
            return new Response(null, { status: 401 });
        }
        accessToken = newToken;
    }

    const response = await fetch(url, {
        ...options,
        headers: {
            ...options.headers,
            "Authorization": "Bearer " + accessToken
        }
    });

    if (response.status === 401) {
        localStorage.setItem("redirectAfterLogin", window.location.pathname);

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
