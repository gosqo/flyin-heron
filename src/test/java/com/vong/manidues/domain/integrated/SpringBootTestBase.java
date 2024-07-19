package com.vong.manidues.domain.integrated;

import com.vong.manidues.domain.board.Board;
import com.vong.manidues.domain.board.BoardRepository;
import com.vong.manidues.domain.comment.Comment;
import com.vong.manidues.domain.comment.CommentRepository;
import com.vong.manidues.domain.member.Member;
import com.vong.manidues.domain.member.MemberRepository;
import com.vong.manidues.domain.member.Role;
import com.vong.manidues.domain.token.TokenRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.vong.manidues.domain.member.MemberFixture.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class SpringBootTestBase {
    private final static int BOARDS_SIZE = 10;
    private static final int COMMENTS_SIZE = 20;
    protected final MemberRepository memberRepository;
    protected final TokenRepository tokenRepository;
    protected final BoardRepository boardRepository;
    protected final CommentRepository commentRepository;
    protected final TestRestTemplate template;
    protected Member member;
    protected List<Board> boards;
    protected List<Comment> comments;

    public SpringBootTestBase(
            MemberRepository memberRepository
            , TokenRepository tokenRepository
            , BoardRepository boardRepository
            , CommentRepository commentRepository
            , TestRestTemplate template
    ) {
        this.memberRepository = memberRepository;
        this.tokenRepository = tokenRepository;
        this.boardRepository = boardRepository;
        this.commentRepository = commentRepository;
        this.template = template;
    }

    private void buildMember() {
        member = Member.builder()
                .email(EMAIL)
                .nickname(NICKNAME)
                .password(ENCODED_PASSWORD)
                .role(Role.USER)
                .build();
    }

    private void buildBoards() {
        boards = new ArrayList<>();

        IntStream.range(0, BOARDS_SIZE).forEach(i -> {
            Board e = boardRepository.save(Board.builder()
                    .title("hello " + i)
                    .content("hello Board.")
                    .member(member)
                    .build());
            boards.add(e);
        });
    }

    private void buildComments() {
        comments = new ArrayList<>();

        IntStream.range(0, COMMENTS_SIZE).forEach(i -> {
            Comment e = commentRepository.save(Comment.builder()
                    .content("hello Comment." + i)
                    .member(member)
                    .board(boards.get(0))
                    .build());
            comments.add(e);
        });
    }

    protected void initMember() {
        buildMember();
        memberRepository.save(member);
    }

    protected void initBoards() {
        initMember();
        buildBoards();
        boardRepository.saveAll(boards);
    }

    protected void initComments() {
        initBoards();
        buildComments();
        commentRepository.saveAll(comments);
    }

    @AfterEach
    void tearDown() {
        commentRepository.deleteAll();
        boardRepository.deleteAll();
        tokenRepository.deleteAll();
        memberRepository.deleteAll();
    }
}
