package juyoon.restfuljourney.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.Size;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @NotBlank(message = "유저 이름은 공백일 수 없습니다.")
    private String username;

    @NotNull(message = "비밀번호는 필수 항목입니다.")
    @Size(min = 8, max = 16, message = "비밀번호는 8자 이상, 16자 이하여야 합니다.")
    private String password;

    @NotNull(message = "이메일은 필수 항목입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;
}

