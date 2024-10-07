import { IndexDOM } from "./libs/IndexDOM.js";
import AuthChecker from "./libs/token/AuthChecker.js";
import TokenUtility from "./libs/token/TokenUtility.js";

window.addEventListener("load", () => {
    const indexDOM = new IndexDOM();
    
    // if (AuthChecker.hasAuth()) TokenUtility.removeTokensIfExpired()
    indexDOM.addAuthDependButtons();
    indexDOM.addBoardListButton();
});
