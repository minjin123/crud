package com.site.Controllers;

import com.site.DTO.AnswerDTO;
import com.site.DTO.QuestionDTO;
import com.site.Form.AnswerForm;
import com.site.Services.AnswerService;
import com.site.Services.QuestionService;
import com.site.Services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequestMapping("/answer")
@RequiredArgsConstructor
@Controller
public class AnswerController {

    private final QuestionService questionService;
    private final AnswerService answerService;

    private static final String REDIRECT_QUESTION_DETAIL = "redirect:/question/detail/";
    private static final String ANSWER_ANCHOR = "#answer_";


    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}")
    public String createAnswer(@Valid AnswerForm answerForm, BindingResult bindingResult, @PathVariable("id") long id, Model model, Principal principal) {

        if(bindingResult.hasErrors()) {
            QuestionDTO question = this.questionService.getById(id);
            model.addAttribute("question", question);
            return "question/question_detail";
        }

        AnswerDTO savedAnswerDTO = this.answerService.create(id,answerForm,principal);

        return REDIRECT_QUESTION_DETAIL + id + ANSWER_ANCHOR + savedAnswerDTO.getId();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String answerModify(@PathVariable("id") long id, Principal principal,AnswerForm answerForm, Model model) {

        AnswerDTO answerDTO = this.answerService.checkAuthorization(id,principal.getName());
        answerForm.setContent(answerDTO.getContent());
        return "answer/answer_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String answerModify(@Valid AnswerForm answerForm, BindingResult bindingResult, @PathVariable("id") long id, Principal principal, Model model, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "answer/answer_form";
        }

        this.answerService.modify(id,principal.getName(),answerForm);
        AnswerDTO answerDTO = answerService.getById(id);
        return REDIRECT_QUESTION_DETAIL + answerDTO.getQuestionId() + ANSWER_ANCHOR + id;
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String answerDelete(@PathVariable("id") long id, Principal principal) {
        AnswerDTO answerDTO = answerService.getById(id);
        this.answerService.delete(id,principal.getName());
        return "redirect:/question/detail/" + answerDTO.getQuestionId();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String answerVote(@PathVariable("id") long id, Principal principal) {
        this.answerService.vote(id, principal.getName());
        AnswerDTO answerDTO = answerService.getById(id);
        return REDIRECT_QUESTION_DETAIL + answerDTO.getQuestionId() + ANSWER_ANCHOR + id;
    }
}
