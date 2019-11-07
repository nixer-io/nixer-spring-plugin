package eu.xword.nixer.nixerplugin.captcha.validation;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@Validated
public class RecaptchaTestController {

    @PostMapping("/captchaTest")
    public String captchaTest(@ModelAttribute("g-recaptcha-response") @Captcha(action = "user_subscribe", message = "Captcha error") String captcha) {
        return "";
    }

    @PostMapping("/captchaTestDefault")
    public String captchaTestDefault(@ModelAttribute("g-recaptcha-response") @Captcha(action = "user_subscribe") String captcha) {
        return "";
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
