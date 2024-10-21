package com.gosqo.flyinheron.acceptance;

import com.gosqo.flyinheron.dto.CustomSliceImpl;
import com.gosqo.flyinheron.dto.comment.CommentGetResponse;
import com.gosqo.flyinheron.global.data.TestDataRemover;
import com.gosqo.flyinheron.repository.BoardRepository;
import com.gosqo.flyinheron.repository.CommentRepository;
import com.gosqo.flyinheron.repository.MemberRepository;
import com.gosqo.flyinheron.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import static com.gosqo.flyinheron.global.utility.HttpUtility.buildGetRequestEntity;
import static org.assertj.core.api.Assertions.assertThat;

public class CommentTest extends SpringBootTestBase {
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public CommentTest(
            TestRestTemplate template
            , MemberRepository memberRepository
            , BoardRepository boardRepository
            , CommentRepository commentRepository
            , TestDataRemover remover
    ) {
        super(template, remover);
        this.memberRepository = memberRepository;
        this.boardRepository = boardRepository;
        this.commentRepository = commentRepository;
    }

    @BeforeEach
    void setUp() {
        member = memberRepository.save(buildMember());
        boards = boardRepository.saveAll(buildBoards());
        comments = commentRepository.saveAll(buildComments());
    }

    @Test
    void getCommentsSlice() {
        Long targetBoardId = boards.get(0).getId();
        final String requestUri = String.format("/api/v1/board/%d/comments?page-number=1", targetBoardId);

        RequestEntity<String> request = buildGetRequestEntity(requestUri);

        ResponseEntity<CustomSliceImpl<CommentGetResponse>> response = template.exchange(
                request,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getSize()).isEqualTo(CommentService.NORMAL_COMMENTS_SLICE_SIZE);
    }
}
