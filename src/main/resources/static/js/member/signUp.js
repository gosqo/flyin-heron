import { SignUpRegex, UniqueCheck } from "../libs/member/SignUpValidator.js";
import { SignUpDOM } from "../libs/member/SignUpDOM.js";

window.addEventListener("load", () => {
    const signUpDOM = new SignUpDOM();
    const uniqueCheck = new UniqueCheck();

    signUpDOM.addSubmitEvent();
    uniqueCheck.addPresentEmailEvent();
    uniqueCheck.addPresentNicknameEvent();

});
