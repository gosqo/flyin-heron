import AuthChecker from "../libs/token/AuthChecker.js";
import { BoardList } from "../libs/board/BoardList.js";

window.addEventListener("load", async () => {
    const state = {
        page_name: "boardList"
        , page_url: "/boards/" + BoardList.Utility.getPageNumber()
        , AuthHeaderRequired: false
    }
    history.replaceState(state, "", "");

    if (AuthChecker.hasAuth())
        BoardList.DOM.addNewBoardButton();

    BoardList.DOM.presentBoardList();
});

window.addEventListener("popstate", () => {
    console.log("popstate boardList.js");

    if (history.state.AuthHeaderRequired) {
        console.log(history.state);
        alert("인증이 필요한 접근입니다. 로그인 상태라면 화면의 버튼을 이용해주세요.");
        history.back();
        return;
    }

    if (history.state.page_url !== null)
        location.href = history.state.page_url;
})
