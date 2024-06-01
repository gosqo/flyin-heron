// main
window.addEventListener('load', () => {
    // 변수를 사용하는 함수들을 현재 코드 블록 내부에 위치시키는 것을 검토.
    // 입력값의 검증을 통과한 후, 회원가입 버튼의 활성화하기.
    // 중복확인 후 적절한 메세지를 html body 내부에 위치 시키기.
    // nodes
    const emailInput = document.querySelector('input[name="email"]');
    const passwordInput = document.querySelector('input[name="password"]');
    const passwordCheckInput = document.querySelector('input[name="passwordCheck"]');
    const nicknameInput = document.querySelector('input[name="nickname"]');

    // regular expressions
    const emailRegex = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/;
    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[A-Za-z\d~!@#$%^&*()+{}|:"<>?`=\[\]-_\\;']{8,20}$/;
    const nicknameRegex = /^[0-9a-zA-Z가-힣]{2,20}$/;

    // validation messages
    const matchedMessage = '적합';
    const matchedPasswordCheckMessage = '비밀번호와 일치합니다.';
    const notMatchedEmail = 'Email 형식을 확인해주세요.';
    const notMatchedPassword = '비밀번호는 영어 대소문자, 숫자, 특수문자를 조합하여 사용할 수 있습니다.\n최소 8자 이상의 문자가 필요합니다.';
    const notMatchedPasswordCheck = '비밀번호와 일치하지 않습니다.\n비밀번호를 확인하거나 양식에 맞는 비밀번호 입력을 선행해주세요.';
    const notMatchedNickname = '닉네임은 2자 이상 20자 이하의의 한글, 영어로 사용할 수 있습니다.';

    // Event Listeners
    addMessageIfValid(emailInput);
    addMessageIfValid(nicknameInput);

    emailInput.addEventListener('input', (event) => {
        const { targetElement, message } = assignTargetAndMessage(event);
        isValidEmail = isValidValue(targetElement, emailRegex, 6, 50)
            ? true : false;

        removeMessages(targetElement);
        messageAccordingToFlag(targetElement, isValidEmail, message, matchedMessage, notMatchedEmail);
        
    });

    passwordInput.addEventListener('input', (event) => {
        const { targetElement, message } = assignTargetAndMessage(event);
        isValidPassword = isValidValue(passwordInput, passwordRegex, 8, 20)
            ? true : false;
        
        removeMessages(targetElement);
        messageAccordingToFlag(targetElement, isValidPassword, message, matchedMessage, notMatchedPassword);
    });

    passwordCheckInput.addEventListener('input', (event) => {
        const { targetElement, message } = assignTargetAndMessage(event);
        isValidPasswordCheck = isMatchedPasswordCheck(passwordInput, passwordCheckInput, passwordRegex)
            ? true : false;

        removeMessages(targetElement);
        messageAccordingToFlag(targetElement, isValidPasswordCheck, message, matchedPasswordCheckMessage, notMatchedPasswordCheck);
        activateSubmitIfClear();
    });

    nicknameInput.addEventListener('input', (event) => {
        const { targetElement, message } = assignTargetAndMessage(event);
        isValidNickname = isValidValue(nicknameInput, nicknameRegex, 2, 20)
            ? true : false;

        removeMessages(targetElement);
        messageAccordingToFlag(targetElement, isValidNickname, message, matchedMessage, notMatchedNickname);
    });
});

//functions
function addMessageIfValid(targetElement) {
    targetElement.addEventListener('blur', () => {
        const resultMessageIsPresent = document.querySelector(
            `#${targetElement.name}ResultMessageIsPresent`);
        if (resultMessageIsPresent) return;

        const validationMessageElement =
            document.querySelector(`#${targetElement.name}ValidationMessage`);
        const isPresentCheckMessage = document.createElement('small');
        isPresentCheckMessage.id = `${targetElement.name}IsPresentCheckMessage`;
        isPresentCheckMessage.textContent = `${targetElement.name} 중복 확인을 해주세요.`;

        removeElementIfPresent(targetElement, 'IsPresentCheckMessage');

        if (validationMessageElement
            && validationMessageElement.style.color === 'green'
        ) validationMessageElement.closest('div').nextElementSibling.append(isPresentCheckMessage);
    });
}

function assignTargetAndMessage(event) {
    const targetElement = event.target;
    const message = createElement('small', `${targetElement.name}ValidationMessage`, null);
    return { targetElement, message };
}

function removeMessages(targetElement) {
    removeElementIfPresent(targetElement, 'IsPresentCheckMessage');
    removeElementIfPresent(targetElement, 'ResultMessageIsPresent');
    removeElementIfPresent(targetElement, 'ValidationMessage');
}

function messageAccordingToFlag(targetElement, flag, messageElement, matchedMessage, notMatchedMessage) {
    targetElement.closest('div').nextElementSibling.append(messageElement);

    if (flag) {
        messageElement.textContent = matchedMessage;
        messageElement.style.color = 'green';
    } else {
        messageElement.textContent = notMatchedMessage;
        messageElement.style.color = 'red';
    }
}

function isMatchedPasswordCheck(passwordInput, passwordCheckInput, passwordRegex) {
    return passwordInput.value === passwordCheckInput.value
        && passwordRegex.test(passwordInput.value);
}

function isValidValue(targetElement, regex, minLength, maxLength) {
    return regex.test(targetElement.value)
        && targetElement.value.length >= minLength
        && targetElement.value.length <= maxLength;
}
