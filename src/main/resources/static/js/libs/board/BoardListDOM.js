import DomCreate from "../dom/DomCreate.js";
import BoardUtility from "./BoardUtility.js";

export class BoardListDOM {
    addNewBoardButton() {
        const boardListHeader = document.querySelector('#board-list-header');

        const newBoardButton = DomCreate.button('register-board', 'btn btn-primary', 'New Board');
        newBoardButton.onclick = () => { location.href = '/board/new'; };
        boardListHeader.append(newBoardButton);
    }

    getPageNumber() {
        const path = window.location.pathname.split('/');
        const uriPageNumber = path[path.length - 1] === 'boards'
            ? 1
            : parseInt(path[path.length - 1]);
        return uriPageNumber;
    }

    addClickEvent(boardId) {
        const targetNode = document.querySelector(`#board${boardId}`);
        targetNode.addEventListener('mouseover', () => {
            targetNode.style.cursor = 'pointer';
        });
        targetNode.addEventListener('click', () => {
            location.href = `/board/${boardId}`;
        });
    }

    createBoardNodes(board) {
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
        const boardWrapper = DomCreate.division(null, 'card mb-3', null);
        boardListContainer.append(boardWrapper);

        const boardBody = DomCreate.division(`board${board.boardId}`, 'card-body', null);
        boardWrapper.append(boardBody);

        const topWrapper = DomCreate.division(null, 'd-flex justify-content-between', null);
        boardBody.append(topWrapper);

        const boardTitle = DomCreate.division(null, 'card-title', board.title);
        topWrapper.append(boardTitle);

        const boardWriter = DomCreate.paragraph('board-writer', null, board.writer);
        topWrapper.append(boardWriter);

        const boardContentPreview = DomCreate.paragraph(null, 'card-text', this.trimOver150(board.content));
        boardBody.append(boardContentPreview);

        const boardDateWrapper = DomCreate.paragraph(null, 'card-text', null);
        boardBody.append(boardDateWrapper);

        const boardDate = DomCreate.element('small', null, 'text-body-secondary');
        boardDate.textContent = BoardUtility.getRecentBoardDate(board);
        boardDateWrapper.append(boardDate);
    }

    trimOver150(content) {
        return content.length > 151
            ? this.trimIn150(content)
            : content;
    }

    trimIn150(content) {
        return content.substring(0, 150) + '...';
    }

    createPageItemsWrapper(boardPageTotalPages, boardPageNumber) {
        const paginationContainer = document.querySelector('#pagination-container');

        // variables for iteration (start|endNumber)
        const startNumber = boardPageNumber > 1
            ? boardPageNumber - 2
            : 0;

        const endNumber = boardPageNumber + 3 > boardPageTotalPages
            ? boardPageTotalPages
            : boardPageNumber + 3;

        const pagination = DomCreate.element('ul', 'pagination-ul', 'pagination justify-content-center');
        paginationContainer.append(pagination);

        if (boardPageNumber > 2)
            this.addPrevButton(pagination, boardPageNumber);

        for (let i = startNumber; i < endNumber; i++)
            pagination.append(this.createPageItem(i, boardPageNumber));

        if (boardPageNumber < boardPageTotalPages - 3)
            this.addNextButton(pagination, boardPageNumber);

    }

    addPrevButton(pagination, boardPageNumber) {
        const prevItem = DomCreate.element('li', null, 'page-item');
        pagination.append(prevItem);

        const pageLink = DomCreate.anchor(null, 'page-link', `/boards/${boardPageNumber + 1 - 3}`, null);
        prevItem.append(pageLink);

        const prevChar = DomCreate.element('span', null, null);
        prevChar.ariaHidden = 'true';
        prevChar.textContent = '«';
        pageLink.append(prevChar);
    }

    addNextButton(pagination, boardPageNumber) {
        const nextItem = DomCreate.element('li', null, 'page-item');
        pagination.append(nextItem);

        const pageLink = DomCreate.anchor(null, 'page-link', `/boards/${boardPageNumber + 1 + 3}`, null);
        nextItem.append(pageLink);

        const nextChar = DomCreate.element('span', null, null);
        nextChar.ariaHidden = 'true';
        nextChar.textContent = '»';
        pageLink.append(nextChar);
    }

    createPageItem(targetNumber, boardPageNumber) {
        const pageItem = DomCreate.element('li', null, 'page-item');
        const pageLink = DomCreate.anchor(
            null,
            'page-link',
            `/boards/${targetNumber + 1}`,
            targetNumber + 1
        );

        if (boardPageNumber === targetNumber)
            pageLink.classList.add('active');

        pageItem.append(pageLink);

        return pageItem;
    }
}
