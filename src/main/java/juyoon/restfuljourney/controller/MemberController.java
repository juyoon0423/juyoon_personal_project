package juyoon.restfuljourney.controller;

import juyoon.restfuljourney.entity.Member;
import juyoon.restfuljourney.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/register")
    public ResponseEntity<Member> register(@Validated @RequestBody Member member) {
        memberService.register(member);
        log.info("username{}, password={}, email={}", member.getUsername(), member.getPassword(), member.getEmail());
        return ResponseEntity.ok(member); // HTTP 200 상태 코드와 함께 응답
    }

    // 회원 정보 조회
    @GetMapping("/member/{memberId}")
    public ResponseEntity<Member> findOne(@PathVariable Long memberId) {
        Member member = memberService.findById(memberId);
        if (member == null) {
            return ResponseEntity.notFound().build(); // 404 응답
        }
        return ResponseEntity.ok(member); // 200 응답과 함께 member 객체 반환
    }


}
