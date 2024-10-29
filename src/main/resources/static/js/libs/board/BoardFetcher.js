import { State } from "../state/StateManage.js";
import { BoardList } from "./BoardList.js";
import { Fetcher } from "../common/Fetcher.js";

export class BoardFetcher {
    static async getBoard(boardId) {
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
        const url = `/api/v1/board?page=${pageNumber}`;

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

    static async deleteBoard(boardId) {
        const url = `/api/v1/board/${boardId}`;
        let options = {
            method: "DELETE"
            , headers: {
                "Authorization": localStorage.getItem("access_token")
            }
        };

        try {
            const data = await Fetcher.withAuth(url, options);

            if (data.status !== 200) {
                alert("게시물 삭제에 문제가 발생했습니다.");
                throw new Error("게시물 삭제에 문제 발생.");
            }

            alert(data.message);
            location.replace(`/board`);
        } catch (error) {
            console.error("Error " + error);
        }
    }

}
