package com.vong.manidues.jpa;

import com.vong.manidues.token.TokenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class UnderstandingJPARepositoryTests {

    @Autowired
    private TokenRepository tokenRepository;

    /**
     * <pre>
     * derived delete method 는 총 두 번의 쿼리를 거친다.
     * 첫 째로
     *     select
     *         *
     *     from
     *         token t1_0
     *     where
     *         t1_0.token=?
     * 둘 째로
     *     select
     *         *
     *     from
     *         member m1_0
     *     where
     *         m1_0.id=?
     * </pre>
     * <p>
     * 토큰 테이블은 멤버 테이블 기본키를 참조하기 때문에
     * Hibernate 는 위와 같이
     * delete method 의 매개변수로 주어진 refreshToken 을 넣어 토큰 테이블을 셀렉트하고,
     * 해당 레코드에서 참조하는 멤버 테이블 기본키를 매개변수로 넣어 멤버 테이블을 셀렉트 한다.
     * (삭제 작업 전에 참조 무결성이나 cascade 관련 을 검사하는 것이려나)
     * </p>
     * <p>
     * 이후에 삭제 작업이 이루어지는 것 같은데,
     * 보다시피 Hibernate 은 derived method 를 선언할 때
     * 반환 타입을 개발자가 정하기 때문에, 해당 객체를 반환할 지, 쿼리 실행 결과 수를 반환할 지 등
     * 지시된 작업을 이행하기 전 정보를 잠시 모아두는 것 같다.
     * </p>
     * <p>
     * Hibernate 가 각 메서드의 반환 타입에 따라 반환할 객체를 찾고, 검사하는 등의 필요성으로
     * 하나의 메서드에 다수의 쿼리가 동작할 수 있는데, 동시성 문제를 보완하기 위해 하나의 트랜잭션으로 묶어줘야된다는 느낌인거 같다.
     * </p>
     * <p>
     * 조회 후 삭제가 이뤄지니 트랜잭션으로 묶어주지 않으면, 조회 --- 삭제 사이 데이터의 변화가 생길 경우 기대하는 쿼리를 수행하는데 문제가 생기기 때문이 아닐까 싶다.
     * </p>
     * <p>
     * 좀더 테스트 해보고 이해한 다음 해당 주석은 수정하자.
     * </p>
     * <hr>
     * <p>첫 째로, repository method signature 에 @Transactional 적용.</p>
     * <pre>
     * 1. delete method 매개변수로 주어진 refresh token 으로 토큰 테이블을 조회
     * Hibernate:
     *     select
     *         t1_0.id,
     *         t1_0.expired,
     *         t1_0.member_id,
     *         t1_0.revoked,
     *         t1_0.token,
     *         t1_0.token_type
     *     from
     *         token t1_0
     *     where
     *         t1_0.token=?
     *
     * 2. 1 에서 얻은 멤버 아이디를 통해 멤버 테이블을 조회.
     * 토큰 엔터티에서 멤버 객체를 속성으로 가지고 있기 때문인가?
     * token.member_id 컬럼의 정보를 얻기위해서 멤버 테이블을 조회하는 것 같다.
     * 그렇게 되면 비로소 derived delete method 의 반환 타입이 객체일 경우에
     * 반환할 객체가 완성될 수 있겠다.
     * Hibernate:
     *     select
     *         m1_0.id,
     *         m1_0.email,
     *         m1_0.nickname,
     *         m1_0.password,
     *         m1_0.register_date,
     *         m1_0.role
     *     from
     *         member m1_0
     *     where
     *         m1_0.id=?
     *
     * 3. 그렇게 어떤 타입으로든 메서드 반환값을 반환할 준비를 마친 후에 직접적인 delete 쿼리가 실행된다.
     * 4. 여기서는 deleteBy method 인데, 쿼리가 두 번 실행되는 것을 볼 수 있다.
     * 아마 delete method 의 매개변수로 전달한 refresh token 을 가지고 있는 레코드가
     * 2 행인 것으로 확인 됐기 때문에 두 번의 delete 쿼리를 실행한 것으로 보인다.
     * 삭제 과정에서 deleteBy method 의 매개변수로는 String token 을 줬지만
     * 실제 삭제 쿼리는 Hibernate 가 조회해서
     * 구성한 Token 엔터티의 id 를 통해서 수행되는 것을 확인할 수 있다.
     * Hibernate:
     *     delete
     *     from
     *         token
     *     where
     *         id=?
     * Hibernate:
     *     delete
     *     from
     *         token
     *     where
     *         id=?
     * </pre>
     * <hr>
     * <p>둘 째로, delete method 를 호출하는 메서드에 @Transactional 적용.</p>
     * <pre>
     * 1. 매개변수로 토큰 테이블 조회.
     * Hibernate:
     *     select
     *         t1_0.id,
     *         t1_0.expired,
     *         t1_0.member_id,
     *         t1_0.revoked,
     *         t1_0.token,
     *         t1_0.token_type
     *     from
     *         token t1_0
     *     where
     *         t1_0.token=?
     *
     * 2. 토큰 엔터티가 참조하는 멤버 테이블 조회
     * Hibernate:
     *     select
     *         m1_0.id,
     *         m1_0.email,
     *         m1_0.nickname,
     *         m1_0.password,
     *         m1_0.register_date,
     *         m1_0.role
     *     from
     *         member m1_0
     *     where
     *         m1_0.id=?
     *
     * Hibernate 쿼리는 콘솔에 여기까지만 찍혔다.
     * 실제로 데이터베이스에서도 해당 레코드는 삭제 되지 않았고,
     * 테스트는 성공했다. deleteByToken 메서드가 반환하는 삭제 값이 2 인 것으로
     * expected 값과 맞았기 때문.
     *
     * delete method 를 호출하는 곳에 @Transactional 을 적용했더니
     * 트랜잭션 범위를 의도한 대로 잡을 수 없었다.
     *
     * </pre>
     * <hr>
     * 1. 우선 @Transactional 어노테이션의 적당한 적용 위치는 derived delete method 를 선안한 곳이 적절하고
     * 2. delete method 를 호출하는 메서드에 @Transactional 을 붙이면 목표한 바대로 delete 쿼리가 가 수행되지 않는다.
     * 2. deleteBy derived method 는 매개변수로 전달한 요소레 부합하는 레코드 수 만큼 delete 구문을 실행한다 정도로 결론을 내린다.
     * */
    @Test
    public void deleteByMethod() {
        String refreshToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoZXJvbkB2b25nLmNvbSIsImlhdCI6MTcwOTcxODA2MSwiZXhwIjoxNzEyMTM3MjYxfQ.8Uy8t_A47BQ1Ays7GrMvSY1RZV7dKKKup-Y5OgXxS68";
        int actualDeleted = tokenRepository.deleteByToken(refreshToken);

        org.springframework.test.util.AssertionErrors.assertEquals("expected deleted count is 2", 2, actualDeleted);
        org.junit.jupiter.api.Assertions.assertEquals(2, actualDeleted);
    }

    /**
     * <pre>
     * 다음으로 deleteAllBy derived method.
     * 마찬가지로 @Transactional 을 deleteAllBy 메서드 시그니처에 적용한다.
     * 우선 실행해본다.
     *
     * 1. 토큰 테이블과 멤버 테이블을 조회하여 엔터티 구성하고.
     * Hibernate:
     *     select
     *         t1_0.id,
     *         t1_0.expired,
     *         t1_0.member_id,
     *         t1_0.revoked,
     *         t1_0.token,
     *         t1_0.token_type
     *     from
     *         token t1_0
     *     where
     *         t1_0.token=?
     * Hibernate:
     *     select
     *         m1_0.id,
     *         m1_0.email,
     *         m1_0.nickname,
     *         m1_0.password,
     *         m1_0.register_date,
     *         m1_0.role
     *     from
     *         member m1_0
     *     where
     *         m1_0.id=?
     *
     * 2. 매개변수로 준 refresh token 값은 해당 테이블에 3개가 있었고.
     * 총 세 번의 delete 쿼리가 실행됐다.
     * Hibernate:
     *     delete
     *     from
     *         token
     *     where
     *         id=?
     * Hibernate:
     *     delete
     *     from
     *         token
     *     where
     *         id=?
     * Hibernate:
     *     delete
     *     from
     *         token
     *     where
     *         id=?
     * </pre>
     * <hr>
     * <p>
     *     해당 매개변수로 조회된 레코드의 수 만큼 delete 쿼리가 이뤄진다는 점을 확인했다.
     *     deleteBy 와 deleteAllBy derived method 의 차이에 대해서 좀 더 알아봐야겠다.
     *     각 crud method 내부동작은 SimpleJpaRepository.java 클래스를 참고할 것.
     * </p>
     * */
    @Test
    public void deleteAllByMethod() {
        String refreshToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoZXJvbkB2b25nLmNvbSIsImlhdCI6MTcwOTcxODUwNSwiZXhwIjoxNzEyMTM3NzA1fQ.VT9XO2ut-h5ZpQD-Bq3FgU8yAwjFYMUMbTtfK4eXjUE";
        int actualDeleted = tokenRepository.deleteAllByToken(refreshToken);

        org.springframework.test.util.AssertionErrors.assertEquals("expected deleted count is 3", 3, actualDeleted);
        org.junit.jupiter.api.Assertions.assertEquals(3, actualDeleted);
    }

}
