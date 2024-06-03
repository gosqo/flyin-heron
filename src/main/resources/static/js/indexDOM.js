window.addEventListener('load', () => {
    addAuthDependButtons();
    addBoardListButton();
});

function addAuthDependButtons() {
    if (hasAuth()) {
        addLogoutButton();
        addJwtTestButton();

        return;
    }

    addLoginButton();
    addSignUpButton();
}

function addLogoutButton() {
    const buttonsArea = document.querySelector('#buttons-area');

    const logoutButton = createButton('logout-button', 'btn btn-primary', 'logout');
    logoutButton.addEventListener('click', () => {
        if (logoutConfirm())
            fetchLogout();
    });
    buttonsArea.append(logoutButton);
}

function addJwtTestButton() {
    const buttonsArea = document.querySelector('#buttons-area');

    const jwtTestButton = createButton('jwt-test-button', 'btn btn-primary', 'Test JWT');
    jwtTestButton.onclick = () => testJwt();
    buttonsArea.append(jwtTestButton);
}

function addLoginButton() {
    const buttonsArea = document.querySelector('#buttons-area');

    const loginButton = createButton('login-btn', 'btn btn-primary', 'Login');
    loginButton.onclick = () => { location.href = '/login'; };
    buttonsArea.append(loginButton);
}

function addSignUpButton() {
    const buttonsArea = document.querySelector('#buttons-area');

    const signUpButton = createButton('sign-up-btn', 'btn btn-primary', 'Sign up');
    signUpButton.onclick = () => { location.href = '/signUp'; };
    buttonsArea.append(signUpButton);
}

function addBoardListButton() {
    const buttonsArea = document.querySelector('#buttons-area');

    const getBoardButton = createButton('get-board-btn', 'btn btn-primary', 'Board List');
    getBoardButton.onclick = () => { location.href = '/boards'; };
    buttonsArea.append(getBoardButton);
}    
