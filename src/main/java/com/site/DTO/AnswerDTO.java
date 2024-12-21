package com.site.DTO;

import com.site.models.SiteUser;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class AnswerDTO {
    private Long id;
    private String content;
    private LocalDateTime createDate;
    private Long questionId;
    private SiteUserDTO author;
    private LocalDateTime modifyDate;
    Set<SiteUser> voter;
}
