package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

/**
 * JPA 공통 인터페이스 JpaRepository 확장
 */
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * 쿼리 메서드
     * 메서드 이름으로 쿼리 생성
     * 컴파일 시에 문법 검사
     * 짤막한 쿼리, 조건이 별로 없는 쿼리를 만드는 데 사용한다.
     * 엔티티의 프로퍼티명을 바꾸면 메서드명을 바꿔줘야 한다. 컴파일 시에 에러.
     */
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    /**
     * 조건이 없는 경우 전체 목록을 조회한다.
     * find{아무거나}By{조건}(조건)
     */
    /*
    select
    member0_.member_id as member_i1_0_,
            member0_.age as age2_0_,
    member0_.team_id as team_id4_0_,
            member0_.username as username3_0_
    from
    member member0_
     */
    List<Member> findHelloBy();

    /*
    select
        member0_.member_id as member_i1_0_,
        member0_.age as age2_0_,
        member0_.team_id as team_id4_0_,
        member0_.username as username3_0_
    from
        member member0_ limit 3;
     */
    List<Member> findTop3HelloBy();

    /**
     * 리포지토리 메서드에 바로 쿼리 정의하기
     * 메서드명 쿼리보다 복잡한 jpql를 정의하기 좋다.
     * 오타가 난 경우 컴파일 에러.
     */
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    /**
     * 값 조회하기
     */
    @Query("select m.username from Member m")
    List<String> findUsernames();

    /**
     * DTO 조회하기
     */
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    /**
     * 리스트 파라미터 바인딩
     */
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    /**
     * 반환 타입
     * List, 단 건 엔티티, Optional, ...
     */
    List<Member> findMembersByUsername(String username);
    Member findMemberByUsername(String username);
    Optional<Member> findOptionalByUsername(String username);
}
