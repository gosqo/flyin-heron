import TokenUtility from "../token/TokenUtility.js"
import DateTimeUtility from "../common/DateTimeUtility.js";

export default class Board {
    static Utility = class {
        static isWriterOf(boardData) {
            const decodedJwt = TokenUtility.parseJwt(localStorage.getItem("access_token"));
            const userId = decodedJwt.id;
    
            if (userId === undefined) 
                throw new Error("No userId detected from token.");
    
            const writerId = boardData.writerId;
    
            return userId === writerId;
        }
    
        static getRecentBoardDate(data) {
            return DateTimeUtility.gapBetweenDateTimes(data.updateDate, data.registerDate) === 0
                    ? DateTimeUtility.formatDate(data.registerDate)
                    : "수정됨 " + DateTimeUtility.formatDate(data.updateDate);
        }
    }
}
