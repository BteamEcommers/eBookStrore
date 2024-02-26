package eBook.EatBook.domain.member.controller;

import eBook.EatBook.domain.member.DTO.FindUsernameForm;
import eBook.EatBook.domain.member.DTO.MemberForm;
import eBook.EatBook.domain.member.DTO.MemberModifyForm;
import eBook.EatBook.domain.member.entity.Member;
import eBook.EatBook.domain.member.service.MemberService;
import eBook.EatBook.global.email.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.security.Principal;


@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
    private final EmailService emailService;

    @GetMapping("/modify")
    public String modifyMember(MemberModifyForm memberModifyForm, Model model, Principal principal) {
        Member member = this.memberService.findByUsername(principal.getName());
        model.addAttribute("member", member);
        return "/member/modifyMemberForm";
    }

    @PostMapping("/modify/{id}")
    public String modifyMember(Model model, @Valid MemberModifyForm memberModifyForm, BindingResult bindingResult,
                               @PathVariable(value = "id") Integer id) throws IOException {
        Member modifyMember = this.memberService.findById(id);
        model.addAttribute("member", modifyMember);

        this.memberService.PasswordValidator(memberModifyForm, bindingResult);

        if (bindingResult.hasErrors()) {
            return "/member/modifyMemberForm";
        }

        try {
            this.memberService.modify(memberModifyForm, modifyMember);
        } catch (DataIntegrityViolationException e) {
            bindingResult.reject("modifyFailed ", "이미 등록된 사용자입니다.");
        } catch (Exception e) {
            bindingResult.reject("modifyFailed ", e.getMessage());
        }

        return "redirect:/member/modify";
    }



    @GetMapping("/register")
    public String register(MemberForm memberForm){

        return "/member/registerForm";
    }

    @PostMapping("/register")
    public String register(@Valid MemberForm memberForm, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "/member/registerForm";
        }
        if(!memberForm.getPassword1().equals(memberForm.getPassword2()) ){
            return "/member/registerForm";
        }

        this.memberService.register(memberForm);

        return "redirect:/";
    }

    @GetMapping("/login")
    public String login(){


        return "/member/loginForm";
    }

    @GetMapping("/logout")
    public String logout(){

        return "redirect:/";
    }

    @GetMapping("/findUsername")
    public String findUsername(){

        return "/member/findUsernameForm";
    }

    @GetMapping("/findPassword")
    public String findPassword(){

        return "/member/findPasswordForm";
    }

    @PostMapping("/sendConfirmCode")
    public String sendConfirmCode(@Valid FindUsernameForm findUsernameForm, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "/member/findPasswordForm";
        }
        // 이메일 맞는지 확인
        Member member = this.memberService.findByEmail(findUsernameForm.getEmail());
        if(member == null){
            return "/member/findPasswordForm";
        }
        String confirmCode = this.RandomCode();

        this.emailService.send(findUsernameForm.getEmail(),"아이디 확인을 위한 코드입니다.",String.format("코드 입력 \n [%s]", confirmCode));

        return "/member/confirmCodeForm";
    }
    // 인증 위한 랜덤 코드 생성기 (대문자 알파벳 6자리)
    public String RandomCode(){
        StringBuilder randomCode = new StringBuilder();
        // 대문자 A-Z 랜덤 알파벳 생성
        for (int i = 1; i <=6; i++) {
            // (Math.random() * 26 => 0 ~ 25 까지의 랜덤한 실수
            //  "대문자 A의 10진수 아스키 코드 번호" == 65
            char ch = (char) ((Math.random() * 26) + 65);
            randomCode.append(ch);
        }

        return randomCode.toString();
    }
}
