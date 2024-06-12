import { DocumentRewriter } from "../dom/DomRewriter.js";
import { State } from "../state/StateManage.js";
import { BoardList } from "./BoardList.js";

export class BoardFetcher {
    static async getBoard() {
        const boardId = BoardView.Utility.getBoardId();
        const url = `/api/v1/board/${boardId}`;

        try {
            const response = await fetch(url, { cache: "no-cache" });

            if (response.status === 404
                && response.headers.get("Content-Type") === "text/html;charset=UTF-8"
            ) {
                const data = await response.text();
                State.replaceHistory(data, "/404-not-found");
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
                State.replaceHistory(data, "/404-not-found");
                return;
            }

            return await response.json();
        } catch (error) {
            console.error("Error " + error);
        }
    }
}
