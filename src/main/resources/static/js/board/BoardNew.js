import AuthChecker from "../libs/token/AuthChecker.js";
import { BoardNewDOM } from "../libs/board/BoardRegisterDOM.js";

window.addEventListener("load", async () => {
    const boardNewDOM = new BoardNewDOM();

    if (AuthChecker.hasAuth()) {
        boardNewDOM.addButtons();
    }
});

window.addEventListener("popstate", (state) => {
    console.log(state)
    console.log("popstate. boardNew.js");
    location.href = history.state.page_url;
})
