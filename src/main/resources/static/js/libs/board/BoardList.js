import { DomCreate } from "../dom/DomCreate.js";
import { State } from "../state/StateManage.js";
import { BoardFetcher } from "./BoardFetcher.js";
import { Fetcher } from "../common/Fetcher.js";
import Board from "./Board.js";

export class BoardList {
    static Utility = class {
        static getPageNumber() {
            const path = window.location.pathname.split("/");
            const uriPageNumber = path[path.length - 1] === "boards"
                ? 1
                : parseInt(path[path.length - 1]);
            return uriPageNumber;
        }
    }

    static DOM = class {
        static addNewBoardButton() {
            const boardListHeader = document.querySelector("#board-list-header");
            const newBoardButton = DomCreate.button("register-board", "btn btn-primary", "New Board");

            boardListHeader.append(newBoardButton);
            newBoardButton.addEventListener("click", async () => {
                const pathToGet = "/board/new"
                const authRequiredView = await Fetcher.getAuthRequiredView(pathToGet);

                State.pushHistory(authRequiredView, pathToGet);
            });
        }

        static async presentBoardList() {
            const data = await BoardFetcher.getBoardList();

            if (data === undefined) return;

            const boardPage = data.boardPage;
            const boardPageContent = boardPage.content;
            const boardPageTotalPages = boardPage.totalPages;
            const boardPageNumber = boardPage.number;

            createBoardItems(boardPageContent);
            removeBoardSample();
            createPageItems(boardPageTotalPages, boardPageNumber);

            function createBoardItems(boardPageContent) {
                boardPageContent.forEach(board => {
                    createBoardItem(board);
                });
            }

            function createBoardItem(board) {
                const boardListContainer = document.querySelector("#board-list-container");
                const sampleBoardNode = document.querySelector("#board-sample");

                const clonedBoard = sampleBoardNode.cloneNode(true);
                boardListContainer.append(clonedBoard);

                clonedBoard.id = "board" + board.boardId;
                clonedBoard.querySelector("#board-title").textContent = board.title;
                clonedBoard.querySelector("#board-writer").textContent = board.writer;
                clonedBoard.querySelector("#board-content").textContent = trimOver150(board.content);
                clonedBoard.querySelector("#board-date").textContent = Board.Utility.getRecentBoardDate(board);

                addMouseOverEvent(clonedBoard);
                addClickEvent(clonedBoard);

                function trimOver150(content) {
                    return content.length > 151 ? trimIn150(content) : content;

                    function trimIn150(content) {
                        return content.substring(0, 150) + "...";
                    }
                }

                function addMouseOverEvent(targetNode) {
                    targetNode.addEventListener("mouseover", () => {
                        targetNode.style.cursor = "pointer";
                    });
                }

                function addClickEvent(targetNode) {
                    targetNode.addEventListener("click", () => {
                        location.href = `/board/${board.boardId}`;
                    });
                }
            }

            function removeBoardSample() {
                document.querySelector("#board-sample").remove();
            }

            function createPageItems(boardPageTotalPages, boardPageNumber) {
                // variables for iteration (start|endNumber)
                const startNumber = boardPageNumber > 1
                    ? boardPageNumber - 2
                    : 0;

                const endNumber = boardPageNumber + 3 > boardPageTotalPages
                    ? boardPageTotalPages
                    : boardPageNumber + 3;

                const paginationUl = document.querySelector("#pagination-ul");
                const prevButton = paginationUl.querySelector("#previous-button");
                const nextButton = paginationUl.querySelector("#next-button");

                setPrevButton(boardPageNumber);

                for (let i = startNumber; i < endNumber; i++) {
                    const pageItem = createPageItem(i, boardPageNumber);
                    paginationUl.append(pageItem);
                }
                removePageButtonSample();

                setNextButton(boardPageNumber);

                function setPrevButton(boardPageNumber) {
                    if (boardPageNumber > 2) {
                        prevButton.querySelector("a").href = `/boards/${boardPageNumber + 1 - 3}`
                        return;
                    }
                    prevButton.remove();
                }

                function createPageItem(targetNumber, boardPageNumber) {
                    const presentNumber = targetNumber + 1
                    const pageItem = document.querySelector("#page-button-sample").cloneNode(true);
                    const pageAnchor = pageItem.querySelector("a")

                    pageItem.id = "page" + (presentNumber);
                    pageAnchor.href = `/boards/${presentNumber}`;
                    pageAnchor.textContent = presentNumber;

                    if (boardPageNumber === targetNumber) {
                        pageAnchor.classList.add("active");
                        pageAnchor.removeAttribute("href");
                    }

                    return pageItem;
                }

                function removePageButtonSample() {
                    document.querySelector("#page-button-sample").remove();
                }

                function setNextButton(boardPageNumber) {
                    if (boardPageNumber < boardPageTotalPages - 3) {
                        nextButton.querySelector("a").href = `/boards/${boardPageNumber + 1 + 3}`
                        paginationUl.append(nextButton);
                        return;
                    }
                    nextButton.remove()
                }
            }
        }
    }
}
