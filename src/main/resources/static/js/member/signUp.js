import { ValidChecker, UniqueChecker } from "../libs/member/SignUpValidator.js";
import { SignUpDOM } from "../libs/member/SignUpDOM.js";

window.addEventListener("load", () => {
    const signUpDOM = new SignUpDOM();
    const uniqueChecker = new UniqueChecker();

    signUpDOM.addSubmitEvent();
    uniqueChecker.addUniqueEmailEvent();
    uniqueChecker.addUniqueNicknameEvent();

});
