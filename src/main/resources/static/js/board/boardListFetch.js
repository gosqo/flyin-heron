async function getBoardList(pageNumber) {
    // TODO page control.
    const url = `/api/v1/boards/${pageNumber}`;
    let options = {
        headers: {
            'Authorization': localStorage.getItem('access_token')
        },
    };

    try {

        const response = await fetch(url, options);
        const data = await response.json();
        console.log(data);
        return data;

    } catch (error) {
        console.error('Error ' + error);
    }
}
