import { LoginDOM } from "../libs/member/LoginDOM.js";

window.addEventListener("load", () => {
    const loginDOM = new LoginDOM();

    loginDOM.addSubmitEvent();
});
