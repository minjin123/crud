package com.site.DTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SiteUserDTO {
    private Long id;
    private String username;
    private String password;
    private String email;
}
