import AuthChecker from "../libs/token/AuthChecker.js";
import { BoardNewDOM } from "../libs/board/BoardRegisterDOM.js";

document.addEventListener("DOMContentLoaded", async () => {
    if (location.pathname !== "/board/new") return;

    const boardNewDOM = new BoardNewDOM();

    if (AuthChecker.hasAuth()) {
        boardNewDOM.addButtons();
    }
});
