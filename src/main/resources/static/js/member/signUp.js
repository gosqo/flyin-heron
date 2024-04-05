// 윈도우가 로드될 때 수행할 일들.
// 버튼 핸들링 관련, Node 변수 선언 및 할당.
window.addEventListener('load', () => {
    const submitButton = document.querySelector('#submit-form-btn');
    const isPresentEmailButton = document.querySelector('#is-present-email-button');
    const isPresentNicknameButton = document.querySelector('#is-present-nickname-button');

    // 버튼이 클릭 이벤트를 수신했을 떄, 수행할 일들.
    // 해당 시점의 입력 값을 가져온다. (변수 선언 및 할당)
    // 서버에 요청.
    isPresentNicknameButton.addEventListener('click', async (event) => {
        event.preventDefault();
        const targetElement = document.querySelector('input[name=nickname]');
        const checkMessage = document.querySelector(
            `#${targetElement.name}IsPresentCheckMessage`);
        if (checkMessage) checkMessage.remove();

        const valueToCheck = targetElement.value;
        const url = '/api/v1/member/isPresentNickname';
        const options = {
            headers: {
                'Content-Type': 'application/json',
            },
            method: 'POST',
            body: JSON.stringify({"valueToCheck": valueToCheck})
        };

        const isPresentResultMessage = await fetchAndReturnMessage(url, options);
        const isPassed = isPresentResultMessage === '사용 가능한 닉네임입니다.';
        addResultMessageIsPresent(targetElement, isPassed, isPresentResultMessage);
    });

    isPresentEmailButton.addEventListener('click', async (event) => {
        event.preventDefault();
        const targetElement = document.querySelector('input[name=email]');
        const checkMessage = document.querySelector(
            `#${targetElement.name}IsPresentCheckMessage`);
        if (checkMessage) checkMessage.remove();

        const valueToCheck = targetElement.value;
        const url = '/api/v1/member/isPresentEmail';
        const options = {
            headers: {
                'Content-Type': 'application/json',
            },
            method: 'POST',
            body: JSON.stringify({"valueToCheck": valueToCheck})
        };

        const isPresentResultMessage= await fetchAndReturnMessage(url, options);
        const isPassed = isPresentResultMessage === '사용 가능한 이메일 주소입니다.';
        addResultMessageIsPresent(targetElement, isPassed, isPresentResultMessage);
    });

    submitButton.addEventListener('click', async (event) => {
        event.preventDefault();
        const form = document.querySelector('#form');
        const formData = new FormData(form);
        const body = {};
        
        formData.forEach((value, key) => {
            body[key] = value;
        });

        const url = '/api/v1/member/';
        const options = {
            headers: {
                'Content-Type': 'application/json',
            },
            method: 'POST',
            body: JSON.stringify(body)
        };

        await fetchSubmit(url, options);
    });
});

function addResultMessageIsPresent(targetElement, flag, resultMessage) {
    const messageElement = document.querySelector(`#${targetElement.name}ResultMessageIsPresent`);
    if (messageElement) messageElement.remove();

    const message = document.createElement('p');
    message.id = `${targetElement.name}ResultMessageIsPresent`;
    message.textContent = resultMessage;
    if (flag) {
        message.style.color ='green';
    } else {
        message.style.color ='red';
    }
    targetElement.closest('div').append(message);
}

// async function fetchAndAlertMessage(url, options) {
//     try {
//         const response = await fetch(url, options);
//         const data = await response.json();
//         alert(data.message);
//     } catch (error) {
//         console.error('Error: ', error);
//     }
// }

async function fetchAndReturnMessage(url, options) {
    try {
        const response = await fetch(url, options);
        const data = await response.json();
        return data.message;
    } catch (error) {
        console.error('Error: ', error);
    }
}

async function fetchSubmit(url, options) {
    try {
        const response = await fetch(url, options);
        console.log(response);

        if (response.status === 200) {

            const result = await response.text();
            alert(result);

            location.replace('/login');

        } else if (response.status === 401) {

            const result = await response.json();
            alert(result.additionalMessage);

        } else if (response.status === 400) {

            const result = await response.json();
            console.log(result);
            alert(result.message);

        } else {

            const result = await response.json();
            alert(result.message);

        }
    } catch (error) {
        console.error(error);
    }
}
