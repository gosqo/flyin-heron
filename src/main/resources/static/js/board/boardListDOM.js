window.addEventListener('load', async () => {
    
    const path = window.location.pathname.split('/');
    const uriPageNumber = path[path.length - 1] === ''
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

        const newBoardButton = createElement('button', 'btn btn-primary', 'register-board');
        // const newBoardButton = document.createElement('button');
        // newBoardButton.className = 'btn btn-primary';
        // newBoardButton.id = "register-board";
        newBoardButton.textContent = 'New Board';
        newBoardButton.onclick = () => { self.location.href = '/board/new' };
        
        boardListHeader.append(newBoardButton);
    }

    boardPageContent.forEach(board => {
        console.log(board);
        createBoardNodes(board);
        addClickEvent(board.boardId);
    });

    createPageItemsWrapper(boardPageTotalPages, boardPageNumber);

});

function createPageItem(targetNumber, boardPageNumber) {

    const pageItem = document.createElement('li');
    pageItem.className = 'page-item';
    
        const pageLink = createElement('a', 'page-link');
        // const pageLink = document.createElement('a');
        // pageLink.className = 'page-link';
        pageLink.href = `/boards/${targetNumber + 1}`;
        pageLink.textContent = targetNumber + 1;

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

    const pagination = document.createElement('ul');
    pagination.className = 'pagination justify-content-center';
    pagination.id = 'pagination-ul';

    paginationContainer.append(pagination);

    if (boardPageNumber > 2) {
        const prevItem = document.createElement('li');
        prevItem.className = 'page-item';
    
        pagination.append(prevItem);

        const pageLink = document.createElement('a');
        pageLink.className = 'page-link';
        pageLink.href = `/boards/${boardPageNumber + 1 - 3}`;

        prevItem.append(pageLink);
        
        const prevChar = document.createElement('span');
        prevChar.ariaHidden = 'true';
        prevChar.textContent = '«';

        pageLink.append(prevChar);

    }

    for (i = startNumber; i < endNumber; i++)
    pagination.append(createPageItem(i, boardPageNumber));

    if (boardPageNumber < boardPageTotalPages - 3) {
        const nextItem = document.createElement('li');
        nextItem.className = 'page-item';

        pagination.append(nextItem);

        const pageLink = document.createElement('a');
        pageLink.className = 'page-link';
        pageLink.href = `/boards/${boardPageNumber + 1 + 3}`;

        nextItem.append(pageLink);
        
        const nextChar = document.createElement('span');
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

    const boardWrapper = document.createElement('div');
    boardWrapper.className = 'card mb-3';

    boardListContainer.append(boardWrapper);
    
      const boardBody = document.createElement('div');
      boardBody.id = `board${board.boardId}`;
      boardBody.className = 'card-body';
    
      boardWrapper.append(boardBody);

        const topWrapper = document.createElement('div');
        topWrapper.className = 'd-flex justify-content-between';

        boardBody.append(topWrapper);
    
          const boardTitle = document.createElement('h5');
          boardTitle.className = 'card-title';
          boardTitle.textContent = board.title;
    
          topWrapper.append(boardTitle);

          const boardWriter = document.createElement('p');
          boardWriter.id = 'board-writer';
          boardWriter.textContent = board.writer;

          topWrapper.append(boardWriter);
    
        const boardContent = document.createElement('p');
        boardContent.className = 'card-text';
        const substringContent = board.content.length > 151
            ? board.content.substring(0, 150) + '...'
            : board.content;
        boardContent.textContent = substringContent;
    
        boardBody.append(boardContent);
    
        const boardDateWrapper = document.createElement('p');
        boardDateWrapper.className = 'card-text';
    
        boardBody.append(boardDateWrapper);
    
          const boardDate = document.createElement('small');
          boardDate.className = 'text-body-secondary';
          boardDate.textContent = 
               gapBetweenDateTimes(board.updateDate, board.registerDate) === 0
                  ? formatDate(board.registerDate)
                  : '수정됨 ' + formatDate(board.updateDate);
    
          boardDateWrapper.append(boardDate);
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
