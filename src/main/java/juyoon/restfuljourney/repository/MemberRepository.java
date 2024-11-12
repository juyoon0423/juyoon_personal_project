package juyoon.restfuljourney.repository;

import juyoon.restfuljourney.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository <Member, Long> {
    // 이메일로 회원 조회
    Optional<Member> findByEmail(String email);
    // 사용자 이름으로 회원 조회
    Optional<Member> findByUsername(String username); // 추가된 메서드

    Page<Member> findByUsernameContaining(String username, Pageable pageable);


}
