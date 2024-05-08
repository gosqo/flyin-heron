package com.vong.manidues.board;

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
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final CookieUtility cookieUtility;

    private final String BOARDS_BEEN_VIEWED = "bbv";

    public void initializeCookieBbv(Long id, HttpServletResponse response) {
        Cookie cookie = new Cookie(BOARDS_BEEN_VIEWED, id.toString());

        cookie.setMaxAge(60 * 60);
        response.addCookie(cookie);
    }

    public void addValueCookieBbv(Long id, HttpServletRequest request, HttpServletResponse response) {
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

    public boolean hasViewed(
            Long id
            , HttpServletRequest request
    ) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            if (cookieUtility.hasCookieNamed(BOARDS_BEEN_VIEWED, cookies)) {
                Cookie targetCookie = cookieUtility.getCookie(BOARDS_BEEN_VIEWED, cookies);

                return cookieUtility.hasSpecificValueIn(id, targetCookie);
            }
        }
        return false;
    }

    @Override
    public Page<Board> getBoardPage(Pageable pageable) {
        Page<Board> foundPage = boardRepository.findAll(pageable);
        if (foundPage.getTotalPages() - 1 < pageable.getPageNumber()) return null;
        return foundPage;
    }

    @Override
    public Long register(String userEmail, BoardRegisterRequest request) {

        Optional<Member> optionalMember = memberRepository.findByEmail(userEmail);

        if (optionalMember.isPresent()) {

            Board entity = request.toEntity(optionalMember.get());
            return boardRepository.save(entity).getId();

        } else {

            throw new NoSuchElementException("No Member present with the email.");
        }
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
    public Board get(Long id) {
        return boardRepository.findById(id).orElseThrow();
    }
}
