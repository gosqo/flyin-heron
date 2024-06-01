window.addEventListener('load', () => {
    const buttonsArea = document.querySelector('#buttons-area');

    addButtons();
    addBoardListButton();

    function addButtons() {

        if (localStorage.getItem('access_token')) {
            const logoutButton = createButton('logoutButton', 'btn btn-primary', 'logout');
            buttonsArea.append(logoutButton);

            const jwtValidationButton = createButton('jwtValidationButton', 'btn btn-primary', 'Test JWT');
            buttonsArea.append(jwtValidationButton);

            return;
        }

        const loginButton = createButton('login-btn', 'btn btn-primary', 'Login');
        buttonsArea.append(loginButton);
        loginButton.addEventListener(
            'click',
            () => self.location.href = '/login'
        );

        const signUpButton = createButton('sign-up-btn', 'btn btn-primary', 'Sign up');
        buttonsArea.append(signUpButton);
        signUpButton.addEventListener(
            'click',
            () => self.location.href = '/signUp'
        );
    }

    function addBoardListButton() {
        const getBoardButton = createButton('get-board-btn', 'btn btn-primary', 'Board List');
        buttonsArea.append(getBoardButton);
        getBoardButton.addEventListener(
            'click',
            () => self.location.href = '/boards'
        );
    }
});
