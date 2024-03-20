window.addEventListener('load', async () => {
    const data = await getBoardList();
    // console.log(data);
    const boardList = data.boardList;

    boardList.forEach(board => {
        console.log(board);
        createBoardNodes(board);
    });
    
});

function createBoardNodes(board) {

    const container = document.querySelector('#container');

    // <div class="card mb-3">
    //   <div class="card-body">
    //     <h5 class="card-title" id="board-title">Card title</h5>
    //     <p class="card-text" id="board-content">This is a wider card with supporting text below as a natural lead-in to additional content. This content is a little bit longer.</p>
    //     <p class="card-text"><small class="text-body-secondary" id="board-date">Last updated 3 mins ago</small></p>
    //   </div>
    // </div>
    
    // TODO Add writer, 
    // <a> tag to href=`/board/${id}` 
    //   or '.card-body'.addEventListener ? 

    const boardWrapper = document.createElement('div');
    boardWrapper.className = 'card mb-3';

    container.append(boardWrapper);
    
      const boardBody = document.createElement('div');
      boardBody.id = `board${board.boardId}`;
      boardBody.className = 'card-body';
    
      boardWrapper.append(boardBody);
    
        const boardTitle = document.createElement('h5');
        boardTitle.className = 'card-title';
        boardTitle.id = 'board-title';
        boardTitle.textContent = board.title;
    
        boardBody.append(boardTitle);
    
        const boardContent = document.createElement('p');
        boardContent.className = 'card-text';
        boardContent.id = 'board-content';
        const substringContent = board.content.length > 151
            ? board.content.substring(0, 150) + '...'
            : board.content;
        boardContent.textContent = substringContent;
    
        boardBody.append(boardContent);
    
        const boardDateWrapper = document.createElement('p');
        boardDateWrapper.className = 'card-text';
        boardDateWrapper.id = 'board-date-wrapper';
    
        boardBody.append(boardDateWrapper);
    
          const boardDate = document.createElement('small');
          boardDate.className = 'text-body-secondary';
          boardDate.id = 'board-date';
          boardDate.textContent = 
              board.registerDate !== board.updateDate
                  ? formatDate(board.registerDate )
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
