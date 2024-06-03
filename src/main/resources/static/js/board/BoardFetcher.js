export default class BoardFetcher {
    async getBoard(boardId, handle404) {
        const url = `/api/v1/board/${boardId}`;

        try {
            const response = await fetch(url, { cache: "no-cache" });

            if (response.status === 404) {
                handle404.page404(response);
                return;
            }

            return await response.json();
        } catch (error) {
            console.error('Error ' + error);
        }
    }

    async getBoardList(pageNumber, handle404) {
        const url = `/api/v1/boards/${pageNumber}`;

        try {
            const response = await fetch(url, { cache: "no-cache" });

            if (response.status === 404) {
                handle404.page404(response);
                return;
            }

            return await response.json();
        } catch (error) {
            console.error('Error ' + error);
        }
    }
}
