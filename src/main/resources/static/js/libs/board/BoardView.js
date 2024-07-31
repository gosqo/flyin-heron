import { Fetcher } from "../common/Fetcher.js";
import { BoardFetcher } from "./BoardFetcher.js";
import { DomCreate } from "../dom/DomCreate.js";
import { State } from "../state/StateManage.js";
import AuthChecker from "../token/AuthChecker.js";
import Board from "./Board.js";

export class BoardView {
    static DOM = class {
        static async present() {
            const boardId = Board.Utility.getBoardId();
            const boardData = await BoardFetcher.getBoard(boardId);

            if (boardData === undefined) return;

            placeData(boardData);

            if (!AuthChecker.hasAuth()) return;

            if (Board.Utility.isWriterOf(boardData)) {
                addButtons(boardId);
            }

            function placeData(boardData) {
                document.querySelector("#board-id").textContent = boardData.boardId;
                document.querySelector("#board-title").textContent = boardData.title;
                document.querySelector("#board-writer").textContent = boardData.writer;
                document.querySelector("#board-hits").textContent = `조회 ${boardData.viewCount}`;
                document.querySelector("#board-date").textContent = Board.Utility.getRecentBoardDate(boardData);
                document.querySelector("#board-content").textContent = boardData.content;
            }

            function addButtons(boardId) {
                const buttonsArea = document.querySelector("#buttons-area");

                addModifyButton(boardId, buttonsArea);
                addDeleteButton(boardId, buttonsArea);

                function addDeleteButton(boardId, buttonsArea) {
                    const deleteButton = DomCreate.button("delete-btn", "btn btn-primary", "Delete");

                    deleteButton.addEventListener("click", async () => {
                        if (!confirmDelete()) {
                            alert("게시물 삭제를 취소합니다.");
                            return;
                        }

                        await deleteBoard(boardId);
                    });
                    buttonsArea.append(deleteButton);

                    function confirmDelete() {
                        return confirm(
                            "게시물을 삭제하시겠습니까?\n"
                            + "확인을 누르면 해당 게시물은 삭제되어 복구할 수 없습니다."
                        );
                    }

                    async function deleteBoard(boardId) {
                        const url = `/api/v1/board/${boardId}`;
                        let options = {
                            method: "DELETE"
                            , headers: {
                                "Authorization": localStorage.getItem("access_token")
                            }
                        };

                        try {
                            const data = await Fetcher.withAuth(url, options);

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
                }

                function addModifyButton(boardId, buttonsArea) {
                    const modifyButton = DomCreate.button("modify-btn", "btn btn-primary", "Modify");

                    modifyButton.addEventListener("click", async () => {
                        const pathToGet = `/board/${boardId}/modify`;
                        const authRequiredView = await Fetcher.getAuthRequiredView(pathToGet);

                        State.pushHistory(authRequiredView, pathToGet);
                    });
                    buttonsArea.append(modifyButton);
                }
            }
        }

    }
}
