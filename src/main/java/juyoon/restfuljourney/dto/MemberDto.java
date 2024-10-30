package juyoon.restfuljourney.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import juyoon.restfuljourney.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberDto {

    private Long id;
    @NotBlank(message = "유저 이름은 공백일 수 없습니다.")
    private String username;

    @JsonIgnore
    @NotNull(message = "비밀번호는 필수 항목입니다.")
    @Size(min = 8, max = 16, message = "비밀번호는 8자 이상, 16자 이하여야 합니다.")
    private String password;

    @NotNull(message = "이메일은 필수 항목입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    public static MemberDto fromEntity(Member entity){
        return new MemberDto().builder()
                .username(entity.getUsername())
                .password(entity.getPassword())
                .email(entity.getEmail())
                .build();
    }
}
