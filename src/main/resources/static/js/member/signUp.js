import { PatternMatcher, UniqueChecker, IsSameChecker } from "../libs/member/SignUpValidator.js";
import { SignUpDOM } from "../libs/member/SignUpDOM.js";
import { KeyEvent } from "../libs/event/KeyEvent.js";

window.addEventListener("load", () => {
    const signUpDOM = new SignUpDOM();
    const uniqueChecker = new UniqueChecker();
    const patternMatcher = new PatternMatcher();
    const isSameChecker = new IsSameChecker();

    KeyEvent.preventInputsEnterKeyEvent();
    signUpDOM.addSubmitEvent();
    uniqueChecker.addAllIsUniqueEvent();
    patternMatcher.addAllIsMatchedEvent();
    isSameChecker.addAllIsSameEvent();
});
