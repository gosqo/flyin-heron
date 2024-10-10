import { BoardNew } from "../libs/board/BoardNew.js";
import { KeyEvent } from "../libs/event/KeyEvent.js";
import { AuthChecker } from "../libs/token/AuthChecker.js";

document.addEventListener("DOMContentLoaded", async () => {
    if (location.pathname !== "/board/new") return;

    KeyEvent.preventInputsEnterKeyEvent();

    if (AuthChecker.hasAuth()) {
        BoardNew.present();
    }
});
