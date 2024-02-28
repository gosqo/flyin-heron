tokenValidationTestButton.addEventListener('click', (event) => {
    event.preventDefault();
    console.log(event.target);
    tokenValidationTest();
});

async function tokenValidationTest() {
    const url = 'api/v1/auth/tokenValidationTest';
    const requestInit = {
        headers: {
            'Authorization': access_token,
        }
    }
    try {

        const response = await fetch(url, requestInit)
        const data = await response.text();

        const paragraph = document.createElement('p');
        document.body.append(paragraph);
        paragraph.textContent = data;

    } catch (error) {
        console.error('Error' + error);
    }
}
