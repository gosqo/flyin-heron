async function getBoardList() {
    // TODO page control.
    const url = `/api/v1/board/`;
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
