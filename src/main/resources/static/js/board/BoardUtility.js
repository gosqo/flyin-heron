import JwtUtility from '../token/JwtUtility'
import DateTimeUtility from '../common/DateTimeUtility';

export default class BoardUtility {
    static isWriterOf(boardData) {
        const decodedJwt = JwtUtility.parseJwt(localStorage.getItem('access_token'));
        const userId = decodedJwt.id;

        if (userId === undefined) 
            throw new Error("No userId detected from token.");

        const writerId = boardData.writerId;

        return userId === writerId;
    }

    static getRecentBoardDate(data) {
        return DateTimeUtility.gapBetweenDateTimes(data.updateDate, data.registerDate) === 0
                ? DateTimeUtility.formatDate(data.registerDate)
                : '수정됨 ' + DateTimeUtility.formatDate(data.updateDate);
    }
}
