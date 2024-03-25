async function getBoard(boardId) {
    console.log(boardId);
    const url = `/api/v1/board/${boardId}`;
    let options = {
        headers: {
            'Authorization': localStorage.getItem('access_token')
        },
    };

    try {

        const response = await fetch(url, options);
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
