import BoardFetcher from './BoardFetcher.js';
import BoardUtility from './BoardUtility.js'
import DomCreate from "../domUtils/DomCreate.js";
import Fetcher from "../common/Fetcher.js"
import FormUtility from '../common/FormUtility.js';
import Handle404 from '../exception/handle404.js';

const boardFetcher = new BoardFetcher();
const boardModifyDOM = new BoardModifyDOM();
const handle404 = new Handle404();

window.addEventListener('load', async () => {
    if (handle404._404Flag) return;

    const boardId = boardModifyDOM.getBoardId();
    const boardData = await boardFetcher.getBoard(boardId, handle404);

    if (boardData === undefined) return;

    boardModifyDOM.placeData(boardData);

    if (BoardUtility.isWriterOf(boardData)) {
        boardModifyDOM.addModifyButton(boardId);
        boardModifyDOM.addCancelButton();
    }
});

class BoardModifyDOM {
    getBoardId() {
        const path = window.location.pathname.split('/');
        const boardId = path[path.length - 2];
        return boardId;
    }

    placeData(boardData) {
        document.querySelector('#board-id').textContent = boardData.boardId;
        document.querySelector('#board-title').value = boardData.title;
        document.querySelector('#board-writer').value = boardData.writer;
        document.querySelector('#board-date').textContent =
            boardData.registerDate !== boardData.updateDate
                ? DateTimeUtility.formatDate(boardData.registerDate)
                : '수정됨 ' + DateTimeUtility.formatDate(boardData.updateDate);
        document.querySelector('#board-content').value = boardData.content;
    }

    addModifyButton(boardId) {
        const buttonsArea = document.querySelector('#buttons-area');

        const modifyButton = DomCreate.button('modify-btn', 'btn btn-primary', 'Modify');
        modifyButton.addEventListener('click', async () => {
                await this.modifyBoard(boardId);
            }
        );
        buttonsArea.append(modifyButton);
        return buttonsArea;
    }

    async modifyBoard(boardId) {
        const url = `/api/v1/board/${boardId}`;
        let options = {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': localStorage.getItem('access_token')
            },
            method: 'PUT',
            body: JSON.stringify(FormUtility.formToBody())
        };

        try {
            const data = await Fetcher.withAuth(url, options);

            if (data.id === undefined) {
                alert(data.message);
                return;
            }

            alert(data.message);
            location.replace(`/board/${data.id}`);
        } catch (error) {
            console.error('Error: ', error);
        }
    }

    addCancelButton() {
        const buttonsArea = document.querySelector('#buttons-area');

        const cancelButton = DomCreate.button('cancel-btn', 'btn btn-secondary', 'Cancel');
        cancelButton.addEventListener('click', () => {
            if (this.confirmCancel()) {
                history.back();
            }
        });
        buttonsArea.append(cancelButton);
    }

    confirmCancel() {
        return confirm('수정을 취소하시겠습니까?\n 확인을 클릭 시, 수정 내용을 저장하지 않고 목록으로 이동합니다.');
    }
}
