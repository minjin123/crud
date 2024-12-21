package com.site.Controllers;

import com.site.DTO.QuestionDTO;
import com.site.Form.AnswerForm;
import com.site.Form.QuestionForm;
import com.site.Services.QuestionService;
import com.site.Services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.security.Principal;

@RequestMapping("/question")
@RequiredArgsConstructor
@Controller
public class QuestionController {

    private final QuestionService questionService;



    @GetMapping("/list")
    public String list(Model model,
                       @RequestParam(value="page",defaultValue = "0")int page,
                       @RequestParam(value="keyword",defaultValue = "")String keyword) {

        Page<QuestionDTO> pages = questionService.getList(page,keyword);
        model.addAttribute("pages", pages);
        model.addAttribute("keyword", keyword);
        return "question/question_list";
    }

    @GetMapping(value="/detail/{id}")
    public String detail(@PathVariable("id") long id, Model model) {
        QuestionDTO question = this.questionService.getById(id);
        model.addAttribute("question", question);
        model.addAttribute("answerForm", new AnswerForm());
        return "question/question_detail";
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String questionCreate() {
        return "question/question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String create(@ModelAttribute @Valid QuestionForm questionForm, BindingResult bindingResult, RedirectAttributes redirectAttributes, Principal principal) {
        if(bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("questionForm", questionForm);
            return "question/question_form";
        }

        this.questionService.createQuestion(questionForm, principal.getName());
        return "redirect:/question/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String questionModify(QuestionForm questionForm,@PathVariable("id") long id, Principal principal) {
        QuestionDTO questionDTO = this.questionService.getById(id);
        if(!questionDTO.getAuthor().getUsername().equals(principal.getName())) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        questionForm.setSubject(questionDTO.getSubject());
        questionForm.setContent(questionDTO.getContent());
        return "question/question_form";
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String questionModify(@Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal, @PathVariable("id") long id) {
        if (bindingResult.hasErrors()) {
            return "question/question_form";
        }
        this.questionService.modify(id, principal.getName(),questionForm);

        return String.format("redirect:/question/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") long id,Principal principal) {

        this.questionService.delete(id,principal.getName());

        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String questionVote(Principal principal,@PathVariable("id") long id) {
        this.questionService.vote(id, principal.getName());
        return String.format("redirect:/question/detail/%s", id);
    }
}
