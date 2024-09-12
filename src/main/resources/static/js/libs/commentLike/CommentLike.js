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
        console.log(this.feat, toPrint/*.join(" ")*/);
    }

    request() {
        this.idsToRequest.forEach((id) => {
            this.ajax(id);
        });
    }
}

export class CommentLike {
    static pendingLikesToRegister = new PendingCommentLikes("register", this.registerCommentLike);
    static pendingLikesToDelete = new PendingCommentLikes("delete", this.removeCommentLike);

    static toggleLike(commentId, hasLiked) {
        const commentLikeButton = document.getElementById(`comment-${commentId}-like-button`);
        const commentLikeImage = commentLikeButton.querySelector("img");
        const commentLikeCount = commentLikeButton.nextElementSibling;

        if (commentLikeImage.src.includes("unchecked")) {
            commentLikeImage.src = "/img/icons/checked.png";
            commentLikeCount.textContent = parseInt(commentLikeCount.textContent) + 1;

            if (!hasLiked) {
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
        
        if (hasLiked) {
            this.pendingLikesToDelete.add(commentId);
        }

        if (this.pendingLikesToRegister.contains(commentId)) {
            this.pendingLikesToRegister.remove(commentId);
        }

        this.pendingLikesToRegister.logElements(commentId);
        this.pendingLikesToDelete.logElements(commentId);
    }

    static initPageUnloadHandler() {
        window.addEventListener('beforeunload', () => {

            if (this.pendingLikesToRegister.idsToRequest.size > 0) {
                this.pendingLikesToRegister.request();
            }

            if (this.pendingLikesToDelete.idsToRequest.size > 0) {
                this.pendingLikesToDelete.request();
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
        };

        const data = await Fetcher.withAuth(uri, options);

        return data;
    }
}

