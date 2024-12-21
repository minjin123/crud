package com.site.repository;

import com.site.DTO.AnswerDTO;
import com.site.models.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
}
