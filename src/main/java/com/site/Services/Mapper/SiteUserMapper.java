package com.site.Services.Mapper;

import com.site.DTO.SiteUserDTO;
import com.site.models.SiteUser;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SiteUserMapper {
    SiteUserMapper INSTANCE = Mappers.getMapper(SiteUserMapper.class);
    SiteUserDTO toSiteUserDTO(SiteUser siteUser);
    SiteUser toSiteUser(SiteUserDTO siteUserDTO);
}
