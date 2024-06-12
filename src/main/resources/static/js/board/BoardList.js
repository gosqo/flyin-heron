import { BoardList } from "../libs/board/BoardList.js";
import AuthChecker from "../libs/token/AuthChecker.js";
import { State } from "../libs/state/StateManage.js";

document.addEventListener("DOMContentLoaded", async () => {
    if (!location.pathname.startsWith("/boards")) return;

    State.replaceCurrentState();

    if (AuthChecker.hasAuth())
        BoardList.DOM.addNewBoardButton();

    BoardList.DOM.presentBoardList();
});

window.addEventListener("popstate", async () => {
    State.replaceCurrentBodyWith(history.state.body);
    State.Event.dispatchDOMContentLoaded();
})
