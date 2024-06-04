import AuthChecker from "./token/AuthChecker.js";
import DomCreate from "./dom/DomCreate.js";
import Logout from "./member/Logout.js";
import TestJwt from "./token/TestJwt.js";

export class IndexDOM {
    logout = new Logout();
    testJwt = new TestJwt();

    addAuthDependButtons() {
        if (AuthChecker.hasAuth()) {
            this.addLogoutButton();
            this.addJwtTestButton();

            return;
        }

        this.addLoginButton();
        this.addSignUpButton();
    }

    addLogoutButton() {
        const buttonsArea = document.querySelector('#buttons-area');

        const logoutButton = DomCreate.button('logout-button', 'btn btn-primary', 'logout');
        logoutButton.addEventListener('click', () => {
            if (this.logout.logoutConfirm())
                this.logout.fetchLogout();
        });
        buttonsArea.append(logoutButton);
    }

    addJwtTestButton() {
        const buttonsArea = document.querySelector('#buttons-area');

        const jwtTestButton = DomCreate.button('jwt-test-button', 'btn btn-primary', 'Test JWT');
        jwtTestButton.onclick = () => this.testJwt.testJwt();
        buttonsArea.append(jwtTestButton);
    }

    addLoginButton() {
        const buttonsArea = document.querySelector('#buttons-area');

        const loginButton = DomCreate.button('login-btn', 'btn btn-primary', 'Login');
        loginButton.onclick = () => { location.href = '/login'; };
        buttonsArea.append(loginButton);
    }

    addSignUpButton() {
        const buttonsArea = document.querySelector('#buttons-area');

        const signUpButton = DomCreate.button('sign-up-btn', 'btn btn-primary', 'Sign up');
        signUpButton.onclick = () => { location.href = '/signUp'; };
        buttonsArea.append(signUpButton);
    }

    addBoardListButton() {
        const buttonsArea = document.querySelector('#buttons-area');

        const getBoardButton = DomCreate.button('get-board-btn', 'btn btn-primary', 'Board List');
        getBoardButton.onclick = () => { location.href = '/boards'; };
        buttonsArea.append(getBoardButton);
    }
}
