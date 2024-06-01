window.addEventListener('load', async () => {
    if (_404Flag) return;

    const boardId = getBoardId();
    const boardData = await getBoard(boardId);

    if (boardData === undefined) return;

    const goBackButton = document.querySelector('#go-back-btn');
    goBackButton.onclick = () => { history.back(); };

    fillContents();

    if (tokenCheck()) {

        const accessToken = localStorage.getItem('access_token');
        const decodedJwt = parseJwt(accessToken);
        const userId = decodedJwt.id;

        // create buttons
        if (userId === boardData.writerId) {
            const buttonsArea = document.querySelector('#buttons-area');

            const modifyButton = createButton('modify-btn', 'btn btn-primary', 'Modify');
            buttonsArea.append(modifyButton);

            const deleteButton = createButton('delete-btn', 'btn btn-primary', 'delete');
            buttonsArea.append(deleteButton);
        }

        // get board modify view.
        const modifyButton = document.querySelector('#modify-btn');
        if (modifyButton) {
            modifyButton.addEventListener('click', () => {
                location.replace(`/board/${boardId}/modify`);
            });
        }

        // delete board fetch.
        const deleteButton = document.querySelector('#delete-btn');
        if (deleteButton) {
            deleteButton.addEventListener('click', async () => {
                const confirmation = confirm(
                    '게시물을 삭제하시겠습니까?\n'
                    + '확인을 누르면 해당 게시물은 삭제되어 복구할 수 없습니다.'
                );

                if (!confirmation) {
                    alert('게시물 삭제를 취소합니다.');
                    return;
                }

                await deleteBoard(boardId);
            });
        }
    }

    function getBoardId() {
        const path = window.location.pathname.split('/');
        const boardId = path[path.length - 1];
        return boardId;
    }

    function fillContents() {
        document.querySelector('#board-id').textContent = boardData.boardId;
        document.querySelector('#board-title').textContent = boardData.title;
        document.querySelector('#board-writer').textContent = boardData.writer;
        document.querySelector('#board-hits').textContent = `조회 ${boardData.viewCount}`;
        document.querySelector('#board-date').textContent =
            gapBetweenDateTimes(boardData.updateDate, boardData.registerDate) === 0
                ? formatDate(boardData.registerDate)
                : '수정됨 ' + formatDate(boardData.updateDate);
        document.querySelector('#board-content').textContent = boardData.content;
    }

    async function deleteBoard(id) {
        const url = `/api/v1/board/${id}`;
        let options = {
            headers: {
                'Authorization': localStorage.getItem('access_token')
            },
            method: `DELETE`
        };

        try {

            const data = await fetchWithToken(url, options);
            if (data.message === null) {
                alert("게시물 삭제에 문제가 발생했습니다.");
                throw new Error("게시물 삭제에 문제 발생.");
            }
            alert(data.message);
            location.replace(`/boards`);

        } catch (error) {
            console.error('Error ' + error);
        }
    }
});

function parseJwt(token) {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(window.atob(base64)
        .split('')
        .map(function (c) {
            return '%'
                + ('00' + c.charCodeAt(0).toString(16))
                    .slice(-2);
        })
        .join('')
    );

    return JSON.parse(jsonPayload);
}

function formatDate(data) {

    // LocalDateTime 형식의 JSON 값을 Date 객체로 변환
    const date = new Date(data);

    // 원하는 형식(yyyy-MM-dd)으로 변환
    const formattedDate = date.getFullYear() + '-' +
        String(date.getMonth() + 1).padStart(2, '0') + '-' +
        String(date.getDate()).padStart(2, '0');

    return formattedDate;
}

function gapBetweenDateTimes(later, earlier) {
    const date1 = new Date(later);
    const date2 = new Date(earlier);

    console.log(date1.getTime());
    console.log(date2.getTime());

    const gap = date1.getTime() - date2.getTime();

    return gap;
}
