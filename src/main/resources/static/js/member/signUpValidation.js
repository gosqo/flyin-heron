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

    addValidationTo(
        emailInput,
        isValidEmail,
        matchedMessage,
        notMatchedEmail
    );

    addValidationTo(
        passwordInput,
        isValidPassword,
        matchedMessage,
        notMatchedPassword
    );

    addValidationTo(
        passwordCheckInput,
        isValidPasswordCheckFlag,
        matchedPasswordCheckMessage,
        notMatchedPasswordCheck
    );

    addValidationTo(
        nicknameInput,
        isValidNickname,
        matchedMessage,
        notMatchedNickname,
    );

    addCheckMessageIfValid(emailInput);
    addCheckMessageIfValid(nicknameInput);

    // Event Listeners
    // 유효한 값 입력 후, 중복 확인 요청 메세지를 더함.
    function addCheckMessageIfValid(targetElement) {
        targetElement.addEventListener('blur', () => {
            const resultMessageIsPresent = document.querySelector(
                `#${targetElement.name}ResultMessageIsPresent`);
            if (resultMessageIsPresent) return;

            const validationMessageElement =
                document.querySelector(`#${targetElement.name}ValidationMessage`);
            const isPresentCheckMessage = document.createElement('p');
            isPresentCheckMessage.id = `${targetElement.name}IsPresentCheckMessage`;
            isPresentCheckMessage.textContent = `${targetElement.name} 중복 확인을 해주세요.`;
    
            // const isPresentCheckMessageElement = document.querySelector(
            //         `#${targetElement.name}IsPresentCheckMessage`);
            // if (isPresentCheckMessageElement) isPresentCheckMessageElement.remove();
            removeElementIfPresent(targetElement, 'IsPresentCheckMessage');
            
            if (validationMessageElement
                && validationMessageElement.style.color === 'green'
            ) validationMessageElement.closest('div').append(isPresentCheckMessage);
        });
    }
    
    // 이메일, 닉네임이 유일한 값인지 확인. (구현되있음 fetch in signUp.js)
    // 통신 결과에 따라 중복확인 요청한 메세지를 사용 가능, 불가능(통신 결과)을 알리는 메시지로 대체.
    emailInput.addEventListener('input', () => {
        isValidEmail = isValidValue(emailInput, emailRegex, 6, 50)
            ? true : false;
    }); 
    passwordInput.addEventListener('input', () => {
        isValidPassword = isValidValue(passwordInput, passwordRegex, 8, 20)
            ? true : false;
    }); 
    passwordCheckInput.addEventListener('input', () => {
        isValidPasswordCheckFlag = isValidPasswordCheck(passwordInput, passwordCheckInput, passwordRegex)
            ? true : false;
    }); 
    nicknameInput.addEventListener('input', () => {
        isValidNickname = isValidValue(nicknameInput, nicknameRegex, 2, 20)
            ? true : false;
    }); 
    
    /**
     * input 입력값에 따른 검증결과 메세지를 붙인다.
     * @param {HTMLInputElement} targetElement 이벤트 리스너를 추가할 요소.
     * @param {boolean} flag 정규표현식을 통해 검증한다면 해당 매개변수를 사용합니다. null 인 경우, 비밀번호 확인란의 검증이 이뤄집니다. 검증 요소가 추가된다면 추후 수정이 필요합니다.
     * @param {string} matchedMessage 검증 성공 시 메세지 입니다.
     * @param {string} notMatchedMessage 검증 실패 시 메세지 입니다.
     */
    function addValidationTo(targetElement, flag, matchedMessage, notMatchedMessage) {
        targetElement.addEventListener('input', () => {
            // const isPresentCheckMessage = document.querySelector(
            //     `#${targetElement.name}IsPresentCheckMessage`);
            // if (isPresentCheckMessage) isPresentCheckMessage.remove();
            removeElementIfPresent(targetElement, 'IsPresentCheckMessage');
            
            // const resultMessageIsPresent = document.querySelector(
            //     `#${targetElement.name}ResultMessageIsPresent`);
            // if (resultMessageIsPresent) resultMessageIsPresent.remove();
            removeElementIfPresent(targetElement, 'ResultMessageIsPresent');

            // const validationMessageElement = document.querySelector(
            //     `#${targetElement.name}ValidationMessage`);
            // if (validationMessageElement) validationMessageElement.remove();
            removeElementIfPresent(targetElement, 'ValidationMessage');
    
            const message = document.createElement('p');
            message.id = targetElement.name + 'ValidationMessage';
            message.style.margin = '0.7rem 0 0 0.4rem';
    
            targetElement.closest('div').append(message)
    
            case (targetElement)
                if (flag) {
                    message.textContent = matchedMessage;
                    message.style.color = 'green';
                    flag = true;
                } else {
                    message.textContent = notMatchedMessage;
                    message.style.color = 'red';
                    flag = false;
                }
        });
    }
    
});

/**
 * 비밀번호 확인란의 검증 함수입니다.
 * @returns 비밀번호 입력란의 값이 유효하고, 비밀번호, 비밀번호 확인란의 값이 같다면 true 를 반환합니다.
 */
function isValidPasswordCheck(passwordInput, passwordCheckInput, passwordRegex) {
    return passwordInput.value === passwordCheckInput.value
        && passwordRegex.test(passwordInput.value);
}

function isValidValue(targetElement, regex, minLength, maxLength) {
    return regex.test(targetElement.value)
    && targetElement.value.length >= minLength
    && targetElement.value.length <= maxLength;
}

