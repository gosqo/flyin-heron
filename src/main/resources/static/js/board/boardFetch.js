async function getBoard(boardId) {
    const url = `/api/v1/board/${boardId}`;

    try {
        const response = await fetch(url);

        if (response.status === 404) {
            location.href = '/error/404';
        } else {
            const data = await response.json();
            console.log(data);
            return data;
        }
    } catch (error) {
        console.error('Error ' + error);
    }
}
