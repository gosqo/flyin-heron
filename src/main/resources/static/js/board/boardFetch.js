async function getBoard(boardId) {
    const url = `/api/v1/board/${boardId}`;

    try {
        const response = await fetch(url);

        if (response.status === 404) {
            await page404(response);
        } else if (response.status === 500) {
            location.href = '/error';
        } else {
            return await response.json();
        }
    } catch (error) {
        console.error('Error ' + error);
    }
}
