export default class BoardFetcher {
    async getBoard(boardId) {
        const url = `/api/v1/board/${boardId}`;

        try {
            const response = await fetch(url, { cache: "no-cache" });

            if (response.status === 404) {
                await this.rewriteDocument(response);
                return;
            }

            return await response.json();
        } catch (error) {
            console.error('Error ' + error);
        }
    }

    async getBoardList(pageNumber) {
        const url = `/api/v1/boards/${pageNumber}`;

        try {
            const response = await fetch(url, { cache: "no-cache" });

            if (response.status === 404) {
                await this.rewriteDocument(response);
                return;
            }

            return await response.json();
        } catch (error) {
            console.error('Error ' + error);
        }
    }

    /**
     * 기존 HTML 문서를 서버가 보낸 HTML 문서로 대체.
     * 해당 문서로의 완전한 대체로 기존 스크립트 대체 및 실행.
     * @param {Promise} response Response body 에 HTML 문서를 가진 Promise 객체. 
     */
    async rewriteDocument(response) {
        const newPage = await response.text();
        document.open();
        document.write(newPage);
        document.close();
    }
}
