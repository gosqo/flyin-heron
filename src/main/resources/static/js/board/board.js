import { BoardView } from "../libs/board/BoardView.js";
import BoardUtility from "../libs/board/BoardUtility.js";
import AuthChecker from "../libs/token/AuthChecker.js";

window.addEventListener("load", async () => {
    BoardView.DOM.present();
});
