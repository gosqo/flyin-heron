if (localStorage.getItem('access_token')) {
    const tokenValidationButton = document.querySelector('#jwtValidationButton');

    tokenValidationButton.addEventListener('click', (event) => {
        event.preventDefault();
        // console.log(event.target);
        tokenValidationCheck();
    });

    async function tokenValidationCheck() {
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

            const paragraph = document.createElement('p');
            document.querySelector('#test-jwt-area').append(paragraph);
            paragraph.textContent = `${data.email} / ${data.expiration}`;

        } catch (error) {
            console.error('Error ' + error);
        }
    }
}
