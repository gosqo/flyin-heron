window.addEventListener('load', async () => {
    const buttonsArea = document.querySelector('#buttons-area')
    const accessToken = localStorage.getItem('access_token');
    const decodedJwt = parseJwt(accessToken);
    // console.log(decodedJwt);
    const userEmail = decodedJwt.sub;
    // console.log(userEmail);
    const userId = decodedJwt.id;
    // console.log(userId);
    
    // fragment/boardHeader button control
    const goBackButton = document.querySelector('#go-back-btn');
    goBackButton.addEventListener('click', () => {
        history.back();
    });
    
    // buttons DOM
    if (userId) {
        // submit Button
        const submitButton = document.createElement('button');

        submitButton.id = 'submit-btn';
        submitButton.className = 'btn btn-primary';
        submitButton.textContent = 'Submit';

        buttonsArea.append(submitButton);

        // resetButton
        const resetButton = document.createElement('button');

        resetButton.id = 'reset-btn';
        resetButton.className = 'btn btn-secondary';
        resetButton.textContent = 'Reset';

        buttonsArea.append(resetButton);

        // cancelButton
        const cancelButton = document.createElement('button');

        cancelButton.id = 'cancel-btn';
        cancelButton.className = 'btn btn-secondary';
        cancelButton.textContent = 'Cancel';

        buttonsArea.append(cancelButton);

    }

    // cancel and back button
    const cancelButton = document.querySelector('#cancel-btn');
    if (cancelButton) {
        cancelButton.addEventListener(
            'click'
            , () => {
                const confirmation = confirm('작성을 취소하시겠습니까?\n 확인을 클릭 시, 작성 내용을 저장하지 않고 이전 페이지로 이동합니다.');
                if (confirmation) {

                    const inputs = document.querySelectorAll('.form-control');
                    inputs.forEach((input) => {
                        input.value = '';
                    });
                    history.back();
                }
            });
    }

    // reset button.
    const resetButton = document.querySelector('#reset-btn');
    if (resetButton) {
        resetButton.addEventListener(
            'click'
            , () => {
                const confirmation = confirm('작성하신 내용을 지우시겠습니까?');
                if (confirmation) {

                    const inputs = document.querySelectorAll('.form-control');
                    inputs.forEach((input) => {
                        input.value = '';
                    });
                }
            });
    }

    // submit board fetch.
    const submitButton = document.querySelector('#submit-btn');
    if (submitButton) {
        submitButton.addEventListener(
            'click',
            async () => {
                const url = `/api/v1/board`;
                let options = {
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': localStorage.getItem('access_token')
                    },
                    method: 'POST',
                    body: JSON.stringify(formToBody())
                };

                try {

                    const data = await fetchWithToken(url, options);
                    console.log(data);
                    if (data.message.includes('Validation')) {

                        alert(data.errors[0].defaultMessage);
                        
                    } else {
                        
                        if (data.id === undefined) {
                            alert(data.message);
                            return;
                        }
                        alert(data.message);
                        location.replace(`/board/${data.id}`);
                    }

                } catch (error) {
                    console.error('Error: ', error);
                }

            }
        );
    }
});

function formToBody() {

    const form = document.querySelector('#form');
    const formData = new FormData(form);
    const body = {};
    formData.forEach((value, key) => {
        body[key] = value;
    });

    return body;
}

function parseJwt(token) {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(window.atob(base64)
        .split('')
        .map(function (c) {
            return '%'
                + ('00' + c.charCodeAt(0).toString(16))
                    .slice(-2);
        })
        .join('')
    );

    return JSON.parse(jsonPayload);
}
