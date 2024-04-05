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

    addMessageIfValid(emailInput);
    addMessageIfValid(nicknameInput);

    // Event Listeners
    // 사용자에게 중복 확인 요청 메세지. (isPresent)
    //   입력 이벤트, 입력값의 검증 결과가 참인 경우 ()
    function addMessageIfValid(targetElement) {
        // 입력후 캐릿이 떠나면(blur), 
        targetElement.addEventListener('blur', () => {
            // 중복확인한 내역이 있으면 리턴
            const resultMessageIsPresent = document.querySelector(
                `#${targetElement.name}ResultMessageIsPresent`);
            if (resultMessageIsPresent) {
                return;
            }
            // 매 이벤트 발생마다 할당하는 변수.
            const validationMessageElement =
                document.querySelector(`#${targetElement.name}ValidationMessage`);
            const isPresentCheckMessage = document.createElement('p');
            isPresentCheckMessage.id = `${targetElement.name}IsPresentCheckMessage`;
            isPresentCheckMessage.textContent = `${targetElement.name} 중복 확인을 해주세요.`;
    
            const isPresentCheckMessageElement =
                document.querySelector(
                    `#${targetElement.name}IsPresentCheckMessage`);
            // 이전에 존재하는 isPresentCheckMessageElement 가 있으면 삭제.
            if (isPresentCheckMessageElement) isPresentCheckMessageElement.remove();
            // 해당 인풋 이름의 ValidationMessage 식별자가 존재하고, 
            // 해당 검증이 트루라면 중복확인을 클릭하도록 하는 메세지를 띄운다.
            if (validationMessageElement
                && validationMessageElement.style.color === 'green'
            ) validationMessageElement.closest('div').append(isPresentCheckMessage);
        });
    }
    
    // 이메일, 닉네임이 유일한 값인지 확인. (구현되있음 fetch in signUp.js)
    // 통신 결과에 따라 중복확인 요청한 메세지를 사용 가능, 불가능(통신 결과)을 알리는 메시지로 대체.
    
    
    /**
     * input 요소에 검증 이벤트 리스너를 추가합니다.
     * @param {HTMLInputElement} targetElement 이벤트 리스너를 추가할 요소.
     * @param {RegExp} regex 정규표현식을 통해 검증한다면 해당 매개변수를 사용합니다. null 인 경우, 비밀번호 확인란의 검증이 이뤄집니다. 검증 요소가 추가된다면 추후 수정이 필요합니다.
     * @param {string} matchedMessage 검증 성공 시 메세지 입니다.
     * @param {string} notMatchedMessage 검증 실패 시 메세지 입니다.
     */
    function addValidationTo(targetElement, regex, matchedMessage, notMatchedMessage) {
        targetElement.addEventListener('input', () => {
            const isPresentCheckMessage = document.querySelector(
                `#${targetElement.name}IsPresentCheckMessage`);
            if (isPresentCheckMessage) isPresentCheckMessage.remove();
            
            const resultMessageIsPresent = document.querySelector(
                `#${targetElement.name}ResultMessageIsPresent`);
            if (resultMessageIsPresent) resultMessageIsPresent.remove();

            const validationMessageElement =
                document.querySelector(`#${targetElement.name}ValidationMessage`);
            if (validationMessageElement) validationMessageElement.remove();
    
            const wrapper = targetElement.closest('div');
            const message = document.createElement('p');
            message.id = targetElement.name + 'ValidationMessage';
            message.style.margin = '0.7rem 0 0 0.4rem';
    
            wrapper.append(message)
    
            if (regex !== null) {
                if (regex.test(targetElement.value)) {
                    message.textContent = matchedMessage;
                    message.style.color = 'green';
                } else {
                    message.textContent = notMatchedMessage;
                    message.style.color = 'red';
                }
            } else {
                if (validatePasswordCheck(passwordInput, passwordCheckInput, passwordRegex)) {
                    message.textContent = matchedMessage;
                    message.style.color = 'green';
                } else {
                    message.textContent = notMatchedMessage;
                    message.style.color = 'red';
                }
            }
        });
    }
    
    /**
     * 비밀번호 확인란의 검증 함수입니다.
     * @returns 비밀번호 입력란의 값이 유효하고, 비밀번호, 비밀번호 확인란의 값이 같다면 true 를 반환합니다.
     */
    function validatePasswordCheck(passwordInput, passwordCheckInput, passwordRegex) {
        return passwordInput.value === passwordCheckInput.value
            && passwordRegex.test(passwordInput.value);
    }
});

