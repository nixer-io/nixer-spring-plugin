package eu.xword.nixer.nixerplugin.example.light;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Validated
public class WelcomeController {

    @GetMapping("/")
    public String main(Model model) {
        model.addAttribute("message", "world");

        return "welcome"; //view
    }
}
