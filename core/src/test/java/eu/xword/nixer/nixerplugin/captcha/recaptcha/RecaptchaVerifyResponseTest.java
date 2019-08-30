package eu.xword.nixer.nixerplugin.captcha.recaptcha;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static eu.xword.nixer.nixerplugin.captcha.recaptcha.RecaptchaVerifyResponse.ErrorCode.MissingSecret;


class RecaptchaVerifyResponseTest {

    ObjectMapper json = new ObjectMapper();

    @Test
    public void testDeserializeOkMessage() throws IOException {
        final InputStream resource = getClass().getClassLoader().getResourceAsStream("ok-message.json");
        final RecaptchaVerifyResponse response = json.readValue(resource, RecaptchaVerifyResponse.class);

        Assertions.assertThat(response)
                .isEqualTo(new RecaptchaVerifyResponse(true, "2019-08-30T07:17:48Z", "localhost", null));
    }

    @Test
    public void testDeserializeErrorMessage() throws IOException {
        final InputStream resource = getClass().getClassLoader().getResourceAsStream("failed-message.json");
        final RecaptchaVerifyResponse response = json.readValue(resource, RecaptchaVerifyResponse.class);

        Assertions.assertThat(response)
                .isEqualTo(new RecaptchaVerifyResponse(false, null, null,
                        ImmutableList.of(MissingSecret)));
    }
}