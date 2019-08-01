package eu.xword.nixer.nixerplugin;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "nixer")
@Component
public class NixerProperties {

    private StigmaProperties stigma = new StigmaProperties();

    public StigmaProperties getStigma() {
        return stigma;
    }

    public void setStigma(final StigmaProperties stigma) {
        this.stigma = stigma;
    }

}
