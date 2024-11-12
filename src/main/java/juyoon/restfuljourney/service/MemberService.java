package juyoon.restfuljourney.service;

import juyoon.restfuljourney.dto.MemberDto;
import juyoon.restfuljourney.entity.Member;
import juyoon.restfuljourney.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;


    @Transactional
// 회원가입
    public Long register(MemberDto memberDto) {
        Member member = new Member();
        member.setId(memberDto.getId());
        member.setUsername(memberDto.getUsername());
        member.setPassword(memberDto.getPassword());
        member.setPassword(memberDto.getPassword());
        member.setEmail(memberDto.getEmail());

        validateDuplicateMember(member); // 중복 회원 검증
        memberRepository.save(member); // 회원 정보 저장

        return member.getId(); // 저장된 회원의 ID 반환
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

    // 페이징 조회
    public Page<Member> findMembersWithPaging(String username, Pageable pageable) {
        return memberRepository.findByUsernameContaining(username, pageable);
    }


}
