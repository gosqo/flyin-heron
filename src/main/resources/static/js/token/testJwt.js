
async function testJwt() {
    const accessToken = localStorage.getItem('access_token')
    const url = '/tokenValidationTest';
    let options = {
        headers: {
            'Authorization': accessToken,
        }
    };

    try {
        const data = await fetchWithToken(url, options);

        if (data === undefined) return;

        const paragraph = createParagraph(null, null, `${data.email} / ${data.expiration}`);
        document.querySelector('#test-jwt-area').append(paragraph);

    } catch (error) {
        console.error('Error ' + error);
    }
}
