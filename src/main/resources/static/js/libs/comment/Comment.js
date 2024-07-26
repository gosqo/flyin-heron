import { Fetcher } from "../common/Fetcher.js";
import Board from "../board/Board.js";
import TokenUtility from "../token/TokenUtility.js";
import AuthChecker from "../token/AuthChecker.js";

export class Comment {
    static pageNumber = 1;
    static loadedCommentsCount = 0;

    static async remove(id) {
        const url = `/api/v1/comment/${id}`;
        let options = {
            headers: {
                "Authorization": localStorage.getItem("access_token")
            },
            method: "DELETE"
        };
        const data = await Fetcher.withAuth(url, options);

        if (data.status === 200) {
            alert(data.message);
            Comment.DOM.removeComment(id);
            return;
        }

        alert(data.message);
    }

    static async getRegisteredComment(id) {
        const url = `/api/v1/comment/${id}`;

        return await fetch(url)
            .then(response => {
                if (response.ok) {
                    const data = response.json();
                    return data;
                }
                alert("등록한 댓글을 불러오는 중 문제가 발생했습니다.");
                throw Error("response not ok.");
            })
            .then(data => {
                return data;
            })
            .catch(error => console.error(error))
    }

    static async getComments() {
        const boardId = Board.Utility.getBoardId();
        const url = `/api/v1/board/${boardId}/comments?page-number=${Comment.pageNumber}`;
        const options = {
            cache: "no-cache"
        }

        await fetch(url, options)
            .then(response => {
                if (response.ok) {
                    const data = response.json();
                    return data;
                }

                if (response.status === 404) return;

                alert("댓글 불러오는 중 문제가 발생했습니다..");
                throw Error("response not ok.");
            })
            .then(data => {
                if (data === undefined) return;

                Comment.DOM.appendComments(data);
                if (!data.commentPage.last) {
                    Comment.DOM.addLoadMoreCommentButton();
                }
            })
            .catch(error => console.error(error));
    }

    static async register() {
        const boardId = Board.Utility.getBoardId();
        const commentContent = document.querySelector("#comment-post-content");
        const body = {
            "boardId": boardId
            , "content": commentContent.value
        }
        const url = "/api/v1/comment";
        let options = {
            headers: {
                "Content-Type": "application/json",
                "Authorization": localStorage.getItem("access_token")
            },
            method: "POST",
            body: JSON.stringify(body)
        };

        try {
            const data = await Fetcher.withAuth(url, options);

            if (data.message.includes("Validation")) {
                alert(data.errors[0].defaultMessage);
                return;
            }

            if (data.status !== 201) {
                alert(data.message);
                return;
            }

            commentContent.value = '';
            const registeredComment = await Comment.getRegisteredComment(data.comment.id);
            Comment.DOM.appendComment(registeredComment);

            alert(data.message);
        } catch (error) {
            console.error("Error: ", error);
        }
    }

    static Utility = class {
        static isWriterOf(commentData) {
            const decodedJwt = TokenUtility.parseJwt(localStorage.getItem("access_token"));
            const userId = decodedJwt.id;

            if (userId === undefined) {
                throw new Error("No userId detected from token.");
            }

            const writerId = commentData.writerId;

            return userId === writerId;
        }
    }

    static DOM = class {
        static addCommentManageButton(commentUnit, data) {
            const buttonArea = commentUnit.querySelector(".comment-unit-row-1-right-side");
            const dropdown = document.querySelector("#comment-dropdown-unit");
            const clonedDropdown = dropdown.cloneNode(true);

            const modifyButton = clonedDropdown.querySelector("#comment-modify-button");
            const removeButton = clonedDropdown.querySelector("#comment-remove-button");

            clonedDropdown.id = `comment-${data.id}-dropdown`;
            clonedDropdown.hidden = false;
            modifyButton.id = `comment-${data.id}-modify-button`;
            removeButton.id = `comment-${data.id}-remove-button`;

            modifyButton.addEventListener("click", () => {
                Comment.DOM.modifyModal(data); // TODO data 기반 모달 구현.
            });
            removeButton.addEventListener("click", () => {
                Comment.remove(data.id);
            });

            buttonArea.appendChild(clonedDropdown);
        }

        static removeComment(id) {
            document.querySelector(`#comment${id}`).remove();
        }

        static getPresentCommentIds() {
            const arr = document.querySelectorAll(".comments-selector");
            const idList = Array.from(arr).map(node => node.id.split("comment")[1]);
            return idList;
        }

        static checkExistingId() {
            const arr = Comment.DOM.getPresentCommentIds();
            arr.find()
        }

        static addRegisterEvent() {
            const commentRegisterButton = document.querySelector("#comment-register-button");
            commentRegisterButton.addEventListener("click", () => {
                Comment.register();
            });
        }

        static cloneCommentUnit() {
            const commentUnit = document.querySelector("#comment-unit");
            commentUnit.hidden = true;

            return commentUnit.cloneNode(true);
        }

        static appendComment(data) {
            const commentContainer = document.querySelector("#comments-container");
            const firstComment = commentContainer.querySelector(".comments-selector");
            const clonedUnit = Comment.DOM.cloneCommentUnit();

            if (firstComment !== undefined) {
                commentContainer.insertBefore(clonedUnit, firstComment);
                return;
            }

            Comment.DOM.placeData(clonedUnit, data);
            commentContainer.appendChild(clonedUnit);

            if (!AuthChecker.hasAuth) {
                return;
            }

            if (Comment.Utility.isWriterOf(data)) {
                Comment.DOM.addCommentManageButton(clonedUnit, data);
            }
        }

        static appendComments(data) {
            const commentContainer = document.querySelector("#comments-container");
            const presentCommentIds = Comment.DOM.getPresentCommentIds();

            data.commentPage.content.forEach(datum => {
                const fetchedCommentId = datum.id;
                const clonedUnit = Comment.DOM.cloneCommentUnit();

                if (presentCommentIds.some(presentCommentId => parseInt(presentCommentId) === fetchedCommentId)) {
                    // continue; // -> SyntaxError: Illegal continue statement: no surrounding iteration statement / Jump target cannot cross function boundary.ts(1107)
                    return; // forEach() 내부에서 return 은 for 문 continue 역할을 함. continue 사용 불가. 
                }

                Comment.DOM.placeData(clonedUnit, datum);
                commentContainer.appendChild(clonedUnit);

                if (!AuthChecker.hasAuth()) {
                    return;
                }

                if (Comment.Utility.isWriterOf(datum)) {
                    Comment.DOM.addCommentManageButton(clonedUnit, datum);
                }
            });
        }

        static addLoadMoreCommentButton() {
            const loadMoreCommentsButtonDiv = document.querySelector("#load-more-comments-button-area");
            const commentContainer = document.querySelector("#comments-container");
            const clonedDiv = loadMoreCommentsButtonDiv.cloneNode(true);

            clonedDiv.style.display = 'block';
            commentContainer.appendChild(clonedDiv);

            clonedDiv.addEventListener("click", () => {
                clonedDiv.remove();
                ++Comment.pageNumber;
                Comment.getComments();
            });
        }

        static placeData(clonedUnit, data) {
            clonedUnit.hidden = false;
            clonedUnit.id = `comment${data.id}`;
            clonedUnit.className = "comments-selector";

            clonedUnit.querySelector("#comment-writer").textContent = data.writerNickname;
            clonedUnit.querySelector("#comment-date").textContent = Board.Utility.getRecentBoardDate(data);
            clonedUnit.querySelector("#comment-content").textContent = data.content;
        }
    }
}
