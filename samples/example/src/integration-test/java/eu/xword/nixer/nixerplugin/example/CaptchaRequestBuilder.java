package eu.xword.nixer.nixerplugin.example;

import javax.servlet.ServletContext;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.RequestBuilder;

public class CaptchaRequestBuilder implements RequestBuilder {

    private RequestBuilder requestBuilder;
    private String captcha;
    private String captchaParameter = "g-recaptcha-response";

    private CaptchaRequestBuilder(final RequestBuilder requestBuilder) {
        this.requestBuilder = requestBuilder;
    }

    public static final CaptchaRequestBuilder from(final RequestBuilder requestBuilder) {
        return new CaptchaRequestBuilder(requestBuilder);
    }

    public CaptchaRequestBuilder withCaptcha(final String catpcha) {
        this.captcha = catpcha;
        return this;
    }

    public CaptchaRequestBuilder withCaptcha(final String captchaParameter, final String captcha) {
        this.captchaParameter = captchaParameter;
        return withCaptcha(captcha);
    }

    @Override
    public MockHttpServletRequest buildRequest(final ServletContext servletContext) {
        final MockHttpServletRequest request = requestBuilder.buildRequest(servletContext);
        request.addParameter(captchaParameter, this.captcha);

        return request;
    }
}
