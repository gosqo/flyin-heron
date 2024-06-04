export default class DateTimeUtility {
    static formatDate(data) {
        // LocalDateTime 형식의 JSON 값을 Date 객체로 변환
        const date = new Date(data);

        // 원하는 형식(yyyy-MM-dd)으로 변환
        const formattedDate = date.getFullYear() + '-' +
            String(date.getMonth() + 1).padStart(2, '0') + '-' +
            String(date.getDate()).padStart(2, '0');

        return formattedDate;
    }

    static gapBetweenDateTimes(later, earlier) {
        const date1 = new Date(later);
        const date2 = new Date(earlier);
        const gap = date1.getTime() - date2.getTime();

        return gap;
    }
}