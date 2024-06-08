import Fetcher from "../common/Fetcher.js";
import DomCreate from "../dom/DomCreate.js";
import BoardUtility from "./BoardUtility.js";

export default class BoardDOM {
    getBoardId() {
        const path = window.location.pathname.split("/");
        const boardId = path[path.length - 1];
        return boardId;
    }

    placeData(boardData) {
        document.querySelector("#board-id").textContent = boardData.boardId;
        document.querySelector("#board-title").textContent = boardData.title;
        document.querySelector("#board-writer").textContent = boardData.writer;
        document.querySelector("#board-hits").textContent = `조회 ${boardData.viewCount}`;
        document.querySelector("#board-date").textContent = BoardUtility.getRecentBoardDate(boardData);
        document.querySelector("#board-content").textContent = boardData.content;
    }

    addDeleteButton(boardId) {
        const buttonsArea = document.querySelector("#buttons-area");

        const deleteButton = DomCreate.button("delete-btn", "btn btn-primary", "delete");
        deleteButton.addEventListener("click", async () => {
            if (!this.confirmDelete()) {
                alert("게시물 삭제를 취소합니다.");
                return;
            }

            await this.deleteBoard(boardId);
        });
        buttonsArea.append(deleteButton);
    }

    async deleteBoard(boardId) {
        const url = `/api/v1/board/${boardId}`;
        let options = {
            headers: {
                "Authorization": localStorage.getItem("access_token")
            },
            method: `DELETE`
        };

        try {
            const data = await Fetcher.fetchObjectWithAuth(url, options);

            // TODO Response DTO status 필드, 빌더 추가. 여타 REST Response DTO 도 확인 및 적용.
            // 하드코딩 된 메세지로만 처리하기엔 대비하지 못할 경우의 수의 존재 가능성 때문.
            if (data.message === "잘못된 요청입니다.") {
                alert("게시물 삭제에 문제가 발생했습니다.");
                throw new Error("게시물 삭제에 문제 발생.");
            }

            alert(data.message);
            location.replace(`/boards`);
        } catch (error) {
            console.error("Error " + error);
        }
    }

    addModifyButton(boardId) {
        const buttonsArea = document.querySelector("#buttons-area");

        const modifyButton = DomCreate.button("modify-btn", "btn btn-primary", "Modify");
        modifyButton.onclick = () => { location.replace(`/board/${boardId}/modify`); };
        buttonsArea.append(modifyButton);
    }

    confirmDelete() {
        return confirm(
            "게시물을 삭제하시겠습니까?\n"
            + "확인을 누르면 해당 게시물은 삭제되어 복구할 수 없습니다."
        );
    }
}
