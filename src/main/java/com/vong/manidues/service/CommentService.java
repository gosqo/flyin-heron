package com.vong.manidues.service;

import com.vong.manidues.domain.Board;
import com.vong.manidues.domain.Comment;
import com.vong.manidues.domain.CommentLike;
import com.vong.manidues.domain.Member;
import com.vong.manidues.dto.comment.*;
import com.vong.manidues.dto.commentlike.GetCommentsLikedByResponse;
import com.vong.manidues.global.utility.AuthHeaderUtility;
import com.vong.manidues.repository.BoardRepository;
import com.vong.manidues.repository.CommentLikeRepository;
import com.vong.manidues.repository.CommentRepository;
import com.vong.manidues.repository.MemberRepository;
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
    private static final int LIKED_COMMENTS_PAGE_SIZE = 10;
    private static final int NORMAL_COMMENTS_PAGE_SIZE = 6;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final ClaimExtractor claimExtractor;
    private final CommentLikeRepository commentLikeRepository;

    private static PageRequest getNormalCommentsPageRequest(int pageNumber) {
        pageNumber = pageNumber - 1;
        Sort sort = Sort.by(Sort.Direction.ASC, "id");

        return PageRequest.of(pageNumber, NORMAL_COMMENTS_PAGE_SIZE, sort);
    }

    private static PageRequest getLikedCommentsPageRequest(int pageNumber) {
        pageNumber = pageNumber - 1;
        Sort sort = Sort.by(Sort.Direction.ASC, "id");

        return PageRequest.of(pageNumber, LIKED_COMMENTS_PAGE_SIZE, sort);
    }

    /**
     * 회원 자신이 좋아요한 댓글들을 반환. 마이페이지에서 조회
     *
     * @param memberEmail 요청한 멤버의 이메일
     * @param pageNumber  조회할 페이지의 번호
     * @return 좋아요한 코멘트 Slice 가 담긴 DTO
     */
    public GetCommentsLikedByResponse getCommentsLikedBy(String memberEmail, Integer pageNumber) {
        final Member member = memberRepository.findByEmail(memberEmail).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 회원의 댓글 좋아요 여부 조회 요청.")
        );
        final Slice<Comment> comments = commentLikeRepository.findByMemberId(
                        member.getId(), getLikedCommentsPageRequest(pageNumber)
                ).map(CommentLike::getComment);

        return GetCommentsLikedByResponse.builder()
                .comments(comments)
                .build();
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
                .message("댓글 삭제가 정상적으로 처리됐습니다.")
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
                .updatedComment(CommentGetResponse.of(updatedComment))
                .build();
    }

    public CommentRegisterResponse register(HttpServletRequest request, CommentRegisterRequest requestBody) {
        final String token = AuthHeaderUtility.extractJwt(request);
        final String requestUserEmail = claimExtractor.extractUserEmail(token);
        final Member member = memberRepository.findByEmail(requestUserEmail).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 회원의 댓글 등록 요청. (토큰 서명 키 유출 가능성)")
        );
        final Board board = boardRepository.findById(requestBody.getBoardId()).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 게시물에 댓글 등록 요청.")
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
                () -> new NoSuchElementException("존재하지 않는 댓글 조회 요청")));
    }

    public CommentPageResponse getCommentSliceOf(Long boardId, int pageNumber) throws NoResourceFoundException {
        Pageable pageable = getNormalCommentsPageRequest(pageNumber);
        Slice<Comment> found = commentRepository.findByBoardId(boardId, pageable);

        if (found.getContent().isEmpty()) {
            throw new NoResourceFoundException(HttpMethod.GET, "/api/v1/board/{boardId}/comments?page=" + pageNumber);
        }

        return CommentPageResponse.of(found);
    }
}
