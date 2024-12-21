package com.site.Services;

import com.site.DTO.AnswerDTO;
import com.site.DTO.QuestionDTO;
import com.site.DTO.SiteUserDTO;
import com.site.Exception.DataNotFoundException;
import com.site.Form.AnswerForm;
import com.site.Services.Mapper.AnswerMapper;
import com.site.Services.Mapper.QuestionMapper;
import com.site.Services.Mapper.SiteUserMapper;
import com.site.models.Answer;
import com.site.models.Question;
import com.site.models.SiteUser;
import com.site.repository.AnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionService questionService;
    private final UserService userService;


    public AnswerDTO checkAuthorization(long id,String username){
        AnswerDTO answerDTO = this.getById(id);
        if (!answerDTO.getAuthor().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "권한이 없습니다.");
        }
        return answerDTO;
    }

    public AnswerDTO create(long questionId, AnswerForm answerForm, Principal principal ) {

        // Form --> DTO
        AnswerDTO answerDTO = new AnswerDTO();
        answerDTO.setContent(answerForm.getContent());
        answerDTO.setCreateDate(LocalDateTime.now());

        // answer 데이터베이스에 저장하기 위헤 question, user 서비스 계층에 데이터 값을 받아옴
        QuestionDTO questionDTO = this.questionService.getById(questionId);
        SiteUserDTO siteUserDTO = this.userService.getUser(principal.getName());

        // DTO --> 엔티티
        Answer answer = AnswerMapper.INSTANCE.toAnswer(answerDTO);
        Question question = QuestionMapper.INSTANCE.toQuestion(questionDTO);
        SiteUser siteUser = SiteUserMapper.INSTANCE.toSiteUser(siteUserDTO);

        // 엔티티에 해당 댓글이 있는 질문과 댓글을 작성한 유저를 엔티티에 저장
        answer.setQuestion(question);
        answer.setAuthor(siteUser);

        //데이터베이스에 저장 후 저장된 엔티티를 다시 DTO로 변환
        Answer savedAnswer = this.answerRepository.save(answer);
        return AnswerMapper.INSTANCE.toAnswerDTO(savedAnswer);
    }
    public AnswerDTO getById(Long id) {
        Optional<Answer> answer = this.answerRepository.findById(id);
        if(answer.isPresent()) {
            return AnswerMapper.INSTANCE.toAnswerDTO(answer.get());
        }else{
            throw new DataNotFoundException("answer not found");
        }
    }
    public void modify(long id, String username,AnswerForm answerForm) {
        AnswerDTO answerDTO =  checkAuthorization(id,username);
        answerDTO.setContent(answerForm.getContent());
        answerDTO.setModifyDate(LocalDateTime.now());
        Answer answer = AnswerMapper.INSTANCE.toAnswer(answerDTO);
        this.answerRepository.save(answer);

    }

    public void delete(long id,String username) {
        AnswerDTO answerDTO = checkAuthorization(id,username);
        Answer answer = AnswerMapper.INSTANCE.toAnswer(answerDTO);
        this.answerRepository.delete(answer);
    }
    public void vote(long id,String username){
        AnswerDTO answerDTO = this.getById(id);
        SiteUserDTO siteUserDTO = this.userService.getUser(username);
        Answer answer = AnswerMapper.INSTANCE.toAnswer(answerDTO);
        SiteUser User = SiteUserMapper.INSTANCE.toSiteUser(siteUserDTO);
        answer.getVoter().add(User);
        this.answerRepository.save(answer);
    }
}
