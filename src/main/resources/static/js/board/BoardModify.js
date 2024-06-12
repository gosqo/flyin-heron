import { BoardModifyView } from "../libs/board/BoardModify.js";

document.addEventListener("DOMContentLoaded", async () => {
    const boardId = BoardModifyView.Utility.getBoardId();

    if (location.pathname !== (`/board/${boardId}/modify`)) return;

    BoardModifyView.DOM.present();
});
