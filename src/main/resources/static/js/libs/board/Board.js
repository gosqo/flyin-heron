import { TokenUtility } from "../token/TokenUtility.js"
import { DateTimeUtility } from "../common/DateTimeUtility.js";

export class Board {
    static Utility = class {
        static getBoardId() {
            const modifyViewPathRegex = /^\/board\/\d+\/modify$/;
            const viewPathRegex = /^\/board\/\d+$/;

            if (viewPathRegex.test(location.pathname)) {
                const path = window.location.pathname.split("/");
                const boardId = path[path.length - 1];

                return boardId;
            }

            if (modifyViewPathRegex.test(location.pathname)) {
                const path = window.location.pathname.split("/");
                const boardId = path[path.length - 2];

                return boardId;
            }
        }

        static isWriterOf(boardData) {
            const decodedJwt = TokenUtility.parseJwt(localStorage.getItem("access_token"));
            const userId = decodedJwt.id;

            if (userId === undefined) {
                throw new Error("No userId detected from token.");
            }

            const writerId = boardData.member.id;

            return userId === writerId;
        }

        static getRecentBoardDate(data) {
            return DateTimeUtility.gapBetweenDateTimes(data.updateDate, data.registerDate) === 0
                ? DateTimeUtility.formatDate(data.registerDate)
                : "수정됨 " + DateTimeUtility.formatDate(data.updateDate);
        }
    }
}
