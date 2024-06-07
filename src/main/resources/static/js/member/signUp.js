import { PatternMatcher, UniqueChecker, IsSameChecker } from "../libs/member/SignUpValidator.js";
import { SignUpDOM } from "../libs/member/SignUpDOM.js";

window.addEventListener("load", () => {
    const signUpDOM = new SignUpDOM();
    const uniqueChecker = new UniqueChecker();
    const patternMatcher = new PatternMatcher();
    const isSameChecker = new IsSameChecker();

    signUpDOM.addSubmitEvent();
    uniqueChecker.addAllIsUniqueEvent();
    patternMatcher.addAllIsMatchedEvent();
    isSameChecker.addAllIsSameEvent();
});
