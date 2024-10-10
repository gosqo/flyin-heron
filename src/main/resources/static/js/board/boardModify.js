import { BoardModifyView } from "../libs/board/BoardModify.js";
import { Board } from "../libs/board/Board.js";

document.addEventListener("DOMContentLoaded", async () => {
    const boardId = Board.Utility.getBoardId();

    if (location.pathname !== (`/board/${boardId}/modify`)) return;

    BoardModifyView.DOM.present();
});
