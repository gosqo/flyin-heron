import { Fetcher } from "../common/Fetcher.js";
import Board from "../board/Board.js";
import TokenUtility from "../token/TokenUtility.js";
import AuthChecker from "../token/AuthChecker.js";
import { DomHtml } from "../dom/DomHtml.js";
import { CommentLike } from "../commentLike/CommentLike.js";

export class Comment {
    static pageNumber = 1;
    static loadedCommentsCount = 0;
    static commentLikeManager = new CommentLike();

    static async modify(id) {
        const modifiedContent = document.querySelector("#comment-modify-textarea").value;
        const body = { "content": modifiedContent };
        const url = `/api/v1/comment/${id}`;
        let options = {
            headers: {
                "Authorization": localStorage.getItem("access_token"),
                "Content-Type": "application/json"
            },
            method: "PUT",
            body: JSON.stringify(body)
        }

        return await Fetcher.withAuth(url, options)
    }

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

    static async getComment(id) {
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
                if (data.commentPage.last === false) {
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
            const registeredComment = await Comment.getComment(data.comment.id);
            Comment.DOM.appendJustRegisteredComment(registeredComment);

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
        static modifyTargetCommentId = -1;

        static addModifyButtonInModalEvent() {
            const modifyButton = document.querySelector("#comment-modify-request-button");
            modifyButton.addEventListener("click", async () => {
                const commentUnit = document.querySelector(`#comment${this.modifyTargetCommentId}`);
                const modalCloseButton = document.querySelector("#comment-modify-cancel-button");
                const data = await Comment.modify(this.modifyTargetCommentId);

                if (data.status === 200) {
                    alert(data.message);
                    this.placeDataWhenModified(commentUnit, data);
                    modalCloseButton.dispatchEvent(new Event("click"));

                    return;
                }

                throw Error("Unexpected Exception by developer: after comment modified, response data status is not 200.");
            });
        }

        static async modifyButtonClickHandler(event) {
            const targetComment = event.target;
            const commentId = getCommentIdFromModifyButton(targetComment);
            const commentModifyTextarea = document.querySelector("#comment-modify-textarea");
            const storedComment = await Comment.getComment(commentId);

            commentModifyTextarea.value = storedComment.content;
            this.modifyTargetCommentId = commentId;

            function getCommentIdFromModifyButton(target) {
                const elementId = target.id;
                const commentId = elementId.split("-")[1];
                return commentId;
            }
        }

        static addCommentManageButton(commentUnit, data) {
            const commentId = data.id
            const buttonArea = commentUnit.querySelector(".comment-unit-row-1-right-side");
            const dropdown = document.querySelector("#comment-dropdown-unit");
            const clonedDropdown = dropdown.cloneNode(true);
            const modifyButton = clonedDropdown.querySelector("#comment-modify-button");
            const removeButton = clonedDropdown.querySelector("#comment-remove-button");

            clonedDropdown.hidden = false;
            clonedDropdown.id = `comment-${commentId}-dropdown`;
            modifyButton.id = `comment-${commentId}-modify-button`;
            removeButton.id = `comment-${commentId}-remove-button`;

            modifyButton.addEventListener("click", (event) => {
                this.modifyButtonClickHandler(event);
            });

            removeButton.addEventListener("click", () => {
                const confirmation = confirm("댓글을 삭제하시겠습니까?\n삭제한 댓글은 복구할 수 없습니다.");

                if (confirmation) {
                    Comment.remove(data.id);
                }
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

        static appendJustRegisteredComment(data) {
            const commentContainer = document.querySelector("#comments-container");
            const firstComment = commentContainer.querySelector(".comments-selector");
            const clonedUnit = Comment.DOM.cloneCommentUnit();

            Comment.DOM.placeData(clonedUnit, data);

            if (AuthChecker.hasAuth() && Comment.Utility.isWriterOf(data)) {
                Comment.DOM.addCommentManageButton(clonedUnit, data);
            }

            const commentLikeButton = clonedUnit.querySelector("#comment-like-button");
            commentLikeButton.id = `comment-${data.id}-like-button`;
            commentLikeButton.addEventListener("click", () => {
                CommentLike.toggleLike(data.id, null);
            });

            if (firstComment !== undefined) {
                commentContainer.insertBefore(clonedUnit, firstComment);
                return;
            }

            commentContainer.appendChild(clonedUnit);
        }

        static async appendComment(data) {
            const commentContainer = document.querySelector("#comments-container");
            const clonedUnit = Comment.DOM.cloneCommentUnit();

            Comment.DOM.placeData(clonedUnit, data);
            commentContainer.appendChild(clonedUnit);

            if (AuthChecker.hasAuth() && Comment.Utility.isWriterOf(data)) {
                Comment.DOM.addCommentManageButton(clonedUnit, data);
            }

            const commentLikeButton = clonedUnit.querySelector("#comment-like-button");
            commentLikeButton.id = `comment-${data.id}-like-button`;

            if (!AuthChecker.hasAuth()) {
                commentLikeButton.addEventListener("click", () => {
                    alert("로그인 이후 사용가능합니다.");
                });
                return;
            }

            const hasLiked = await CommentLike.hasLiked(data.id);

            if (hasLiked === true) {
                const commentLikeImage = commentLikeButton.querySelector("img");
                commentLikeImage.src = "/img/icons/checked.png";
            }

            commentLikeButton.addEventListener("click", () => {
                CommentLike.toggleLike(data.id, hasLiked);
            });
        }

        static appendComments(data) {
            const presentCommentIds = Comment.DOM.getPresentCommentIds();

            data.commentPage.content.forEach(datum => {
                const fetchedCommentId = datum.id;

                if (presentCommentIds.some(presentCommentId => parseInt(presentCommentId) === fetchedCommentId)) {
                    // continue; // -> SyntaxError: Illegal continue statement: no surrounding iteration statement / Jump target cannot cross function boundary.ts(1107)
                    return; // forEach() 내부에서 return 은 for 문 continue 역할을 함. continue 사용 불가. 
                }

                this.appendComment(datum);
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

        static placeData(commentUnit, data) {
            const commentContent = commentUnit.querySelector("#comment-content");

            commentUnit.hidden = false;
            commentUnit.id = `comment${data.id}`;
            commentUnit.className = "comments-selector";

            commentUnit.querySelector("#comment-writer").textContent = data.writerNickname;
            commentUnit.querySelector("#comment-date").textContent = Board.Utility.getRecentBoardDate(data);
            commentUnit.querySelector("#comment-like-count").textContent = data.likeCount;
            commentContent.textContent = data.content;
            DomHtml.addHyperLink(commentContent);
        }

        static placeDataWhenModified(commentUnit, data) {
            commentUnit.querySelector("#comment-date").textContent = Board.Utility.getRecentBoardDate(data.updatedComment);
            const commentContent = commentUnit.querySelector("#comment-content");
            commentContent.textContent = data.updatedComment.content;
            DomHtml.addHyperLink(commentContent);
        }
    }
}
