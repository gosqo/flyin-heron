// nodes
const emailInput = document.querySelector('input[name="email"]');
const passwordInput = document.querySelector('input[name="password"]');
const passwordCheckInput = document.querySelector('input[name="passwordCheck"]');
const nicknameInput = document.querySelector('input[name="nickname"]');

// regular expressions
const emailRegex = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/;
const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[A-Za-z\d~!@#$%^&*()+{}|:"<>?`=\[\]-_\\;']{8,20}$/;
const nicknameRegex = /^[0-9a-zA-Z가-힣]{2,20}$/;

/**
 * 비밀번호 확인란의 검증 함수입니다.
 * @returns 비밀번호 입력란의 값이 유효하고, 비밀번호, 비밀번호 확인란의 값이 같다면 true 를 반환합니다.
 */
function validatePasswordCheck() {
    return passwordInput.value === passwordCheckInput.value 
            && passwordRegex.test(passwordInput.value);
}

// validation messages
const matchedMessage = '적합';
const matchedPasswordCheckMessage = '비밀번호와 일치합니다.';
const notMatchedEmail = 'Email 형식을 확인해주세요.';
const notMatchedPassword = '비밀번호는 영어 대소문자, 숫자, 특수문자를 조합하여 사용할 수 있습니다.\n최소 8자 이상의 문자가 필요합니다.';
const notMatchedPasswordCheck = '비밀번호와 일치하지 않습니다.\n비밀번호를 확인하거나 양식에 맞는 비밀번호 입력을 선행해주세요.';
const notMatchedNickname = '닉네임은 2자 이상 20자 이하의의 한글, 영어로 사용할 수 있습니다.';

// Event Listeners
/**
 * input 요소에 검증 이벤트 리스너를 추가합니다.
 * @param {HTMLInputElement} inputElement 이벤트 리스너를 추가할 요소.
 * @param {RegExp} regex 정규표현식을 통해 검증한다면 해당 매개변수를 사용합니다. null 인 경우, 비밀번호 확인란의 검증이 이뤄집니다. 검증 요소가 추가된다면 추후 수정이 필요합니다.
 * @param {string} matchedMessage 검증 성공 시 메세지 입니다.
 * @param {string} notMatchedMessage 검증 실패 시 메세지 입니다.
 */
async function addValidationTo(inputElement, regex, matchedMessage, notMatchedMessage) {

    inputElement.addEventListener('input', () => {

        const idSelector = `#${inputElement.name}ValidationMessage`;
        const messageElement = document.querySelector(idSelector);

        if (messageElement) messageElement.remove();

        const wrapper = inputElement.closest('div');
        const message = document.createElement('pre');
        message.style.margin = '0.7rem 0 0 0.4rem';

        wrapper.append(message)
        message.id = inputElement.name + 'ValidationMessage';

        if (regex !== null) {
            if (regex.test(inputElement.value)) {
                message.textContent = matchedMessage;
                message.style.color = 'green';
            } else {
                message.textContent = notMatchedMessage;
                message.style.color = 'red';
            }
        } else {
            if (validatePasswordCheck()) {
                message.textContent = matchedMessage;
                message.style.color = 'green';
            } else {
                message.textContent = notMatchedMessage;
                message.style.color = 'red';
            }
        }
    });
}

addValidationTo(
    emailInput,
    emailRegex,
    matchedMessage,
    notMatchedEmail
);

addValidationTo(
    passwordInput,
    passwordRegex,
    matchedMessage,
    notMatchedPassword
);

addValidationTo(
    passwordCheckInput,
    null,
    matchedPasswordCheckMessage,
    notMatchedPasswordCheck
);

addValidationTo(
    nicknameInput,
    nicknameRegex,
    matchedMessage,
    notMatchedNickname
);
