package com.site.Services;


import com.site.DTO.QuestionDTO;
import com.site.DTO.SiteUserDTO;
import com.site.Exception.DataNotFoundException;
import com.site.Form.QuestionForm;
import com.site.Services.Mapper.QuestionMapper;
import com.site.Services.Mapper.SiteUserMapper;
import com.site.models.Answer;
import com.site.models.Question;
import com.site.models.SiteUser;
import com.site.repository.QuestionRepository;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.Serial;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final UserService userService;

    public QuestionDTO checkAuthorization(long id,String username){
        QuestionDTO questionDTO = this.getById(id);
        if (!questionDTO.getAuthor().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "권한이 없습니다.");
        }
        return questionDTO;
    }

    // 글 목록 출력
    public Page<QuestionDTO> getList(int page, String keyword){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));

        Pageable pageable = PageRequest.of(page,10,Sort.by(sorts));
        Specification<Question> spec = search(keyword);
        Page<Question> questions = this.questionRepository.findAll(spec, pageable);
        return questions.map(QuestionMapper.INSTANCE::toQuestionDTO);
    }
    public QuestionDTO getById(long id){
        Optional<Question> question = this.questionRepository.findById(id);
        if(question.isPresent()){
            return QuestionMapper.INSTANCE.toQuestionDTO(question.get());
        }else {
            throw new DataNotFoundException("question not found");
        }
    }
    // 글 등록
    public void createQuestion(QuestionForm questionForm,String username){
        QuestionDTO questionDTO = new QuestionDTO();
        SiteUserDTO siteUserDTO = this.userService.getUser(username);

        questionDTO.setSubject(questionForm.getSubject());
        questionDTO.setContent(questionForm.getContent());
        questionDTO.setCreateDate(LocalDateTime.now());
        questionDTO.setAuthor(siteUserDTO);

        Question q = QuestionMapper.INSTANCE.toQuestion(questionDTO);

        this.questionRepository.save(q);
    }
    public void modify(long id, String username,QuestionForm questionForm){
        QuestionDTO questionDTO = checkAuthorization(id,username);
        questionDTO.setSubject(questionForm.getSubject());
        questionDTO.setContent(questionForm.getContent());
        questionDTO.setModifyDate(LocalDateTime.now());

        Question q = QuestionMapper.INSTANCE.toQuestion(questionDTO);

        this.questionRepository.save(q);
    }

    public void delete(long id,String username){
        QuestionDTO questionDTO = checkAuthorization(id,username);
        Question q = QuestionMapper.INSTANCE.toQuestion(questionDTO);

        this.questionRepository.delete(q);
    }

    public void vote(long id,String username){
        QuestionDTO questionDTO = this.getById(id);
        SiteUserDTO siteUserDTO = this.userService.getUser(username);
        Question q = QuestionMapper.INSTANCE.toQuestion(questionDTO);
        SiteUser user = SiteUserMapper.INSTANCE.toSiteUser(siteUserDTO);
        q.getVoter().add(user);
        this.questionRepository.save(q);

    }

    private Specification<Question> search(String keyword){
        return new Specification<>(){
            @Serial
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);
                Join<Question,SiteUser> u1 = q.join("author", JoinType.LEFT);
                Join<Question,Answer> a = q.join("answers", JoinType.LEFT);
                Join<Answer,SiteUser> u2 = a.join("author", JoinType.LEFT);
                return cb.or(cb.like(q.get("subject"),"%"+keyword+"%"),
                cb.like(q.get("content"),"%"+keyword+"%"),
                cb.like(u1.get("username"),"%"+keyword+"%"),
                cb.like(a.get("content"),"%"+keyword+"%"),
                cb.like(u2.get("username"),"%"+keyword+"%"));
            }
        };
    }



}
