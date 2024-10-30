package juyoon.restfuljourney.controller;

import juyoon.restfuljourney.dto.MemberDto;
import juyoon.restfuljourney.entity.Member;
import juyoon.restfuljourney.repository.MemberRepository;
import juyoon.restfuljourney.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    // 회원가입
    @PostMapping("/register")
    public MemberDto register(@Validated @RequestBody MemberDto memberDto) {
        Member member = Member.builder()
                .username(memberDto.getUsername())
                .password(memberDto.getPassword())
                .email(memberDto.getEmail())
                .build();

        Long id = memberService.register(member);
        log.info("username={}, password={}, email={}", member.getUsername(), member.getPassword(), member.getEmail());

        Member registeredMember = memberService.findOne(id);
        return MemberDto.fromEntity(registeredMember); // 등록 후 엔티티를 Dto로 변환해 반환
    }

    // 회원 정보 조회
    @GetMapping("/member/{memberId}")
    public MemberDto findOne(@PathVariable Long memberId) {
        // 멤버 검색
        Member member = memberService.findOne(memberId);

        return MemberDto.fromEntity(member); // 변환 코드 간소화
    }

    // 전체 회원 조회
    @GetMapping("/members")
    public Result findMembers() {
        List<Member> findMembers = memberRepository.findAll();
        List<MemberDto> collect = new ArrayList<>();

        for (Member member : findMembers) {
            collect.add(MemberDto.fromEntity(member));
        }

        return new Result<>(collect.size(), collect);

    }

    // 회원 수정
    @PutMapping("/editMember/{memberId}")
    public MemberDto editMember(
            @PathVariable Long memberId,
            @Validated @RequestBody MemberDto memberDto) {

        memberService.update(memberId, memberDto);

        Member findMember = memberService.findOne(memberId);
        return MemberDto.fromEntity(findMember); // 변환 코드 간소화
    }

    // 회원 탈퇴
    @DeleteMapping("/delete/{memberId}")
    public String delete(@PathVariable Long memberId) {
        memberService.delete(memberId);
        return "삭제 완료";
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int size;
        private T data;
    }
}
