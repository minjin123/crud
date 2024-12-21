package com.site.DTO;

import com.site.models.SiteUser;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class QuestionDTO {
    private Long id;
    private String subject;
    private String content;
    private LocalDateTime createDate;
    private List<AnswerDTO> answers;
    private SiteUserDTO author;
    private LocalDateTime modifyDate;
    Set<SiteUser> voter;
}
