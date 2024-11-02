package juyoon.restfuljourney.controller;

import juyoon.restfuljourney.dto.MemberDto;
import juyoon.restfuljourney.dto.MemberResponseDto;
import juyoon.restfuljourney.entity.Member;
import juyoon.restfuljourney.repository.MemberRepository;
import juyoon.restfuljourney.service.MemberService;
import juyoon.restfuljourney.service.S3Service;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final S3Service s3Service;

    // 회원가입
    @PostMapping("/register")
    public MemberResponseDto register(@Validated @RequestBody MemberDto memberDto) {
        Member member = Member.builder()
                .id(memberDto.getId())
                .username(memberDto.getUsername())
                .password(memberDto.getPassword())
                .email(memberDto.getEmail())
                .build();

        Long id = memberService.register(memberDto);
        log.info("username={}, password={}, email={}", member.getUsername(), member.getPassword(), member.getEmail());

        Member registeredMember = memberService.findOne(id);
        return MemberResponseDto.fromEntity(registeredMember); // 등록 후 엔티티를 Dto로 변환해 반환
    }

    // 회원 정보 조회
    @GetMapping("/member/{memberId}")
    public MemberResponseDto findOne(@PathVariable Long memberId) {
        // 멤버 검색
        Member member = memberService.findOne(memberId);

        return MemberResponseDto.fromEntity(member); // 변환 코드 간소화
    }

//    // 전체 회원 조회
//    @GetMapping("/members")
//    public Result findMembers() {
//        List<Member> findMembers = memberRepository.findAll();
//        List<MemberResponseDto> collect = new ArrayList<>();
//
//        for (Member member : findMembers) {
//            collect.add(MemberResponseDto.fromEntity(member));
//        }
//
//        return new Result<>(collect.size(), collect);
//    }

    // 전체 회원 조회
    @GetMapping("/members")
    public Result findMembers(
            @RequestParam(defaultValue = "") String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("username").ascending());
        Page<Member> memberPage = memberRepository.findByUsernameContaining(username, pageable);

        List<MemberResponseDto> members = memberPage.getContent().stream()
                .map(MemberResponseDto::fromEntity)
                .toList();

        return new Result<>(memberPage.getTotalPages(), members);
    }

    // 회원 수정
    @PutMapping("/editMember/{memberId}")
    public MemberResponseDto editMember(
            @PathVariable Long memberId,
            @Validated @RequestBody MemberDto memberDto) {

        memberService.update(memberId, memberDto);

        Member findMember = memberService.findOne(memberId);
        return MemberResponseDto.fromEntity(findMember); // 변환 코드 간소화
    }

    // 회원 탈퇴
    @DeleteMapping("/delete/{memberId}")
    public String delete(@PathVariable Long memberId) {
        memberService.delete(memberId);
        return "삭제 완료";
    }

    // 파일 업로드
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = s3Service.uploadFile(file);
            log.info("File uploaded successfully: {}", fileUrl);
            return fileUrl;
        } catch (IOException e) {
            log.error("Failed to upload file", e);
            throw new RuntimeException("파일 업로드에 실패했습니다.");
        }
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int totalPages;
        private T data;
    }
}
