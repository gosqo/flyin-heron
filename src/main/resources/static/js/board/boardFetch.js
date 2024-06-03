async function getBoard(boardId) {
    const url = `/api/v1/board/${boardId}`;

    try {
        const response = await fetch(url, { cache: "no-cache" });

        if (response.status === 404) {
            page404(response);
            return;
        }

        return await response.json();
    } catch (error) {
        console.error('Error ' + error);
    }
}

async function getBoardList(pageNumber) {
    const url = `/api/v1/boards/${pageNumber}`;

    try {
        const response = await fetch(url, { cache: "no-cache" });

        if (response.status === 404) {
            page404(response);
            return;
        }

        return await response.json();
    } catch (error) {
        console.error('Error ' + error);
    }
}

