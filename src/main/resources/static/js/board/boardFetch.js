async function getBoard(boardId) {
    console.log(boardId);
    const url = `/api/v1/board/${boardId}`;
    let options = {
        headers: {
            'Authorization': localStorage.getItem('access_token')
        },
    };

    try {

        const data = await fetchWithToken(url, options);
        console.log(data);
        return data;

    } catch (error) {
        console.error('Error ' + error);
    }
}
