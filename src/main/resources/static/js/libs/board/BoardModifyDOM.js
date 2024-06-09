import Fetcher from "../common/Fetcher.js";
import FormUtility from "../common/FormUtility.js";
import DomCreate from "../dom/DomCreate.js";
import BoardUtility from "./BoardUtility.js";

export class BoardModifyDOM {
    getBoardId() {
        const path = window.location.pathname.split("/");
        const boardId = path[path.length - 2];
        return boardId;
    }

    placeData(boardData) {
        document.querySelector("#board-id").textContent = boardData.boardId;
        document.querySelector("#board-title").value = boardData.title;
        document.querySelector("#board-writer").value = boardData.writer;
        document.querySelector("#board-date").textContent = BoardUtility.getRecentBoardDate(boardData);
        document.querySelector("#board-content").value = boardData.content;
    }

    addModifyButton(boardId) {
        const buttonsArea = document.querySelector("#buttons-area");

        const modifyButton = DomCreate.button("modify-btn", "btn btn-primary", "Modify");
        modifyButton.addEventListener("click", async () => {
            await this.modifyBoard(boardId);
        }
        );
        buttonsArea.append(modifyButton);
        return buttonsArea;
    }

    async modifyBoard(boardId) {
        const url = `/api/v1/board/${boardId}`;
        let options = {
            headers: {
                "Content-Type": "application/json",
                "Authorization": localStorage.getItem("access_token")
            },
            method: "PUT",
            body: JSON.stringify(FormUtility.formToBody())
        };

        try {
            const data = await Fetcher.withAuth(url, options);

            if (data.id === undefined) {
                alert(data.message);
                return;
            }

            alert(data.message);
            location.replace(`/board/${data.id}`);
        } catch (error) {
            console.error("Error: ", error);
        }
    }

    addCancelButton() {
        const buttonsArea = document.querySelector("#buttons-area");

        const cancelButton = DomCreate.button("cancel-btn", "btn btn-secondary", "Cancel");
        cancelButton.addEventListener("click", () => {
            if (this.confirmCancel()) {
                history.back();
            }
        });
        buttonsArea.append(cancelButton);
    }

    confirmCancel() {
        return confirm("수정을 취소하시겠습니까?\n 확인을 클릭 시, 수정 내용을 저장하지 않고 목록으로 이동합니다.");
    }
}
