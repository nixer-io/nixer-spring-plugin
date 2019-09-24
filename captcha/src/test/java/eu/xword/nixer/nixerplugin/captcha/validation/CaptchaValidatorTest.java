package eu.xword.nixer.nixerplugin.captcha.validation;

import eu.xword.nixer.nixerplugin.captcha.CaptchaService;
import eu.xword.nixer.nixerplugin.captcha.CaptchaServiceFactory;
import eu.xword.nixer.nixerplugin.captcha.error.RecaptchaClientException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = RecaptchaTestController.class, secure = false)
@ContextConfiguration(classes = AppConfiguration.class)
public class CaptchaValidatorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CaptchaServiceFactory serviceFactory;

    @MockBean
    private CaptchaService captchaService;

    @BeforeEach
    public void setup() {
        Mockito.when(serviceFactory.createCaptchaService(Mockito.anyString())).thenReturn(captchaService);
    }

    @Test
    public void should_return_ok_captcha_validation_succeeded() throws Exception {
        Mockito.doNothing()
                .when(captchaService).processResponse(Mockito.anyString());

        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/captchaTest")
                .param("g-recaptcha-response", "eu.xword.nixer.nixerplugin.captcha")).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void should_return_validation_error_with_custom_message() throws Exception {
        Mockito.doThrow(new RecaptchaClientException(""))
                .when(captchaService).processResponse(Mockito.anyString());

        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/captchaTest")
                .param("g-recaptcha-response", "eu.xword.nixer.nixerplugin.captcha")).andDo(print())
                .andExpect(content().string(containsString("Captcha error")))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void should_return_validation_error_with_valid_message() throws Exception {
        Mockito.doThrow(new RecaptchaClientException(""))
                .when(captchaService).processResponse(Mockito.anyString());

        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/captchaTestDefault")
                .param("g-recaptcha-response", "eu.xword.nixer.nixerplugin.captcha")).andDo(print())
                .andExpect(content().string(containsString("{eu.xword.nixer.nixerplugin.eu.xword.nixer.nixerplugin.captcha.validation.Captcha.message}")))
                .andExpect(status().isBadRequest());
    }
}
