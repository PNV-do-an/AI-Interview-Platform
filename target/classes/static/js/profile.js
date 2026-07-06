
async function getProfile() {
    const response = await callApi("api/user/profile",
    {
        method : "GET"
    });

    if (!response.ok) {
        throw new Error("Can't load profile");
        // throw new object error catch error of Java is different with JavaScript

    }
    return response.json();
}
