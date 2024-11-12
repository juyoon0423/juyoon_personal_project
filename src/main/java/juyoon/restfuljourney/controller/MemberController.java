package juyoon.restfuljourney.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import juyoon.restfuljourney.dto.MemberDto;
import juyoon.restfuljourney.dto.MemberResponseDto;
import juyoon.restfuljourney.entity.Member;
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
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final S3Service s3Service;

    @Operation(summary = "Register a new member", description = "회원가입 API로 회원 정보를 받아 새로 등록합니다.")
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
        return MemberResponseDto.fromEntity(registeredMember);
    }

    @Operation(summary = "Get member by ID", description = "회원 ID로 회원 정보를 조회합니다.")
    @GetMapping("/member/{memberId}")
    public MemberResponseDto findOne(
            @Parameter(description = "ID of the member to retrieve") @PathVariable Long memberId) {
        Member member = memberService.findOne(memberId);
        return MemberResponseDto.fromEntity(member);
    }

    @Operation(summary = "Get all members", description = "전체 회원 정보를 페이징하여 조회합니다.")
    @GetMapping("/members")
    public Result findMembers(
            @Parameter(description = "Username to search") @RequestParam(defaultValue = "") String username,
            @Parameter(description = "Page number for pagination") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "8") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("username").ascending());
        Page<Member> memberPage = memberService.findMembersWithPaging(username, pageable);

        List<MemberResponseDto> members = memberPage.getContent().stream()
                .map(MemberResponseDto::fromEntity)
                .toList();

        return new Result<>(memberPage.getTotalPages(), members);
    }

    @Operation(summary = "Update a member", description = "회원 정보를 수정합니다.")
    @PutMapping("/editMember/{memberId}")
    public MemberResponseDto editMember(
            @Parameter(description = "ID of the member to update") @PathVariable Long memberId,
            @Validated @RequestBody MemberDto memberDto) {

        memberService.update(memberId, memberDto);
        Member findMember = memberService.findOne(memberId);
        return MemberResponseDto.fromEntity(findMember);
    }

    @Operation(summary = "Delete a member", description = "회원 정보를 삭제합니다.")
    @DeleteMapping("/delete/{memberId}")
    public String delete(
            @Parameter(description = "ID of the member to delete") @PathVariable Long memberId) {
        memberService.delete(memberId);
        return "삭제 완료";
    }

    @Operation(summary = "Upload a file to S3", description = "파일을 S3에 업로드하고, 해당 파일의 URL을 반환합니다.")
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
