const form = document.querySelector('#form');
const submitButton = document.querySelector('#submit-form-btn');

submitButton.addEventListener('click', async (event) => {
    event.preventDefault();

    const formData = new FormData(form);

    const body = {};
    formData.forEach((value, key) => {
        body[key] = value;
    });

    const url = '/api/v1/auth/authenticate';
    const requestInit = {
        headers: {
            "Content-Type": 'application/json',
        },
        method: 'POST',
        body: JSON.stringify(body),
    }

    try {
        const response = await fetch(url, requestInit);
        console.log(response);

        if (response.status === 200) {

            const result = await response.json();
            console.log(result);

            console.log("access_token: " + result.access_token);
            console.log("refresh_token: " + result.refresh_token);
            localStorage.setItem('access_token', 'Bearer ' + result.access_token);
            localStorage.setItem('refresh_token', 'Bearer ' + result.refresh_token);

            alert('sign in success.');

            self.location = '/';
            
        } else if (response.status === 500) {
            const result = await response.json();
            console.log(result);
            alert('login failed.');

        }
    } catch (error) {
        console.error("Error: ", error);
    }
});
 