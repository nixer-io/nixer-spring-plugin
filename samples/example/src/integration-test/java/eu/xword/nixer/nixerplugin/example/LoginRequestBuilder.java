package eu.xword.nixer.nixerplugin.example;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class LoginRequestBuilder {

    private String captcha;
    private String captchaParam = "g-recaptcha-response";

    private String usernameParam = "username";
    private String passwordParam = "password";
    private String username = "user";
    private String password = "password";
    private String loginProcessingUrl = "/login";
    private MediaType acceptMediaType = MediaType.APPLICATION_FORM_URLENCODED;

    private RequestPostProcessor postProcessor = csrf();

    public static LoginRequestBuilder formLogin() {
        return new LoginRequestBuilder();
    }

    public MockHttpServletRequestBuilder build() {
        return post(this.loginProcessingUrl)
                .accept(this.acceptMediaType)
                .param(this.usernameParam, this.username)
                .param(this.passwordParam, this.password)
                .param(this.captchaParam, this.captcha)
                .with(postProcessor);
    }

    /**
     * The value of the captcha parameter.
     * @param captcha the value of the captcha parameter.
     * @return the {@link LoginRequestBuilder} for additional customizations
     */
    public LoginRequestBuilder captcha(final String captcha) {
        this.captcha = captcha;
        return this;
    }

    /**
     * The HTTP parameter to place the captcha. Default is "g-recaptcha-response".
     * @param captchaParameter the HTTP parameter to place the captcha response. Default is
     * "g-recaptcha-response".
     * @return the {@link LoginRequestBuilder} for additional customizations
     */
    public LoginRequestBuilder captchaParameter(final String captchaParameter) {
        this.captchaParam = captchaParam;
        return this;
    }

    /**
     * Specifies the URL to POST to. Default is "/login"
     *
     * @param loginProcessingUrl the URL to POST to. Default is "/login"
     * @return the {@link LoginRequestBuilder} for additional customizations
     */
    public LoginRequestBuilder loginProcessingUrl(String loginProcessingUrl) {
        this.loginProcessingUrl = loginProcessingUrl;
        return this;
    }

    /**
     * The HTTP parameter to place the username. Default is "username".
     * @param usernameParameter the HTTP parameter to place the username. Default is
     * "username".
     * @return the {@link LoginRequestBuilder} for additional customizations
     */
    public LoginRequestBuilder userParameter(String usernameParameter) {
        this.usernameParam = usernameParameter;
        return this;
    }

    /**
     * The HTTP parameter to place the password. Default is "password".
     * @param passwordParameter the HTTP parameter to place the password. Default is
     * "password".
     * @return the {@link LoginRequestBuilder} for additional customizations
     */
    public LoginRequestBuilder passwordParam(String passwordParameter) {
        this.passwordParam = passwordParameter;
        return this;
    }

    /**
     * The value of the password parameter. Default is "password".
     * @param password the value of the password parameter. Default is "password".
     * @return the {@link LoginRequestBuilder} for additional customizations
     */
    public LoginRequestBuilder password(String password) {
        this.password = password;
        return this;
    }

    /**
     * The value of the username parameter. Default is "user".
     * @param username the value of the username parameter. Default is "user".
     * @return the {@link LoginRequestBuilder} for additional customizations
     */
    public LoginRequestBuilder user(String username) {
        this.username = username;
        return this;
    }

    /**
     * Specify both the password parameter name and the password.
     *
     * @param passwordParameter the HTTP parameter to place the password. Default is
     * "password".
     * @param password the value of the password parameter. Default is "password".
     * @return the {@link LoginRequestBuilder} for additional customizations
     */
    public LoginRequestBuilder password(String passwordParameter,
                                        String password) {
        passwordParam(passwordParameter);
        this.password = password;
        return this;
    }

    /**
     * Specify both the password parameter name and the password.
     *
     * @param usernameParameter the HTTP parameter to place the username. Default is
     * "username".
     * @param username the value of the username parameter. Default is "user".
     * @return the {@link LoginRequestBuilder} for additional customizations
     */
    public LoginRequestBuilder user(String usernameParameter, String username) {
        userParameter(usernameParameter);
        this.username = username;
        return this;
    }

    /**
     * Specify a media type to set as the Accept header in the request.
     *
     * @param acceptMediaType the {@link MediaType} to set the Accept header to.
     * Default is: MediaType.APPLICATION_FORM_URLENCODED
     * @return the {@link LoginRequestBuilder} for additional customizations
     */
    public LoginRequestBuilder acceptMediaType(MediaType acceptMediaType) {
        this.acceptMediaType = acceptMediaType;
        return this;
    }

}
