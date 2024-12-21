package com.site.Services.Mapper;

import com.site.DTO.QuestionDTO;
import com.site.models.Question;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface QuestionMapper {
    QuestionMapper INSTANCE = Mappers.getMapper(QuestionMapper.class);
    QuestionDTO toQuestionDTO(Question question);
    Question toQuestion(QuestionDTO questionDTO);
}
