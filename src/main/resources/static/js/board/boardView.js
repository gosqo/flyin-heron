import { BoardView } from "../libs/board/BoardView.js";
import { State } from "../libs/state/StateManage.js";
import Board from "../libs/board/Board.js";
import { Comment } from "../libs/comment/Comment.js";

document.addEventListener("DOMContentLoaded", async () => {
    const boardId = Board.Utility.getBoardId();

    if (location.pathname !== (`/board/${boardId}`)) return;

    State.replaceCurrentState();

    BoardView.DOM.present();
    Comment.DOM.addRegisterEvent();
    Comment.DOM.addModifyButtonInModalEvent();
    Comment.getComments();
});

window.addEventListener("popstate", () => {
    State.replaceCurrentBodyWith(history.state.body);
    State.Event.dispatchDOMContentLoaded();
});
