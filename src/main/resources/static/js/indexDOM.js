const buttonsArea = document.querySelector('#buttons-area');

if (localStorage.getItem('access_token')) {
    // logoutButton
    const logoutButton = document.createElement('button');
    
    logoutButton.id = 'logoutButton';
    logoutButton.className = 'btn btn-primary';
    logoutButton.textContent = 'logout';
    
    buttonsArea.append(logoutButton);
    
    // jwtValidationButton
    const jwtValidationButton = document.createElement('button');

    jwtValidationButton.id = 'jwtValidationButton';
    jwtValidationButton.className = 'btn btn-primary';
    jwtValidationButton.textContent = 'Test JWT';

    buttonsArea.append(jwtValidationButton);

    // getBoard
    const getBoardButton = document.createElement('button');
    
    getBoardButton.id = 'get-board-btn';
    getBoardButton.className = 'btn btn-primary';
    getBoardButton.textContent = 'Board List';

    getBoardButton.addEventListener(
        'click',
        () => self.location.href = '/boardList'
    );
    
    buttonsArea.append(getBoardButton);

} else {
    // loginButton
    const loginButton = document.createElement('button');
    
    loginButton.id = 'login-btn';
    loginButton.className = 'btn btn-primary';
    loginButton.textContent = 'Login';

    loginButton.addEventListener(
        'click',
        () => self.location.href = '/login'
    );
    
    buttonsArea.append(loginButton);

    // signUpButton
    const signUpButton = document.createElement('button');
    
    signUpButton.id = 'sign-up-btn';
    signUpButton.className = 'btn btn-primary';
    signUpButton.textContent = 'Sign up';

    signUpButton.addEventListener(
        'click', 
        () => self.location.href = '/signUp'
    );
    
    buttonsArea.append(signUpButton);

}
