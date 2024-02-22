package eBook.EatBook.domain.member.controller;

import eBook.EatBook.domain.member.DTO.ConfirmCodeForm;
import eBook.EatBook.domain.member.DTO.FindUsernameForm;
import eBook.EatBook.domain.member.DTO.MemberForm;
import eBook.EatBook.domain.member.entity.Member;
import eBook.EatBook.domain.member.service.MemberService;
import eBook.EatBook.global.email.entity.Email1;
import eBook.EatBook.global.email.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
    private final EmailService emailService;

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
            return "/member/findUsernameForm";
        }
        // 이메일 맞는지 확인
        Member member = this.memberService.findByEmail(findUsernameForm.getToEmail());
        if(member == null){
            return "redirect:/member/findUsernameForm";
        }
        String confirmCode = this.RandomCode();
        // 여기서 터짐
        this.emailService.saveConfirmCode(findUsernameForm.getToEmail(),confirmCode);
        //
        this.emailService.send(findUsernameForm.getToEmail(),"[EatBook]아이디 확인을 위한 코드입니다.",String.format("코드 입력 \n [%s]", confirmCode));

        return "redirect:/member/confirmCodeUsername";
    }
    @GetMapping("/confirmCodeUsername")
    public String confirmCodeUsername(){


        return "/member/confirmCodeUsernameForm";
    }

    @PostMapping("/confirmCodeUsername")
    public String confirmCodeUsername(@Valid ConfirmCodeForm confirmCodeForm, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "/member/confirmCodeUsernameForm";
        }
        Email1 email1 =  this.emailService.findConfirmCode(confirmCodeForm.getConfirmCode());
        if(email1 == null){
            return "/member/findUsernameForm";
        }
        Member member = this.memberService.findByEmail(email1.getToEmail());
        this.emailService.send(member.getEmail(),"[EatBook] 아이디를 확인하세요",String.format("\n 아이디 : [%s]", member.getUsername()));

        return "redirect:/";
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
