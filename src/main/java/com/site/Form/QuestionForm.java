package com.site.Form;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionForm {
    @NotEmpty(message="제목을 입력하시오.")
    @Size(max=200)
    private String subject;

    @NotEmpty(message="내용을 입력하시오.")
    private String content;

}
