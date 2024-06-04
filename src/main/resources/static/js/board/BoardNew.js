import AuthChecker from "../libs/token/AuthChecker.js";
import { BoardNewDOM } from "../libs/board/BoardRegisterDOM.js";

window.addEventListener('load', async () => {
    const boardNewDOM = new BoardNewDOM();

    if (AuthChecker.hasAuth()) {
        boardNewDOM.addButtons();
    }
});
