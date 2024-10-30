import { AuthChecker } from "./token/AuthChecker.js";
import { DomCreate } from "./dom/DomCreate.js";
import { Logout } from "./member/Logout.js";
import { TestJwt } from "./token/TestJwt.js";
import { MemberProfileImage } from "./member/MemberProfile.js";

export class IndexDOM {
    testJwt = new TestJwt();

    addAuthDependButtons() {
        if (AuthChecker.hasAuth()) {
            this.addLogoutButton();
            this.addJwtTestButton();
            MemberProfileImage.addImageUploader();

            return;
        }

        this.addLoginButton();
        this.addSignUpButton();
    }

    addLogoutButton() {
        const buttonsArea = document.querySelector("#buttons-area");
        let preferredButtonClassName = localStorage.getItem("themePreference") === "dark"
            ? "btn btn-light me-1"
            : "btn btn-dark me-1";

        const logoutButton = DomCreate.button("logout-button", preferredButtonClassName, "logout");
        logoutButton.addEventListener("click", () => {
            if (Logout.logoutConfirm())
                Logout.logout();
        });
        buttonsArea.append(logoutButton);
    }

    addJwtTestButton() {
        const buttonsArea = document.querySelector("#buttons-area");

        const jwtTestButton = DomCreate.button("jwt-test-button", "btn btn-info me-1", "Test JWT");
        jwtTestButton.onclick = () => this.testJwt.testJwt();
        buttonsArea.append(jwtTestButton);
    }

    addLoginButton() {
        const buttonsArea = document.querySelector("#buttons-area");

        const loginButton = DomCreate.button("login-btn", "btn btn-primary me-1", "Login");
        loginButton.onclick = () => { location.href = "/login"; };
        buttonsArea.append(loginButton);
    }

    addSignUpButton() {
        const buttonsArea = document.querySelector("#buttons-area");

        const signUpButton = DomCreate.button("sign-up-btn", "btn btn-success me-1", "Sign up");
        signUpButton.onclick = () => { location.href = "/signUp"; };
        buttonsArea.append(signUpButton);
    }

    addBoardListButton() {
        const buttonsArea = document.querySelector("#buttons-area");

        const getBoardButton = DomCreate.button("get-board-btn", "btn btn-warning", "Board List");
        getBoardButton.onclick = () => { location.href = "/board"; };
        buttonsArea.append(getBoardButton);
    }
}
