package com.vong.manidues.integrated;

import com.vong.manidues.dto.CustomSliceImpl;
import com.vong.manidues.dto.comment.CommentGetResponse;
import com.vong.manidues.global.utility.ObjectMapperUtility;
import com.vong.manidues.repository.BoardRepository;
import com.vong.manidues.repository.CommentRepository;
import com.vong.manidues.repository.MemberRepository;
import com.vong.manidues.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import static com.vong.manidues.global.utility.HttpUtility.buildGetRequestEntity;
import static org.assertj.core.api.Assertions.assertThat;

public class CommentTest extends SpringBootTestBase {
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public CommentTest(
            TestRestTemplate template,
            MemberRepository memberRepository,
            BoardRepository boardRepository,
            CommentRepository commentRepository
    ) {
        super(template);
        this.memberRepository = memberRepository;
        this.boardRepository = boardRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    void initData() {
        member = memberRepository.save(buildMember());
        boards = boardRepository.saveAll(buildBoards());
        comments = commentRepository.saveAll(buildComments());
    }

    @BeforeEach
    @Override
    void setUp() {
        initData();
    }

    @Override
    void tearDown() {
        commentRepository.deleteAll();
        boardRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    void getCommentsSlice() {
        // JSON 으로 직렬화된 응답 본문을 역직렬화, 자바 객체에 매핑
        ObjectMapperUtility.addCustomSliceImplToObjectMapper();

        Long targetBoardId = boards.get(0).getId();
        final String requestUri = String.format("/api/v1/board/%d/comments?page-number=1", targetBoardId);

        RequestEntity<String> request = buildGetRequestEntity(requestUri);

        ResponseEntity<CustomSliceImpl<CommentGetResponse>> response = template.exchange(
                request,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getSize()).isEqualTo(CommentService.NORMAL_COMMENTS_SLICE_SIZE);
    }
}
