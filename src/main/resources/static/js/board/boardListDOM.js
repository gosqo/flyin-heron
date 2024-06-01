window.addEventListener('load', async () => {
    if (_404Flag) return;
    await getBoards();
});

async function getBoards() {
    const path = window.location.pathname.split('/');
    const uriPageNumber = path[path.length - 1] === 'boards'
        ? 1
        : parseInt(path[path.length - 1]);
    const data = await getBoardList(uriPageNumber);
    console.log(data);
    const boardPage = data.boardPage;
    const boardPageContent = boardPage.content;
    const boardPageTotalPages = boardPage.totalPages;
    const boardPageNumber = boardPage.number;
    console.log(boardPageNumber);

    if (tokenCheck()) {
        const boardListHeader = document.querySelector('#board-list-header');

        const newBoardButton = createButton('register-board', 'btn btn-primary', 'New Board');
        newBoardButton.onclick = () => { location.href = '/board/new'; };

        boardListHeader.append(newBoardButton);
    }

    boardPageContent.forEach(board => {
        console.log(board);
        createBoardNodes(board);
        addClickEvent(board.boardId);
    });

    createPageItemsWrapper(boardPageTotalPages, boardPageNumber);
}

function createPageItem(targetNumber, boardPageNumber) {

    const pageItem = createElement('li', null, 'page-item');

    const pageLink = createAnchor(
        null
        , 'page-link'
        , `/boards/${targetNumber + 1}`
        , targetNumber + 1
    );

    if (boardPageNumber === targetNumber)
        pageLink.classList.add('active');

    pageItem.append(pageLink);

    return pageItem;
}

function createPageItemsWrapper(boardPageTotalPages, boardPageNumber) {

    const paginationContainer = document.querySelector('#pagination-container');

    // variables for iteration (start|endNumber)
    const startNumber = boardPageNumber > 1
        ? boardPageNumber - 2
        : 0;

    const endNumber = boardPageNumber + 3 > boardPageTotalPages
        ? boardPageTotalPages
        : boardPageNumber + 3;

    const pagination = createElement('ul', 'pagination-ul', 'pagination justify-content-center');
    paginationContainer.append(pagination);

    if (boardPageNumber > 2) {
        const prevItem = createElement('li', null, 'page-item');
        pagination.append(prevItem);

        const pageLink = createAnchor(null, 'page-link', `/boards/${boardPageNumber + 1 - 3}`, null);
        prevItem.append(pageLink);

        const prevChar = createElement('span', null, null);
        prevChar.ariaHidden = 'true';
        prevChar.textContent = '«';
        pageLink.append(prevChar);
    }

    for (i = startNumber; i < endNumber; i++)
        pagination.append(createPageItem(i, boardPageNumber));

    if (boardPageNumber < boardPageTotalPages - 3) {
        const nextItem = createElement('li', null, 'page-item');
        pagination.append(nextItem);

        const pageLink = createAnchor(null, 'page-link', `/boards/${boardPageNumber + 1 + 3}`, null);
        nextItem.append(pageLink);

        const nextChar = createElement('span', null, null);
        nextChar.ariaHidden = 'true';
        nextChar.textContent = '»';
        pageLink.append(nextChar);
    }
}

function addClickEvent(boardId) {
    const targetNode = document.querySelector(`#board${boardId}`);
    targetNode.addEventListener('mouseover', () => {
        targetNode.style.cursor = 'pointer';
    });
    targetNode.addEventListener('click', () => {
        self.location.href = `/board/${boardId}`;
    });
}

function createBoardNodes(board) {

    const boardListContainer = document.querySelector('#board-list-container');

    // <div class="card mb-3">
    //   <div class="card-body">
    //     <div class=" d-flex justify-content-between">
    //       <h5 class="card-title" id="board-title">Card title</h5>
    //       <p>writer</p>
    //     </div>
    //     <p class="card-text" id="board-content">This is a wider card with supporting text below as a natural lead-in to
    //       additional content. This content is a little bit longer.</p>
    //     <p class="card-text"><small class="text-body-secondary" id="board-date">Last updated 3 mins ago</small></p>
    //   </div>
    // </div>

    const boardWrapper = createDivision(null, 'card mb-3');
    boardListContainer.append(boardWrapper);

    const boardBody = createDivision(`board${board.boardId}`, 'card-body');
    boardWrapper.append(boardBody);

    const topWrapper = createDivision(null, 'd-flex justify-content-between');
    boardBody.append(topWrapper);

    const boardTitle = createDivision(null, 'card-title');
    boardTitle.textContent = board.title;

    topWrapper.append(boardTitle);

    const boardWriter = createParagraph('board-writer', null, board.writer);
    topWrapper.append(boardWriter);

    const contentPreview = board.content.length > 151
        ? trimIn150(board.content)
        : board.content;
    const boardContentPreview = createParagraph(null, 'card-text', contentPreview);
    boardBody.append(boardContentPreview);

    const boardDateWrapper = createParagraph(null, 'card-text', null);
    boardBody.append(boardDateWrapper);

    const boardDate = document.createElement('small', null, 'text-body-secondary');
    boardDate.textContent =
        gapBetweenDateTimes(board.updateDate, board.registerDate) === 0
            ? formatDate(board.registerDate)
            : '수정됨 ' + formatDate(board.updateDate);
    boardDateWrapper.append(boardDate);

    function trimIn150(content) {
        return content.substring(0, 150) + '...';
    }
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

    const gap = date1.getTime() - date2.getTime();

    return gap;
}
