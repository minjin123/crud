package com.site.Services.Mapper;

import com.beust.ah.A;
import com.site.DTO.AnswerDTO;
import com.site.models.Answer;
import com.site.models.Question;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AnswerMapper {
    AnswerMapper INSTANCE = Mappers.getMapper(AnswerMapper.class);

    @Mapping(source = "question.id", target = "questionId")
    AnswerDTO toAnswerDTO(Answer answer);
    @Mapping(source = "questionId", target = "question.id")
    Answer toAnswer(AnswerDTO answerDTO);
}
