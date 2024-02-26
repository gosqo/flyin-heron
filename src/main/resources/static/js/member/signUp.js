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
            "Content-Type": 'application/json',
        },
        method:'POST',
        body: JSON.stringify(body),
    }
    try {
        const response = await fetch('/member', requestInit);
        console.log(response);
        console.log(response.body);

        if(response.status === 200) {

            const result = await response.text()
            console.log("nickname is " + result);

            alert('회원가입을 완료했습니다. 로그인 화면으로 이동합니다.');

            self.location = '/login';

        } else {

            const result = await response.text();
            console.log(result);
            alert('회원가입에 실패했습니다.');

        }
    } catch (error) {
        console.error(error);
    }

});