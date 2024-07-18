package com.vong.manidues.domain.comment;

import com.vong.manidues.domain.board.Board;
import com.vong.manidues.domain.board.BoardRepository;
import com.vong.manidues.domain.comment.dto.*;
import com.vong.manidues.domain.member.Member;
import com.vong.manidues.domain.member.MemberRepository;
import com.vong.manidues.domain.token.ClaimExtractor;
import com.vong.manidues.global.utility.AuthHeaderUtility;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final ClaimExtractor claimExtractor;

    static final int PAGE_SIZE = 6;

    private static PageRequest getPageRequest(int pageNumber) {
        pageNumber = pageNumber - 1;
        Sort sort = Sort.by(Sort.Direction.ASC, "id");

        return PageRequest.of(pageNumber, PAGE_SIZE, sort);
    }

    public CommentDeleteResponse remove(Long id, HttpServletRequest request) {
        final String token = AuthHeaderUtility.extractJwt(request);
        final String requestUserEmail = claimExtractor.extractUserEmail(token);
        final Comment storedComment = commentRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 게시물 delete 요청.")
        );

        if (!storedComment.getMember().getEmail().equals(requestUserEmail))
            throw new AccessDeniedException("요청자와 저작자의 불일치");

        commentRepository.delete(storedComment);

        return CommentDeleteResponse.builder()
                .status(200)
                .message("게시물 삭제가 정상적으로 처리됐습니다.")
                .build();
    }

    public CommentUpdateResponse modify(Long id, HttpServletRequest request, CommentUpdateRequest requestBody) {
        final String token = AuthHeaderUtility.extractJwt(request);
        final String requestUserEmail = claimExtractor.extractUserEmail(token);
        final Comment storedComment = commentRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 댓글 수정 요청.")
        );

        if (!storedComment.getMember().getEmail().equals(requestUserEmail)) {
            throw new AccessDeniedException("요청자와 저작자의 불일치");
        }

        storedComment.updateContent(requestBody.getContent());
        storedComment.updateUpdateDate();

        Comment updatedComment = commentRepository.save(storedComment);

        return CommentUpdateResponse.builder()
                .status(200)
                .message("댓글을 수정했습니다.")
                .updatedContent(updatedComment.getContent())
                .build();
    }

    public CommentRegisterResponse register(HttpServletRequest request, CommentRegisterRequest requestBody) {
        final String token = AuthHeaderUtility.extractJwt(request);
        final String requestUserEmail = claimExtractor.extractUserEmail(token);
        final Member member = memberRepository.findByEmail(requestUserEmail).orElseThrow(
                () -> new NoSuchElementException("Member not exist with the email.")
        );
        final Board board = boardRepository.findById(requestBody.getBoardId()).orElseThrow(
                () -> new NoSuchElementException("Board Not Exist.")
        );

        Comment comment = commentRepository.save(Comment.builder()
                .member(member)
                .board(board)
                .content(requestBody.getContent())
                .build());

        return CommentRegisterResponse.builder()
                .status(201)
                .message("댓글을 등록했습니다.")
                .comment(CommentGetResponse.of(comment))
                .build();
    }

    public CommentGetResponse get(Long id) {
        return CommentGetResponse.of(commentRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("Request GET comment not exist")));
    }

    public CommentPageResponse getCommentSliceOf(Long boardId, int pageNumber) throws NoResourceFoundException {
        Pageable pageable = getPageRequest(pageNumber);
        Slice<Comment> found = commentRepository.findByBoardId(boardId, pageable);

        if (found.getContent().isEmpty()) {
            throw new NoResourceFoundException(HttpMethod.GET, "/api/v1/board/{boardId}/comments?page=" + pageNumber);
        }

        return CommentPageResponse.of(found);
    }
}
