package io.nixer.nixerplugin.captcha.validation;

import io.nixer.nixerplugin.captcha.CaptchaService;
import io.nixer.nixerplugin.captcha.CaptchaServiceFactory;
import io.nixer.nixerplugin.captcha.error.CaptchaClientException;
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
@WebMvcTest(controllers = RecaptchaTestController.class)
@ContextConfiguration(classes = AppConfiguration.class)
class CaptchaValidatorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CaptchaServiceFactory serviceFactory;

    @MockBean
    private CaptchaService captchaService;

    @BeforeEach
    void setup() {
        Mockito.when(serviceFactory.createCaptchaService(Mockito.anyString())).thenReturn(captchaService);
    }

    @Test
    void should_return_ok_captcha_validation_succeeded() throws Exception {
        Mockito.doNothing()
                .when(captchaService).verifyResponse(Mockito.anyString());

        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/captchaTest")
                .param("g-recaptcha-response", "io.nixer.nixerplugin.captcha")).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void should_return_validation_error_with_custom_message() throws Exception {
        Mockito.doThrow(new CaptchaClientException(""))
                .when(captchaService).verifyResponse(Mockito.anyString());

        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/captchaTest")
                .param("g-recaptcha-response", "io.nixer.nixerplugin.captcha")).andDo(print())
                .andExpect(content().string(containsString("Captcha error")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_return_validation_error_with_valid_message() throws Exception {
        Mockito.doThrow(new CaptchaClientException(""))
                .when(captchaService).verifyResponse(Mockito.anyString());

        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/captchaTestDefault")
                .param("g-recaptcha-response", "io.nixer.nixerplugin.captcha")).andDo(print())
                .andExpect(content().string(containsString("{io.nixer.nixerplugin.captcha.validation.Captcha.message}")))
                .andExpect(status().isBadRequest());
    }
}
