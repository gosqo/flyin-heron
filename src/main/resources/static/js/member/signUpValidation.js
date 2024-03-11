// nodes
const emailInput = document.querySelector('input[name="email"]');
const passwordInput = document.querySelector('input[name="password"]');
const passwordCheckInput = document.querySelector('input[name="passwordCheck"]');
const nicknameInput = document.querySelector('input[name="nickname"]');

// regular expressions
const emailRegex = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/;
const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,20}$/;

function isValidInput(regex, enteredInput) {
    return regex.test(enteredInput);
}

// create elements
const properEmailSign = document.createElement('p');
properEmailSign.textContent = '적합';
properEmailSign.style.color = 'green';
properEmailSign.id = 'emailValidationSign';

const notProperEmailSign = document.createElement('p');
notProperEmailSign.textContent = 'Email 형식을 확인해주세요.';
notProperEmailSign.style.color = 'red';
notProperEmailSign.id = 'emailValidationSign';

emailInput.addEventListener('blur', (event) => {
    const enteredEmail = event.target.value;

    if (isValidInput(emailRegex, enteredEmail)) {
        if (document.querySelector('#emailValidationSign')) {
            document.querySelector('#emailValidationSign').remove();
        }
        emailInput.closest('div').append(properEmailSign);
    } else {
        if (document.querySelector('#emailValidationSign')) {
            document.querySelector('#emailValidationSign').remove();
        }
        emailInput.closest('div').append(notProperEmailSign);
    }
});
