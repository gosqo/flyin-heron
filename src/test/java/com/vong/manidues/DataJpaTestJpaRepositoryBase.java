package com.vong.manidues;

import com.vong.manidues.domain.board.Board;
import com.vong.manidues.domain.board.BoardFixture;
import com.vong.manidues.domain.board.BoardRepository;
import com.vong.manidues.domain.comment.Comment;
import com.vong.manidues.domain.comment.CommentRepository;
import com.vong.manidues.domain.member.Member;
import com.vong.manidues.domain.member.MemberRepository;
import com.vong.manidues.domain.member.Role;
import com.vong.manidues.domain.token.TokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

@DataJpaTest(showSql = false)
@ActiveProfiles("test")
@Slf4j
public class DataJpaTestJpaRepositoryBase {
    protected static final int BOARD_COUNT = 3;
    protected static final int COMMENT_COUNT = 20;
    protected final MemberRepository memberRepository;
    protected final TokenRepository tokenRepository;
    protected final BoardRepository boardRepository;
    protected final CommentRepository commentRepository;
    protected Member member;
    protected List<Board> boards;
    protected List<Comment> comments;
    protected Long mainMemberId;
    protected Long mainBoardId;
    protected Long mainCommentId;
    protected long[] boardIds;
    protected long[] commentIds;

    @Autowired
    public DataJpaTestJpaRepositoryBase(
            MemberRepository memberRepository
            , BoardRepository boardRepository
            , CommentRepository commentRepository
            , TokenRepository tokenRepository
    ) {
        this.memberRepository = memberRepository;
        this.boardRepository = boardRepository;
        this.commentRepository = commentRepository;
        this.tokenRepository = tokenRepository;
    }

    private static Member buildMember() {
        return Member.builder()
                .email("check@auth.io")
                .nickname("testOnly")
                .password("$2a$10$.YTh5A02ylk3nhxMltZ0F.fdPp0InH6Sin.w91kve8SEGUYR4KAZ.")
                .role(Role.USER)
                .build();
    }

    private List<Board> buildBoards() {
        List<Board> boards = new ArrayList<>();

        for (int i = 0; i < BOARD_COUNT; i++) {
            Board board = Board.builder()
                    .member(member)
                    .title(BoardFixture.TITLE + i + 1)
                    .content(BoardFixture.CONTENT + i + 1)
                    .build();
            boards.add(board);
        }
        return boards;
    }

    private List<Comment> buildComments() {
        List<Comment> comments = new ArrayList<>();

        for (int i = 0; i < COMMENT_COUNT; i++) {
            Comment comment = Comment.builder()
                    .member(member)
                    .board(boards.get(0))
                    .content("Hello, Comment " + i + 1)
                    .build();
            comments.add(comment);
        }
        return comments;
    }

    @BeforeEach
    void setUp() {
        initializeData();
        log.info("==== Test data initialized. ====");
    }

    @AfterEach
    void tearDown() {
        log.info("==== Deleting test data. ====");
        commentRepository.deleteAll();
        boardRepository.deleteAll();
        tokenRepository.deleteAll();
        memberRepository.deleteAll();
    }

    private void initializeData() {
        member = buildMember();
        Member storedMember = memberRepository.save(member);
        mainMemberId = storedMember.getId();

        boards = buildBoards();
        List<Board> boardList = boardRepository.saveAll(boards);
        boardIds = boardList.stream().mapToLong(Board::getId).toArray();
        mainBoardId = boardIds[0];

        comments = buildComments();
        List<Comment> commentList = commentRepository.saveAll(comments);
        commentIds = commentList.stream().mapToLong(Comment::getId).toArray();
        mainCommentId = commentIds[0];
    }
}
