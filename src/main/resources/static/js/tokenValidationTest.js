tokenValidationTestButton.addEventListener('click', (event) => {
    event.preventDefault();
    console.log(event.target);
    tokenValidationTest();
});

async function tokenValidationTest() {
    const url = '/tokenValidationTest';
    let options = {
        headers: {
            'Authorization': localStorage.getItem('access_token'),
        }
    };

    try {

        const data = await fetchWithToken(url, options);

        const paragraph = document.createElement('p');
        document.body.append(paragraph);
        paragraph.textContent = data.email + " " + data.expiration;

    } catch (error) {
        console.error('Error' + error);
    }
}
