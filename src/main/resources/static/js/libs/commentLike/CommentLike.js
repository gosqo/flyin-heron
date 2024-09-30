import { Fetcher } from "../common/Fetcher.js";

class PendingCommentLikes {
    constructor(feat, ajax) {
        this.feat = feat;
        this.ajax = ajax;
        this.idsToRequest = new Set();
    }

    add(id) {
        this.idsToRequest.add(id);
    }

    remove(id) {
        this.idsToRequest.delete(id);
    }

    contains(id) {
        return this.idsToRequest.has(id);
    }

    logElements() {
        const toPrint = new Array();
        this.idsToRequest.forEach(element => {
            toPrint.push(element);
        });
        console.log(this.feat, toPrint);
    }

    request() {
        this.idsToRequest.forEach((id) => {
            if (this.ajax.name === "registerCommentLike") {
                CommentLike.likedCommentIds.add(id);
            }

            if (this.ajax.name === "removeCommentLike") {
                CommentLike.likedCommentIds.delete(id);
            }

            this.ajax(id);
            this.remove(id);
        });
    }
}

export class CommentLike {
    static likedCommentIds = new Set();
    static pendingLikesToRegister = new PendingCommentLikes("register", this.registerCommentLike);
    static pendingLikesToDelete = new PendingCommentLikes("delete", this.removeCommentLike);

    static toggleLike(commentId) {
        const commentLikeButton = document.getElementById(`comment-${commentId}-like-button`);
        const commentLikeImage = commentLikeButton.querySelector("img");
        const commentLikeCount = commentLikeButton.nextElementSibling;

        if (commentLikeImage.src.includes("unchecked")) {
            commentLikeImage.src = "/img/icons/checked.png";
            commentLikeCount.textContent = parseInt(commentLikeCount.textContent) + 1;

            this.addToPendingRegister(commentId);
            
            return;
        }

        commentLikeImage.src = "/img/icons/unchecked.png";
        commentLikeCount.textContent = parseInt(commentLikeCount.textContent) - 1;

        this.addToPendingDelete(commentId);
    }

    static addToPendingDelete(commentId) {
        if (this.likedCommentIds.has(commentId)) {
            this.pendingLikesToDelete.add(commentId);
        }

        if (this.pendingLikesToRegister.contains(commentId)) {
            this.pendingLikesToRegister.remove(commentId);
        }

        this.pendingLikesToRegister.logElements(commentId);
        this.pendingLikesToDelete.logElements(commentId);
    }

    static addToPendingRegister(commentId) {
        if (!this.likedCommentIds.has(commentId)) {
            this.pendingLikesToRegister.add(commentId);
        }

        if (this.pendingLikesToDelete.contains(commentId)) {
            this.pendingLikesToDelete.remove(commentId);
        }

        this.pendingLikesToRegister.logElements(commentId);
        this.pendingLikesToDelete.logElements(commentId);
    }

    static initPageUnloadHandler() {
        window.addEventListener('visibilitychange', () => {
             // 해당 조건 없으면 visibilityState === "visible" (페이지 로드)에도 이벤트 발생
            if (document.visibilityState === "hidden") {
                this.requestRegister();
                this.requestDelete();
            }
        });
    }

    static requestDelete() {
        if (this.pendingLikesToDelete.idsToRequest.size > 0) {
            this.pendingLikesToDelete.request();
        }
    }

    static requestRegister() {
        if (this.pendingLikesToRegister.idsToRequest.size > 0) {
            this.pendingLikesToRegister.request();
        }
    }

    static async hasLiked(commentId) {
        const uri = `/api/v1/comment-like/${commentId}`;
        let options = {
            method: "GET",
            headers: {
                "Authorization": localStorage.getItem("access_token"),
            },
        };

        const data = await Fetcher.withAuth(uri, options);

        return data.hasLike;
    }

    static async registerCommentLike(commentId) {
        const uri = `/api/v1/comment-like/${commentId}`;
        let options = {
            method: "POST",
            headers: {
                "Authorization": localStorage.getItem("access_token"),
            },
            // visibilitychange 이벤트 콜백에서 Fetch API 호출, document.visibilityState === "hidden" 일 경우 추가.
            keepalive: "true"
        };

        // visibility hidden 상태에서 비동기 요청을 여럿 보낼 수 있지만 await 이 붙으면 이후 요청을 보내지 못함.
        // 그렇기 때문에 전송으로 마칠 수 있는 요청만을 해당 이벤트 핸들러로 삼는 것이 올바른 사용법인 것으로 보임.
        // 예외 처리가 불가능
        // 예외 발생 요인을 사전에 차단하고 요청을 보내기만 하도록 함.
        Fetcher.withAuth(uri, options);
    }

    static removeCommentLike(commentId) {
        const uri = `/api/v1/comment-like/${commentId}`;
        let options = {
            method: "DELETE",
            headers: {
                "Authorization": localStorage.getItem("access_token"),
            },
            keepalive: "true"
        };

        Fetcher.withAuth(uri, options);
    }
}
