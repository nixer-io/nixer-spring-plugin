package eu.xword.nixer.nixerplugin;

import eu.xword.nixer.nixerplugin.captcha.CaptchaLoginProperties;
import eu.xword.nixer.nixerplugin.stigma.StigmaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "nixer.login")
public class NixerProperties {

    @NestedConfigurationProperty
    private StigmaProperties stigma = new StigmaProperties();

    @NestedConfigurationProperty
    private CaptchaLoginProperties captcha = new CaptchaLoginProperties();

    public StigmaProperties getStigma() {
        return stigma;
    }

    public void setStigma(final StigmaProperties stigma) {
        this.stigma = stigma;
    }

    public CaptchaLoginProperties getCaptcha() {
        return captcha;
    }

    public void setCaptcha(final CaptchaLoginProperties captcha) {
        this.captcha = captcha;
    }
}
