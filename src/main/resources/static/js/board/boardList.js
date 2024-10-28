import { BoardList } from "../libs/board/BoardList.js";
import { AuthChecker } from "../libs/token/AuthChecker.js";
import { State } from "../libs/state/StateManage.js";
import { TokenUtility } from "../libs/token/TokenUtility.js"

document.addEventListener("DOMContentLoaded", async () => {
    if (!location.pathname.startsWith("/board")) return;

    State.replaceCurrentState();

    if (AuthChecker.hasAuth()) {
        try {
            TokenUtility.invalidateRefreshTokenInLocalStorage();
            BoardList.DOM.addNewBoardButton();
        } catch (error) {
            console.error(error);
        }
    }

    BoardList.DOM.presentBoardList();
});

window.addEventListener("popstate", async () => {
    State.replaceCurrentBodyWith(history.state.body);
    State.Event.dispatchDOMContentLoaded();
})
