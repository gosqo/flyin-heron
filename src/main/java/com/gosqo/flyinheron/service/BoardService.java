package com.gosqo.flyinheron.service;

import com.gosqo.flyinheron.domain.Board;
import com.gosqo.flyinheron.domain.Member;
import com.gosqo.flyinheron.dto.board.*;
import com.gosqo.flyinheron.global.exception.ThrowIf;
import com.gosqo.flyinheron.global.utility.AuthHeaderUtility;
import com.gosqo.flyinheron.global.utility.CookieUtility;
import com.gosqo.flyinheron.repository.BoardRepository;
import com.gosqo.flyinheron.repository.MemberRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BoardService {

    public static final int PAGE_SIZE = 3;
    public static final String BOARD_VIEWS_COOKIE_NAME = "bbv";
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final ClaimExtractor claimExtractor;

    private static PageRequest getPageRequest(int pageNumber) {
        pageNumber = pageNumber - 1;
        Sort sort = Sort.by(Sort.Direction.DESC, "registerDate");

        return PageRequest.of(pageNumber, PAGE_SIZE, sort);
    }

    @Transactional
    public BoardPageResponse getBoardPage(int pageNumber) throws NoResourceFoundException {
        PageRequest pageRequest = getPageRequest(pageNumber);
        Page<Board> foundPage = boardRepository.findAll(pageRequest);

        if (foundPage.getContent().isEmpty()) {
            throw new NoResourceFoundException(HttpMethod.GET
                    , "/board?page=" + (foundPage.getPageable().getPageNumber() + 1));
        }

        return BoardPageResponse.of(foundPage);
    }

    public BoardRegisterResponse register(HttpServletRequest request, BoardRegisterRequest requestBody) {
        final String token = AuthHeaderUtility.extractAccessToken(request);
        final String requestUserEmail = claimExtractor.extractUserEmail(token);
        final Member member = memberRepository.findByEmail(requestUserEmail).orElseThrow(
                () -> new NoSuchElementException("No Member present with the email.")
        );
        final Board entity = requestBody.toEntity(member);
        final Board persistedBoard = boardRepository.save(entity);
        return BoardRegisterResponse.builder()
                .id(persistedBoard.getId())
                .message("게시물이 성공적으로 등록됐습니다.")
                .build();
    }

    public BoardUpdateResponse update(Long id, HttpServletRequest request, BoardUpdateRequest requestBody) {
        final String token = AuthHeaderUtility.extractAccessToken(request);
        final String requesterUserEmail = claimExtractor.extractUserEmail(token);
        final Member requester = memberRepository.findByEmail(requesterUserEmail).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 회원의 게시물 update 요청.")
        );
        final Board storedBoard = boardRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 게시물 update 요청.")
        );

        ThrowIf.NotMatchedResourceOwner(requester, storedBoard.getMember().getId());

        storedBoard.updateTitle(requestBody.getTitle());
        storedBoard.updateContent(requestBody.getContent());
        storedBoard.updateUpdateDate();

        Board updatedBoard = boardRepository.save(storedBoard);

        return BoardUpdateResponse.builder()
                .id(updatedBoard.getId())
                .message("게시물 수정이 정상적으로 처리됐습니다.")
                .build();
    }

    public BoardDeleteResponse delete(Long id, HttpServletRequest request) {
        final String token = AuthHeaderUtility.extractAccessToken(request);
        final String requesterUserEmail = claimExtractor.extractUserEmail(token);
        final Member requester = memberRepository.findByEmail(requesterUserEmail).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 회원의 게시물 delete 요청.")
        );
        final Board storedBoard = boardRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 게시물 delete 요청.")
        );

        ThrowIf.NotMatchedResourceOwner(requester, storedBoard.getMember().getId());

        boardRepository.delete(storedBoard);

        return BoardDeleteResponse.builder()
                .message("게시물 삭제가 정상적으로 처리됐습니다.")
                .build();
    }

    @Transactional
    public BoardGetResponse get(
            Long id
            , HttpServletRequest request
            , HttpServletResponse response
    ) throws NoResourceFoundException {
        Board entity = boardRepository.findById(id).orElseThrow(
                () -> new NoResourceFoundException(HttpMethod.GET, request.getRequestURI())
        );

        if (!hasViewed(id, request)) {
            addValueCookieBbv(id, request, response);

            // 게시글 중 조회수가 null 인 게시물 하나를 위한 if.
            if (entity.getViewCount() == null) {
                return BoardGetResponse.of(entity);
            }

            entity.addViewCount(); // throws NPException in previous version.
        }

        return BoardGetResponse.of(entity);
    }

    private void initializeCookieBbv(Long id, HttpServletResponse response) {
        Cookie cookie = new Cookie(BOARD_VIEWS_COOKIE_NAME, id.toString());

        cookie.setMaxAge(60 * 60);
        response.addCookie(cookie);
    }

    private void addValueCookieBbv(Long id, HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();

        if (!CookieUtility.hasCookieNamed(BOARD_VIEWS_COOKIE_NAME, cookies)) {
            initializeCookieBbv(id, response);
            return;
        }

        Cookie cookie = CookieUtility.findCookie(BOARD_VIEWS_COOKIE_NAME, cookies);

        if (CookieUtility.over3500BytesOf(cookie)) {
            CookieUtility.trimFront500Bytes(cookie, '/');
        }

        CookieUtility.appendValue(id, cookie, '/');

        cookie.setMaxAge(60 * 60);
        response.addCookie(cookie);
    }

    private boolean hasViewed(
            Long id
            , HttpServletRequest request
    ) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return false;
        }

        Cookie targetCookie = CookieUtility.findCookie(BOARD_VIEWS_COOKIE_NAME, cookies);

        if (targetCookie == null) {
            return false;
        }

        return CookieUtility.contains(id, targetCookie);
    }
}
