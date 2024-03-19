window.onload = async () => {
    const path = window.location.pathname.split('/');
    const boardId = path[path.length - 2];
    const buttonsArea = document.querySelector('#buttons-area')
    const accessToken = localStorage.getItem('access_token');
    const decodedJwt = parseJwt(accessToken);
    console.log(decodedJwt);
    const userEmail = decodedJwt.sub;
    console.log(userEmail);
    const userId = decodedJwt.id;
    console.log(userId);

    // fetch board {id}
    const boardData = await getBoard(boardId);
    console.log(boardData);

    if (boardData !== '' || boardData !== undefined) {
        
        document.querySelector('#board-id').textContent = boardData.boardId;
        document.querySelector('#board-title').value = boardData.title;
        document.querySelector('#board-writer').value = boardData.writer;
        // TODO add boardHits on response entity(on server). then unlock below.
        // document.querySelector('#board-hits').textContent = boardData.hits;
        document.querySelector('#board-date').textContent = 
                boardData.registerDate !== boardData.updateDate
                        ? formatDate(boardData.registerDate )
                        : '수정됨 ' + formatDate(boardData.updateDate);
        document.querySelector('#board-content').value = boardData.content;
    
        // buttons DOM
        if (userId === boardData.writerId) {
            // modifyButton
            const modifyButton = document.createElement('button');
    
            modifyButton.id = 'modify-btn';
            modifyButton.className = 'btn btn-primary';
            modifyButton.textContent = 'Modify';
    
            buttonsArea.append(modifyButton);
    
            // cancelButton
            const cancelButton = document.createElement('button');
    
            cancelButton.id = 'cancel-btn';
            cancelButton.className = 'btn btn-secondary';
            cancelButton.textContent = 'cancel';
    
            buttonsArea.append(cancelButton);
    
        }
    
        // modify board fetch.
        const modifyButton = document.querySelector('#modify-btn');
        if (modifyButton) {
            modifyButton.addEventListener(
                'click',
                async () => {
                    const url = `/api/v1/board/${boardId}`;
                    let options = {
                        headers: {
                            'Content-Type': 'application/json',
                            'Authorization': localStorage.getItem('access_token')
                        },
                        method: 'PUT',
                        body: JSON.stringify(formToBody())
                    };

                    try {

                        const data = await fetchWithToken(url, options);
                        console.log(data);
                        if (data.updated === true) {
                            alert(data.message);
                            self.location.href = `/board/${data.id}`;
                        } else {
                            // TODO 서버에서 예외 핸들링 및 응답 메세지 작성. 
                            // input value request Object validation 등
                            alert('게시물 수정에 문제가 발생했습니다.');
                        }

                    } catch (error) {
                        console.error('Error: ', error);
                    }

                }
            );
        }
    }
}

function formToBody() {

    const form = document.querySelector('#form');
    const formData = new FormData(form);
    const body = {};
    formData.forEach((value, key) => {
        body[key] = value;
    });

    return body;
}

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
