package cz.zcu.kiv.elexa.mnsck.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@Controller
public class IndexController {
    @GetMapping("/")
    public String index(@RequestHeader(value = "HX-Request", required = false) boolean isHtmxRequest) {
        if (isHtmxRequest) {
            return "index";
        }

        return "layout";
    }
}
