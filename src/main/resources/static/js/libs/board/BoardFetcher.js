import { DocumentRewriter } from "../dom/DomRewriter.js";
import { BoardList } from "./BoardList.js";

export default class BoardFetcher {
    static async getBoard(boardId) {
        const url = `/api/v1/board/${boardId}`;

        try {
            const response = await fetch(url, { cache: "no-cache" });

            if (response.status === 404
                && response.headers.get("Content-Type") === "text/html;charset=UTF-8"
            ) {
                const data = await response.text();
                DocumentRewriter.rewriteWith(data);
                return;
            }

            return await response.json();
        } catch (error) {
            console.error("Error " + error);
        }
    }

    static async getBoardList() {
        const pageNumber = BoardList.Utility.getPageNumber()
        const url = `/api/v1/boards/${pageNumber}`;

        try {
            const response = await fetch(url, { cache: "no-cache" });

            if (response.status === 404
                && response.headers.get("Content-Type") === "text/html;charset=UTF-8"
            ) {
                const data = await response.text();
                DocumentRewriter.rewriteWith(data);
                return;
            }

            return await response.json();
        } catch (error) {
            console.error("Error " + error);
        }
    }
}
