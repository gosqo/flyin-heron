package com.vong.manidues.board;

import com.vong.manidues.board.dto.BoardRegisterRequest;
import com.vong.manidues.board.dto.BoardUpdateRequest;
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

    private int getBbvSize(Cookie bbv) {
        return (bbv.getValue().getBytes()).length;
    }

    public String cutFirst500byte(String origin) {
        return origin.substring(origin.indexOf("/", 500) + 1);
    }

    public boolean hasViewed(
            Long id
            , HttpServletRequest request
            , HttpServletResponse response
    ) {
        // viewed flag, 쿠키에 해당 보드를 조회한 내역이 있는지. 반환 결과에 따라서 조회수 증가.
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equalsIgnoreCase("bbv")) { // bbv boardsBeenViewed
                    String beenViewed = cookie.getValue();
                    String[] viewedIds = beenViewed.split("/");
                    String updatedBeenViewed = "";
                    int bbvSize = getBbvSize(cookie);

                    log.info("Cookie size by byte is :{}", bbvSize);

                    for (String viewedId : viewedIds) {
                        if (viewedId.matches(id.toString())) return true;
                    }

                    if (bbvSize > 3500) {
                        updatedBeenViewed = cutFirst500byte(beenViewed) + "/" + id;

                        cookie.setValue(updatedBeenViewed);
                        cookie.setMaxAge(60 * 60);
                        response.addCookie(cookie);

                        return false;
                    }

                    beenViewed += "/" + id;

                    cookie.setValue(beenViewed);
                    cookie.setMaxAge(60 * 60);
                    response.addCookie(cookie);

                    return false;
                }
            }
        }
        Cookie newCookie = new Cookie("bbv", id.toString());
        newCookie.setMaxAge(60 * 60);
        response.addCookie(newCookie);

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
