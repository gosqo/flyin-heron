if (localStorage.getItem('access_token')) {
    // logoutButton
    const logoutButtonWrapper = document.createElement('a');
    const logoutButton = document.createElement('button');
    
    logoutButtonWrapper.setAttribute('href', '#');
    logoutButton.id = 'logoutButton';
    logoutButton.className = 'btn';
    logoutButton.textContent = 'logout';
    
    document.body.append(logoutButtonWrapper);
    logoutButtonWrapper.append(logoutButton);
    
    // jwtValidationButton
    const jwtValidationButtonWrapper = document.createElement('a');
    const jwtValidationButton = document.createElement('button');

    jwtValidationButtonWrapper.setAttribute('href', '#');
    jwtValidationButton.id = 'jwtValidationButton';
    jwtValidationButton.className = 'btn';
    jwtValidationButton.textContent = 'test jwt';

    document.body.append(jwtValidationButtonWrapper);
    jwtValidationButtonWrapper.append(jwtValidationButton);

} else {
    // loginButton
    const loginButtonWrapper = document.createElement('a');
    const loginButton = document.createElement('button');
    
    loginButtonWrapper.setAttribute('href', '/login');
    loginButton.id = 'login-btn';
    loginButton.className = 'btn';
    loginButton.textContent = 'login';
    
    document.body.append(loginButtonWrapper);
    loginButtonWrapper.append(loginButton);

    // signUpButton
    const signUpButtonWrapper = document.createElement('a');
    const signUpButton = document.createElement('button');
    
    signUpButtonWrapper.setAttribute('href', '/signUp');
    signUpButton.id = 'sign-up-btn';
    signUpButton.className = 'btn';
    signUpButton.textContent = 'sign up';
    
    document.body.append(signUpButtonWrapper);
    signUpButtonWrapper.append(signUpButton);

}
