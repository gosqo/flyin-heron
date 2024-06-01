async function getBoardList(pageNumber) {
    // TODO page control.
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
