package com.site.Controllers;


import com.site.Form.UserCreateForm;
import com.site.Form.VerificationForm;
import com.site.Form.idVerificationGroup;
import com.site.Form.setPasswordGroup;
import com.site.Services.EmailService;
import com.site.Services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final EmailService emailService;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/signup")
    public String signupForm(UserCreateForm userCreateForm, Model model) {
        model.addAttribute("userCreateForm", userCreateForm);
        return "user/signup_form";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "user/login_form";
    }

    @PostMapping("/signup")
    public String processSignup(@ModelAttribute @Valid UserCreateForm userCreateForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "user/signup_form";
        }

        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            redirectAttributes.addFlashAttribute("userCreateForm", userCreateForm);
            bindingResult.rejectValue("password2", "Passwords do not match", "2개의 패스워드가 일치하지 않습니다.");
            return "user/signup_form";
        }

        try {
            this.userService.createUser(userCreateForm.getEmail(), userCreateForm.getUsername(), userCreateForm.getPassword1());
        } catch (DataIntegrityViolationException e) {
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            logger.error("User signup failed", e);
            return "user/signup_form";
        } catch (Exception e) {
            bindingResult.reject("signupFailed", "회원가입 중 오류가 발생했습니다.");
            logger.error("Unexpected error during signup", e);
            return "user/signup_form";
        }


        return "redirect:/";

    }
    @GetMapping("/forgot_username")
    public String showForgotUsernameForm(VerificationForm verificationForm, Model model) {
        model.addAttribute("verificationForm", verificationForm);
        model.addAttribute("verificationSent", false);
        return "user/forgot_username";
    }
    @PostMapping("/forgot_username")
    public String handleForgotUsername(
            @ModelAttribute @Validated(idVerificationGroup.class) VerificationForm verificationForm,
            BindingResult bindingResult,
            Model model,
            @RequestParam String action){

        String email = verificationForm.getEmail();
        String code = emailService.generateVerificationCode();

        if (bindingResult.hasErrors()) {
            return "user/forgot_username";
        }

        switch (action){
            case "send":
                if(!userService.isEmailRegistered(email)) {
                    model.addAttribute("verificationSent", false);
                    model.addAttribute("email",email);
                    bindingResult.rejectValue("email","emailNotExist", "입력하신 정보와 일치하는 유저가 없습니다.");
                    return "user/forgot_username";
                }

                emailService.VerificationForId(email,code);
                model.addAttribute("email",email);
                model.addAttribute("verificationSent",true);
                model.addAttribute("message", "인증번호가 이메일로 발송되었습니다.");
                return "user/forgot_username";
            case "verify":
                String InputCode = verificationForm.getVerificationCode();

                if(!emailService.verifyVerificationCodeForId(email,InputCode)){
                    model.addAttribute("verificationSent", true);
                    model.addAttribute("email",email);
                    bindingResult.rejectValue("verificationCode","errorCode","인증번호가 일치하지 않습니다.");
                    return "user/forgot_username";
                }
                String username = userService.getUserByEmail(email);
                model.addAttribute("verificationSent", false);
                model.addAttribute("username",username);
                return "user/forgot_username";
            default:
                return "user/forgot_username";
        }
    }
    @GetMapping("/forgot_password")
    public String forgot_password(VerificationForm verificationForm, Model model){
        model.addAttribute("verificationSent", false);
        model.addAttribute("verificationForm", verificationForm);
        return "user/forgot_password";
    }
    @PostMapping("/forgot_password")
    public String VerificationUserPassword(
            @ModelAttribute @Valid VerificationForm verificationForm,
            BindingResult bindingResult,
            Model model,
            HttpSession session,
            @RequestParam String action){

        String username = verificationForm.getUsername();
        String email = verificationForm.getEmail();
        String code = emailService.generateVerificationCode();

        if (bindingResult.hasErrors()) {
            return "user/forgot_password";
        }

        switch (action){
            case "send":
                if(!userService.isEmailRegistered(email) || !userService.isUsernameRegistered(username)) {

                    addUserAttributes(model,verificationForm);
                    model.addAttribute("verificationSent", false);
                    bindingResult.rejectValue("email","NotExistEmailOrUsername", "입력하신 정보와 일치하는 유저가 없습니다.");
                    return "user/forgot_password";
                }
                model.addAttribute("verificationSent",true);
                emailService.VerificationForPassword(username,email,code);
                addUserAttributes(model,verificationForm);
                model.addAttribute("message","인증번호가 이메일로 발송되었습니다.");
                return "user/forgot_password";
            case "verify":
                String InputCode = verificationForm.getVerificationCode();
                if(!emailService.verifyVerificationCodeForPassword(username,email,InputCode)){
                    model.addAttribute("verificationSent", true);
                    addUserAttributes(model,verificationForm);
                    bindingResult.rejectValue("verificationCode","errorCode","인증번호가 일치하지 않습니다.");
                    return "user/forgot_password";
                }
                session.setAttribute("email",email);

                return "redirect:/user/set_password";
            default:
                return "user/forgot_password";

        }
    }
    @GetMapping("/set_password")
    public String forgot_password(UserCreateForm userCreateForm,
                                  Model model){
        model.addAttribute("userCreateForm", userCreateForm);
        return "user/set_password";
    }
    @PostMapping("/set_password")
    public String setPassword(@ModelAttribute @Validated(setPasswordGroup.class) UserCreateForm userCreateForm,
                              BindingResult result,
                              HttpSession session
                             ){

        if(result.hasErrors()){
            return "user/set_password";
        }

        if(!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())){
            result.rejectValue("password2", "Passwords do not match", "2개의 패스워드가 일치하지 않습니다.");
            return "user/set_password";
        }

        String email = (String)session.getAttribute("email");
        if(email == null){
            return "redirect:/forgot_password";
        }

        try {
            this.userService.updatePassword(email, userCreateForm.getPassword1());
            session.removeAttribute("email");
        }catch (Exception e){
            return "user/set_password";
        }
        return "redirect:/user/login";

    }
    private void addUserAttributes(Model model,VerificationForm verificationForm){
        model.addAttribute("username",verificationForm.getUsername());
        model.addAttribute("email",verificationForm.getEmail());
    }

}
