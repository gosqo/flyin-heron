import BoardFetcher from "../libs/board/BoardFetcher.js";
import AuthChecker from "../libs/token/AuthChecker.js";
import { BoardListDOM } from "../libs/board/BoardListDOM.js";

window.addEventListener("load", async () => {
    const boardFetcher = new BoardFetcher();
    const boardListDOM = new BoardListDOM();

    if (AuthChecker.hasAuth())
        boardListDOM.addNewBoardButton();

    const uriPageNumber = boardListDOM.getPageNumber();
    const data = await boardFetcher.getBoardList(uriPageNumber);

    if (data === undefined) return;

    const boardPage = data.boardPage;
    const boardPageContent = boardPage.content;
    const boardPageTotalPages = boardPage.totalPages;
    const boardPageNumber = boardPage.number;

    boardPageContent.forEach(board => {
        boardListDOM.createBoardNodes(board);
        boardListDOM.addClickEvent(board.boardId);
    });

    boardListDOM.createPageItemsWrapper(boardPageTotalPages, boardPageNumber);
});
