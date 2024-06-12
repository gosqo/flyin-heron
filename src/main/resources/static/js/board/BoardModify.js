import { BoardFetcher } from "../libs/board/BoardFetcher.js";
import BoardUtility from "../libs/board/BoardUtility.js";
import { BoardModifyDOM } from "../libs/board/BoardModifyDOM.js";
import AuthChecker from "../libs/token/AuthChecker.js";

window.addEventListener("load", async () => {
    // Spring Security PreAuthorize
    if (!AuthChecker.hasAuth()) {
        AuthChecker.redirectToHome();
    }
    
    const boardFetcher = new BoardFetcher();
    const boardModifyDOM = new BoardModifyDOM();

    const boardId = boardModifyDOM.getBoardId();
    const boardData = await boardFetcher.getBoard(boardId);

    if (boardData === undefined) return;

    boardModifyDOM.placeData(boardData);

    if (BoardUtility.isWriterOf(boardData)) {
        boardModifyDOM.addModifyButton(boardId);
        boardModifyDOM.addCancelButton();
    }
});
