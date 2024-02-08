const form = document.querySelector('#form');
const submitButton = document.querySelector('#submit-form-btn');

submitButton.addEventListener('click', async (event) => {
    event.preventDefault();

    const formData = new FormData(form);

    const body = {};
    formData.forEach((value, key) => {
        body[key] = value;
    });

    const requestInit = {
        headers: {
            "Content-Type": 'application/json;utf-8',
        },
        method: 'POST',
        body: JSON.stringify(body),
    }
    try {
        const response = await fetch('/login/process', requestInit);
        console.log(response);
        console.log(response.body);
        if (response.status === 200) {

            const result = await response.text();
            console.log("token: " + result);
            sessionStorage.setItem('jwt', result);

            // alert('sign in success.');
            
            self.location = '/';
            
        } else if (response.status === 500) {
            const result = await response.json();
            console.log(result);
            alert('sign in failed.');

        }
    } catch (error) {
        console.error("Error: ", error);
    }
});