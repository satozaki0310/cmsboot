package jp.co.stnet.cms.base.presentation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

    /**
     * tests
     *
     * @return
     */
    @GetMapping
    public String hello() {
        return "hello";
    }

}
