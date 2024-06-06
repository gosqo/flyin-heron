import { ValidChecker, UniqueChecker } from "../libs/member/SignUpValidator.js";
import { SignUpDOM } from "../libs/member/SignUpDOM.js";

window.addEventListener("load", () => {
    const signUpDOM = new SignUpDOM();
    const uniqueChecker = new UniqueChecker();
    const validChecker = new ValidChecker();

    signUpDOM.addSubmitEvent();
    uniqueChecker.addUniqueEmailEvent();
    uniqueChecker.addUniqueNicknameEvent();
    validChecker.addValidEmailEvent();
    validChecker.addValidPasswordEvent();
    validChecker.addValidPasswordCheckEvent();
    validChecker.addValidNicknameEvent();
    validChecker.addIsSamePasswordEvent();
});
