const logoutButton = document.querySelector('#logout-btn');
const signUpButton = document.querySelector('#sign-up-btn');
const loginButton = document.querySelector('#login-btn');

if (token) {
    signUpButton.remove();
    loginButton.remove();
} else {
    logoutButton.remove();
}

function logoutConfirm(event) {
    const confirmation = confirm('로그아웃 하시겠습니까?')
    if (confirmation) {
        sessionStorage.removeItem('jwt');
    } else {
        event.preventDefault();
    }
}

logoutButton.addEventListener('click', (event) => {
    logoutConfirm(event);
});
