import { Fetcher } from "../common/Fetcher.js";
import FormUtility from "../common/FormUtility.js";
import { DomCreate } from "../dom/DomCreate.js";
import { BoardFetcher } from "./BoardFetcher.js";
import Board from "./Board.js";
import { State } from "../state/StateManage.js";

export class BoardModifyView {
    static DOM = class {
        static async present() {
            const boardId = Board.Utility.getBoardId();
            const boardData = await BoardFetcher.getBoard(boardId);

            if (boardData === undefined) return;

            placeData(boardData);

            if (Board.Utility.isWriterOf(boardData)) {
                addButtons(boardId);
            }

            function placeData(boardData) {
                document.querySelector("#board-id").textContent = boardData.boardId;
                document.querySelector("#board-title").value = boardData.title;
                document.querySelector("#board-writer").value = boardData.writer;
                document.querySelector("#board-date").textContent = Board.Utility.getRecentBoardDate(boardData);
                document.querySelector("#board-content").value = boardData.content;
            }

            function addButtons(boardId) {
                const buttonsArea = document.querySelector("#buttons-area");

                addModifyButton(boardId, buttonsArea);
                addCancelButton(buttonsArea);

                function addModifyButton(boardId, buttonsArea) {
                    const modifyButton = DomCreate.button("modify-btn", "btn btn-primary", "Modify");
                    modifyButton.addEventListener("click", async () => {
                        await modifyBoard(boardId);
                    });
                    buttonsArea.append(modifyButton);

                    async function modifyBoard(boardId) {
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
                }

                function addCancelButton(buttonsArea) {
                    const cancelButton = DomCreate.button("cancel-btn", "btn btn-secondary", "Cancel");
                    cancelButton.addEventListener("click", () => {
                        if (confirmCancel()) {
                            history.back();
                        }
                    });
                    buttonsArea.append(cancelButton);

                    function confirmCancel() {
                        return confirm("수정을 취소하시겠습니까?\n 확인을 클릭 시, 수정 내용을 저장하지 않고 목록으로 이동합니다.");
                    }
                }
            }
        }
    }
}
