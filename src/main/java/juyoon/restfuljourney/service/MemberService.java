package juyoon.restfuljourney.service;

import juyoon.restfuljourney.dto.MemberDto;
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
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    // 회원 조회
    public Member findOne(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalStateException("해당 회원을 찾을 수 없습니다."));
    }

    // 회원 탈퇴
    @Transactional
    public void delete(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new IllegalStateException("회원 정보가 존재하지 않습니다.");
        }
        memberRepository.deleteById(memberId);
    }

    // 회원 수정
    @Transactional
    public void update(Long id, MemberDto memberDto) {
        Member member = findOne(id);
        member.setUsername(memberDto.getUsername());
        member.setEmail(memberDto.getEmail());
        member.setPassword(memberDto.getPassword());
    }


}
