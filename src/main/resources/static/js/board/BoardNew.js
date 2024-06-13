import { BoardNew } from "../libs/board/BoardNew.js";
import AuthChecker from "../libs/token/AuthChecker.js";

document.addEventListener("DOMContentLoaded", async () => {
    if (location.pathname !== "/board/new") return;

    if (AuthChecker.hasAuth()) {
        BoardNew.present();
    }
});
