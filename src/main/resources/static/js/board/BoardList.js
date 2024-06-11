import { BoardList } from "../libs/board/BoardList.js";
import AuthChecker from "../libs/token/AuthChecker.js";
import { State } from "../libs/state/StateManage.js";

document.addEventListener("DOMContentLoaded", async () => {
    if (!location.pathname.startsWith("/boards")) return;

    const state = {
        pathname: location.pathname + "/" + BoardList.Utility.getPageNumber()
        , body: document.querySelector("body").outerHTML
        , AuthHeaderRequired: false
    }
    history.replaceState(state, "", "");

    if (AuthChecker.hasAuth())
        BoardList.DOM.addNewBoardButton();

    BoardList.DOM.presentBoardList();
});

window.addEventListener("popstate", async () => {
    State.Body.replaceCurrentBodyWith(history.state.body);
    State.Event.dispatchDOMContentLoaded();
})
