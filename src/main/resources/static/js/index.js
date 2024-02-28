const logoutButton = document.querySelector('#logout-btn');
const signUpButton = document.querySelector('#sign-up-btn');
const loginButton = document.querySelector('#login-btn');
const tokenValidationTestButton = document.querySelector('#jwt-validation-test').closest('a');

if (access_token) {
    signUpButton.remove();
    loginButton.remove();
} else {
    logoutButton.remove();
    tokenValidationTestButton.remove();
}
