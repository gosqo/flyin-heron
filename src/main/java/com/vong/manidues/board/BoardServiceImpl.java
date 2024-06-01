package com.vong.manidues.board;

import com.vong.manidues.board.dto.*;
import com.vong.manidues.cookie.CookieUtility;
import com.vong.manidues.member.Member;
import com.vong.manidues.member.MemberRepository;
import com.vong.manidues.utility.AuthHeaderUtility;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final CookieUtility cookieUtility;
    private final AuthHeaderUtility authHeaderUtility;

    private static final String BOARDS_BEEN_VIEWED = "bbv";

    @Override
    public BoardPageResponse getBoardPage(int pageNumber) throws NoResourceFoundException {
        PageRequest pageRequest = getPageRequest(pageNumber);
        Page<Board> foundPage = boardRepository.findAll(pageRequest);

        if (foundPage.getTotalPages() - 1 < pageRequest.getPageNumber())
            throw new NoResourceFoundException(HttpMethod.GET, "/boards/" + (foundPage.getPageable().getPageNumber() + 1));

        return BoardPageResponse.fromEntityPage(foundPage);
    }

    @Override
    public BoardRegisterResponse register(HttpServletRequest request, BoardRegisterRequest requestBody) {
        String requestUserEmail = authHeaderUtility.extractEmailFromHeader(request);
        Member member = memberRepository.findByEmail(requestUserEmail).orElseThrow(
                () -> new NoSuchElementException("No Member present with the email.")
        );
        Board entity = requestBody.toEntity(member);

        return BoardRegisterResponse.builder()
                .id(boardRepository.save(entity).getId())
                .message("게시물이 성공적으로 등록됐습니다.")
                .build();
    }

    @Override
    public BoardUpdateResponse update(Long id, HttpServletRequest request, BoardUpdateRequest body) {
        final String requestUserEmail = authHeaderUtility.extractEmailFromHeader(request);
        final Board storedBoard = boardRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 게시물 update 요청.")
        );

        if (!storedBoard.getMember().getEmail().equals(requestUserEmail)) {
            throw new AccessDeniedException("요청자와 저작자의 불일치");
        }

        storedBoard.updateTitle(body.getTitle());
        storedBoard.updateContent(body.getContent());
        storedBoard.updateUpdateDate();

        Board updatedBoard = boardRepository.save(storedBoard);

        return BoardUpdateResponse.builder()
                .id(updatedBoard.getId())
                .message("게시물 수정이 정상적으로 처리됐습니다.")
                .build();
    }

    @Override
    public BoardDeleteResponse delete(Long id, HttpServletRequest request) {
        final String requestUserEmail = authHeaderUtility.extractEmailFromHeader(request);
        final Board storedBoard = boardRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 게시물 delete 요청.")
        );

        if (!storedBoard.getMember().getEmail().equals(requestUserEmail))
            throw new AccessDeniedException("요청자와 저작자의 불일치");

        boardRepository.delete(storedBoard);

        return BoardDeleteResponse.builder()
                .message("게시물 삭제가 정상적으로 처리됐습니다.")
                .build();
    }

    @Override
    public BoardGetResponse get(Long id, HttpServletRequest request, HttpServletResponse response) throws NoResourceFoundException {
        var entity = boardRepository.findById(id).orElseThrow(
                () -> new NoResourceFoundException(HttpMethod.GET, request.getRequestURI())
        );

        if (!hasViewed(id, request)) {
            addValueCookieBbv(id, request, response);

            // 게시글 중 조회수가 null 인 게시물 하나를 위한 if.
            if (entity.getViewCount() == null) {
                return BoardGetResponse.of(entity);
            }

            entity.addViewCount(); // throws NPException in previous version.
            boardRepository.save(entity);
        }
        return BoardGetResponse.of(entity);
    }

    private void initializeCookieBbv(Long id, HttpServletResponse response) {
        Cookie cookie = new Cookie(BOARDS_BEEN_VIEWED, id.toString());

        cookie.setMaxAge(60 * 60);
        response.addCookie(cookie);
    }

    private void addValueCookieBbv(Long id, HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            initializeCookieBbv(id, response);
            return;
        }
        Cookie cookie = cookieUtility.getCookie(BOARDS_BEEN_VIEWED, cookies);

        if (cookieUtility.getCookieValueSize(cookie) > 3500)
            cookieUtility.cutFirst500BytesWith(cookie, '/');
        cookieUtility.appendCookieValueWith(id, cookie, '/');

        cookie.setMaxAge(60 * 60);
        response.addCookie(cookie);
    }

    private boolean hasViewed(
            Long id
            , HttpServletRequest request
    ) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) return false;

        Cookie targetCookie = cookieUtility.getCookie(BOARDS_BEEN_VIEWED, cookies);

        if (targetCookie == null) return false;

        return cookieUtility.hasSpecificValueIn(id, targetCookie);
    }

    private static PageRequest getPageRequest(int pageNumber) {
        pageNumber = pageNumber - 1;
        int pageSize = 3;
        Sort sort = Sort.by(Sort.Direction.DESC, "registerDate");

        return PageRequest.of(pageNumber, pageSize, sort);
    }
}
