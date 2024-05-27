package com.vong.manidues.board;

import com.vong.manidues.board.dto.BoardGetResponse;
import com.vong.manidues.board.dto.BoardRegisterRequest;
import com.vong.manidues.board.dto.BoardUpdateRequest;
import com.vong.manidues.cookie.CookieUtility;
import com.vong.manidues.member.Member;
import com.vong.manidues.member.MemberRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final CookieUtility cookieUtility;

    private static final String BOARDS_BEEN_VIEWED = "bbv";

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

    @Override
    public Page<Board> getBoardPage(Pageable pageable) {
        Page<Board> foundPage = boardRepository.findAll(pageable);
        if (foundPage.getTotalPages() - 1 < pageable.getPageNumber()) return null;
        return foundPage;
    }

    @Override
    public Long register(String userEmail, BoardRegisterRequest request) {
        Member member = memberRepository.findByEmail(userEmail).orElseThrow(
                () -> new NoSuchElementException("No Member present with the email.")
        );
        Board entity = request.toEntity(member);

        return boardRepository.save(entity).getId();
    }

    @Override
    public boolean update(Long id,
                          String requestUserEmail,
                          BoardUpdateRequest request) {

        Board storedBoard = boardRepository.findById(id).orElseThrow();

        if (storedBoard.getMember().getEmail().equals(requestUserEmail)) {

            storedBoard.updateTitle(request.getTitle());
            storedBoard.updateContent(request.getContent());
            storedBoard.updateUpdateDate();

            boardRepository.save(storedBoard);

            return true;
        }

        return false;
    }

    @Override
    public boolean delete(Long id, String requestUserEmail) {

        Board storedBoard = boardRepository.findById(id).orElseThrow();

        if (storedBoard.getMember().getEmail().equals(requestUserEmail)) {

            boardRepository.delete(storedBoard);
            return true;
        }

        return false;
    }

    @Override
    public BoardGetResponse get(Long id, HttpServletRequest request, HttpServletResponse response) {
        var entity = boardRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 게시물 get 요청."));

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
}
