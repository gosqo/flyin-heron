import { BoardView } from "../libs/board/BoardView.js";
import { State } from "../libs/state/StateManage.js";
import Board from "../libs/board/Board.js";

document.addEventListener("DOMContentLoaded", async () => {
    const boardId = Board.Utility.getBoardId();

    if (location.pathname !== (`/board/${boardId}`)) return;

    State.replaceCurrentState();

    BoardView.DOM.present();
});

window.addEventListener("popstate", () => {
    State.replaceCurrentBodyWith(history.state.body);
    State.Event.dispatchDOMContentLoaded();
});
