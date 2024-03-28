const form = document.querySelector('#form');
const submitButton = document.querySelector('#submit-form-btn');

submitButton.addEventListener('click', async (event) => {

    event.preventDefault();

    const formData = new FormData(form);

    const body = {};
    formData.forEach((value, key) => {
        body[key] = value;
    });

    const url = '/member'
    const requestInit = {
        headers: {
            "Content-Type": 'application/json',
        },
        method:'POST',
        body: JSON.stringify(body),
    }
    try {
        const response = await fetch(url, requestInit);
        console.log(response);

        if(response.status === 200) {

            const result = await response.text()
            alert(result);

            self.location = '/login';

        } else if (response.status === 401) {

            const result = await response.json();
            alert(result.additionalMessage);

        } else if (response.status === 400) {

            const result = await response.json();
            alert(result.errors[0].defaultMessage);

        } else {

            const result = await response.json();
            alert(result.message);
            
        }
    } catch (error) {
        console.error(error);
    }

});
