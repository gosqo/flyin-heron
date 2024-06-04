import BoardDOM from "../libs/board/BoardDOM.js";
import BoardFetcher from "../libs/board/BoardFetcher.js";
import BoardUtility from "../libs/board/BoardUtility.js";
import AuthChecker from "../libs/token/AuthChecker.js";

window.addEventListener("load", async () => {
    const boardDOM = new BoardDOM();
    const boardFetcher = new BoardFetcher();
    
    const boardId = boardDOM.getBoardId();
    const boardData = await boardFetcher.getBoard(boardId);
    
    if (boardData === undefined) return;

    boardDOM.placeData(boardData);

    if (!AuthChecker.hasAuth()) return;

    if (BoardUtility.isWriterOf(boardData)) {
        boardDOM.addModifyButton(boardId);
        boardDOM.addDeleteButton(boardId);
    }
});
