package io.nixer.nixerplugin.captcha.recaptcha;

import java.io.IOException;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.Fault;
import io.nixer.nixerplugin.captcha.error.CaptchaServiceException;
import io.nixer.nixerplugin.core.metrics.MetricsFactory;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.util.ResourceUtils;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
        classes = RecaptchaConfiguration.class,
        properties = {
                "nixer.captcha.recaptcha.verifyUrl=http://localhost:8089/recaptcha/api/siteverify",
                "nixer.captcha.recaptcha.key.secret=secret-key"
        }
)
class RecaptchaRestClientTest {

    private static final String CAPTCHA = "captcha";

    @MockBean
    MetricsFactory metricsFactory;

    @Autowired
    private RecaptchaRestClient client;

    @Value("${nixer.captcha.recaptcha.key.secret}")
    private String secretKey;

    private WireMockServer wireMockServer = new WireMockServer(wireMockConfig().port(8089));


    @BeforeEach
    void setup() {
        wireMockServer.start();
    }

    @Test
    void shouldHandleCaptchaValidMessage() throws IOException {
        mockOkMessage(okMessage());

        final RecaptchaVerifyResponse verifyResponse = client.call(CAPTCHA);

        assertTrue(verifyResponse.isSuccess());
    }

    @Test
    void shouldHandleCaptchaFailedMessage() throws IOException {
        mockOkMessage(failedMessage());

        final RecaptchaVerifyResponse verifyResponse = client.call(CAPTCHA);

        assertFalse(verifyResponse.isSuccess());
    }

    @Test
    void shouldHandleServerError() throws IOException {
        mockMessage(500, failedMessage());

        assertThrows(CaptchaServiceException.class, () -> client.call(CAPTCHA));
    }

    @Test
    void shouldHandleConnectivityError() {
        wireMockServer.stop();

        assertThrows(CaptchaServiceException.class, () -> client.call(CAPTCHA));
    }

    @Test
    void shouldHandleServerTimeout() {
        mockServerTimeout();

        assertThrows(CaptchaServiceException.class, () -> client.call(CAPTCHA));
    }

    @EnumSource(Fault.class)
    @ParameterizedTest(name = "should handle fault {0}")
    void shouldHandleConnectivityFaults(Fault fault) {
        mockServerFault(fault);

        assertThrows(CaptchaServiceException.class, () -> client.call(CAPTCHA));
    }

    private void mockServerTimeout() {
        wireMockServer.stubFor(get(urlPathEqualTo("/recaptcha/api/siteverify"))
                .withQueryParam("response", equalTo(CAPTCHA))
                .withQueryParam("secret", equalTo(secretKey))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withFixedDelay(5_000)
                )
        );
    }

    private void mockOkMessage(final String body) {
        wireMockServer.stubFor(get(urlPathEqualTo("/recaptcha/api/siteverify"))
                .withQueryParam("response", equalTo(CAPTCHA))
                .withQueryParam("secret", equalTo(secretKey))
                .willReturn(okJson(body)));
    }

    private void mockMessage(final int status, final String body) {
        wireMockServer.stubFor(get(urlPathEqualTo("/recaptcha/api/siteverify"))
                .withQueryParam("response", equalTo(CAPTCHA))
                .withQueryParam("secret", equalTo(secretKey))
                .willReturn(aResponse()
                        .withStatus(status)
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .withBody(body)
                )
        );
    }

    private void mockServerFault(Fault fault) {
        wireMockServer.stubFor(get(urlPathEqualTo("/"))
                .withQueryParam("response", equalTo(CAPTCHA))
                .withQueryParam("secret", equalTo(secretKey))
                .willReturn(aResponse()
                        .withFault(fault))
        );
    }


    private String okMessage() throws IOException {
        return FileUtils.readFileToString(ResourceUtils.getFile("classpath:ok-message.json"));
    }

    private String failedMessage() throws IOException {
        return FileUtils.readFileToString(ResourceUtils.getFile("classpath:failed-message.json"));
    }

    @AfterEach
    void teardown() {
        wireMockServer.stop();
    }

}