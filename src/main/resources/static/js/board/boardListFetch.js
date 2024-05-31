async function getBoardList(pageNumber) {
    // TODO page control.
    const url = `/api/v1/boards/${pageNumber}`;

    try {
        const response = await fetch(url, { cache: "no-cache" });
        console.log(response);

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
