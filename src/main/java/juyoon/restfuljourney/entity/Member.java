package juyoon.restfuljourney.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import jakarta.validation.constraints.Size;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;
    private String password;
    private String email;


}

