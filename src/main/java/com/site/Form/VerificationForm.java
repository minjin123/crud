package com.site.Form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerificationForm {
    @Size(min=3, max=25)
    @NotEmpty(message ="ID를 입력하시오")
    private String username;

    @NotEmpty(message ="이메일을 입력하시오")
    @Email(groups = idVerificationGroup.class)
    private String email;

    private String verificationCode;
}
