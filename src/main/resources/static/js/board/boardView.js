import { BoardView } from "../libs/board/BoardView.js";
import { State } from "../libs/state/StateManage.js";
import Board from "../libs/board/Board.js";
import { CommentLike } from "../libs/commentLike/CommentLike.js";
import { Comment } from "../libs/comment/Comment.js";
import TokenUtility from "../libs/token/TokenUtility.js";

document.addEventListener("DOMContentLoaded", async () => {
    const boardId = Board.Utility.getBoardId();

    if (location.pathname !== (`/board/${boardId}`)) return;

    State.replaceCurrentState();
    
    TokenUtility.forceReissueAccessToken();

    BoardView.DOM.present();
    Comment.DOM.addRegisterEvent();
    Comment.DOM.addModifyButtonInModalEvent();
    Comment.getComments();
    CommentLike.initPageUnloadHandler();
});

window.addEventListener("popstate", () => {
    State.replaceCurrentBodyWith(history.state.body);
    State.Event.dispatchDOMContentLoaded();
});
