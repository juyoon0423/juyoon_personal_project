package juyoon.restfuljourney.service;

import juyoon.restfuljourney.entity.Member;
import juyoon.restfuljourney.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional
    // 회원가입
    public Long register(Member member) {
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    // 이미 존재하는 회원인지 검증
    private void validateDuplicateMember(Member member) {
        Optional<Member> findMembers = memberRepository.findByEmail(member.getEmail());
        if (findMembers.isPresent()) {
            throw new IllegalStateException("이미 존재하는 회원입니다");
        }
    }

    // 회원 조회
    public Member findById(Long memberId) {
        return memberRepository.findById(memberId).orElse(null);
    }
}
