package com.mysite.sbb.question;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/question")
public class QuestionController {


    @GetMapping("/list") //Get, Post내부에 @RequestMapping 들어있음 (중복 ㄴㄴ)
    public String list() {
        return "/question/list";
    }
}
