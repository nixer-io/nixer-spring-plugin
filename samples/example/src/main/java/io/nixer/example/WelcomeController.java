package io.nixer.example;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import io.nixer.nixerplugin.captcha.validation.Captcha;
import io.nixer.nixerplugin.captcha.validation.Captcha;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

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

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map handle(ConstraintViolationException exception) {
        return error(exception.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList()));
    }

    private Map error(Object message) {
        return Collections.singletonMap("error", message);
    }

}
