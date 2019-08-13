package eu.xword.nixer.nixerplugin.example;

import javax.validation.Valid;

import eu.xword.nixer.nixerplugin.captcha.validation.Captcha;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Validated
public class WelcomeController {

    @GetMapping("/")
    public String main(Model model) {
        model.addAttribute("message", "world");

        return "welcome"; //view
    }

    @PostMapping("/subscribeUser")
    public String subscribeUser(@Valid @RequestParam("g-recaptcha-response") @Captcha(action = "user_subscribe") String captcha, Model model) {

        model.addAttribute("message", "User registered");

        return "userSubscription";
    }
}
