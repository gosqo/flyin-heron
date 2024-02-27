const logoutButton = document.querySelector('#logout-btn');
const signUpButton = document.querySelector('#sign-up-btn');
const loginButton = document.querySelector('#login-btn');
const validationButton = document.querySelector('#jwt-validation-test');

if (access_token) {
    signUpButton.remove();
    loginButton.remove();
} else {
    logoutButton.remove();
}
