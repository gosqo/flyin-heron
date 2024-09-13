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

            if (!this.likedCommentIds.has(commentId)) {
                this.pendingLikesToRegister.add(commentId);
            }

            if (this.pendingLikesToDelete.contains(commentId)) {
                this.pendingLikesToDelete.remove(commentId);
            }

            this.pendingLikesToRegister.logElements(commentId);
            this.pendingLikesToDelete.logElements(commentId);
            return;
        }

        commentLikeImage.src = "/img/icons/unchecked.png";
        commentLikeCount.textContent = parseInt(commentLikeCount.textContent) - 1;

        if (this.likedCommentIds.has(commentId)) {
            this.pendingLikesToDelete.add(commentId);
        }

        if (this.pendingLikesToRegister.contains(commentId)) {
            this.pendingLikesToRegister.remove(commentId);
        }

        this.pendingLikesToRegister.logElements(commentId);
        this.pendingLikesToDelete.logElements(commentId);
    }

    static initPageUnloadHandler() {
        // window.addEventListener('pagehide', (e) => { // pagehide 의 경우, 해당 블록에서 수행하는 요청이 비교적 늦게 나감.
        window.addEventListener('visibilitychange', () => {
            // e.preventDefault();

            if (document.visibilityState === "hidden") { // 해당 조건 없으면 visibilityState === "visible" (페이지 로드)에도 이벤트 발생
                
                if (this.pendingLikesToRegister.idsToRequest.size > 0) {
                    this.pendingLikesToRegister.request();
                }
                
                if (this.pendingLikesToDelete.idsToRequest.size > 0) {
                    this.pendingLikesToDelete.request();
                }
            }
        });
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

        const data = await Fetcher.withAuth(uri, options);

        return data;
    }

    static async removeCommentLike(commentId) {
        const uri = `/api/v1/comment-like/${commentId}`;
        let options = {
            method: "DELETE",
            headers: {
                "Authorization": localStorage.getItem("access_token"),
            },
            keepalive: "true"
        };

        const data = await Fetcher.withAuth(uri, options);

        return data;
    }
}

