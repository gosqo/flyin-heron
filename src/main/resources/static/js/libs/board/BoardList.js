import { DomCreate } from "../dom/DomCreate.js";
import { State } from "../state/StateManage.js";
import { BoardFetcher } from "./BoardFetcher.js";
import { Fetcher } from "../common/Fetcher.js";
import { Board } from "./Board.js";
import { MemberProfileImage } from "../member/MemberProfile.js"

export class BoardList {
    static Utility = class {
        static getPageNumber() {
            const parameters = window.location.search.split("?");
            const pageQueryString = parameters.find(item => item.startsWith("page"))

            if (pageQueryString === undefined) {
                return 1;
            }

            const split = pageQueryString.split("=");
            const uriPageNumber = split[split.length - 1] === ""
                ? 1
                : parseInt(split[split.length - 1]);

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
                clonedBoard.querySelector("#board-content").textContent = trimOver150(board.content);
                clonedBoard.querySelector("#board-date").textContent = Board.Utility.getRecentBoardDate(board);
                clonedBoard.querySelector("#board-writer").textContent = board.member.nickname;

                const profileImage = clonedBoard.querySelector("#board-member-profile-image");
                const profileImageContainer = clonedBoard.querySelector(".member-profile-image-container");
                const profileImageData = board.member.profileImage;


                MemberProfileImage.renderProfileImage(profileImageData, profileImageContainer, profileImage)

                addMouseOverEvent(clonedBoard);
                addClickEvent(clonedBoard);

                boardListContainer.removeAttribute("hidden");

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

            function createPageItems(totalPagesCount, currentPageIndex) {
                const lastPageIndex = totalPagesCount - 1;
                const itemsCount = 5;
                // variables for iteration (start|endNumber)
                const startNumber = calStartNumber(currentPageIndex, lastPageIndex, itemsCount);
                const endNumber = calEndNumber(currentPageIndex, startNumber, lastPageIndex, itemsCount);

                const paginationContainer = document.querySelector("#pagination-container");
                const paginationUl = document.querySelector("#pagination-ul");
                const firstPageButton = paginationUl.querySelector("#first-page-button");
                const prevPageButton = paginationUl.querySelector("#previous-page-button");
                const nextPageButton = paginationUl.querySelector("#next-page-button");
                const lastPageButton = paginationUl.querySelector("#last-page-button");

                setFirstPageButton(currentPageIndex);
                setPrevPageButton(currentPageIndex, lastPageIndex);

                for (let i = startNumber; i <= endNumber; i++) {
                    const pageItem = createPageItem(i, currentPageIndex);
                    paginationUl.append(pageItem);
                }
                removePageButtonSample();

                setNextPageButton(currentPageIndex, lastPageIndex);
                setLastPageButton(currentPageIndex, lastPageIndex);

                paginationContainer.removeAttribute("hidden");


                function calStartNumber(current, last, count) {
                    let val;

                    if (current <= 1) {
                        val = 0;
                        return val;
                    }

                    const toLast = last - current;

                    if (toLast <= 2) {
                        val = current - (count - (toLast) - 1);
                        return val;
                    }

                    val = current - 2;
                    return val;
                }

                function calEndNumber(current, start, last, count) {
                    let val;

                    if (last - current <= 2) {
                        val = last;
                        return val;
                    }

                    if (current - start <= 1) {
                        val = start + count - 1;
                        return val;
                    }

                    val = current + 2;
                    return val;
                }

                function setFirstPageButton(currentPageIndex) {
                    if (currentPageIndex > 2) {
                        firstPageButton.querySelector("a").href = `/board?page=1`;
                        return;
                    }

                    firstPageButton.remove();
                }

                function setPrevPageButton(current, last) {

                    if (current === 3) {
                        prevPageButton.querySelector("a").href = `/board?page=${(current + 1) - 3}`;
                        return;
                    }

                    if (current === 4) {
                        prevPageButton.querySelector("a").href = `/board?page=${(current + 1) - 4}`;
                        return;
                    }

                    if (current > 4) {
                        prevPageButton.querySelector("a").href = `/board?page=${(current + 1) - 5}`;
                        return;
                    }

                    prevPageButton.remove();
                }

                function createPageItem(targetIndex, current) {
                    const presentNumber = targetIndex + 1
                    const pageItem = document.querySelector("#page-button-sample").cloneNode(true);
                    const pageAnchor = pageItem.querySelector("a")

                    pageItem.id = "page" + (presentNumber);
                    pageAnchor.href = `/board?page=${presentNumber}`;
                    pageAnchor.textContent = presentNumber;

                    if (current === targetIndex) {
                        pageAnchor.classList.add("active");
                        pageAnchor.removeAttribute("href");
                    }

                    return pageItem;
                }

                function setNextPageButton(current, last) {

                    const toLast = last - current;

                    if (toLast === 3) {
                        nextPageButton.querySelector("a").href = `/board?page=${(current + 1) + 3}`;
                        paginationUl.append(nextPageButton);
                        return;
                    }

                    if (toLast === 4) {
                        nextPageButton.querySelector("a").href = `/board?page=${(current + 1) + 4}`;
                        paginationUl.append(nextPageButton);
                        return;
                    }

                    if (toLast > 4) {
                        nextPageButton.querySelector("a").href = `/board?page=${(current + 1) + 5}`;
                        paginationUl.append(nextPageButton);
                        return;
                    }

                    nextPageButton.remove();
                }

                function setLastPageButton(current, last) {
                    if (last - current > 2) {
                        lastPageButton.querySelector("a").href = `/board?page=${last + 1}`;
                        paginationUl.append(lastPageButton);
                        return;
                    }

                    lastPageButton.remove()
                }

                function removePageButtonSample() {
                    document.querySelector("#page-button-sample").remove();
                }
            }
        }
    }
}
