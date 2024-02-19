const logoutButton = document.querySelector('#logout-btn');
const signUpButton = document.querySelector('#sign-up-btn');
const loginButton = document.querySelector('#login-btn');

if (token) {
    signUpButton.remove();
    loginButton.remove();
} else {
    logoutButton.remove();
}
