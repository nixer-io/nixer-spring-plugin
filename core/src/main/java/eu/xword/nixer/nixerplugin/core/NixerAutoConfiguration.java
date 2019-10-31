package eu.xword.nixer.nixerplugin.core;

import eu.xword.nixer.nixerplugin.core.detection.DetectionConfiguration;
import eu.xword.nixer.nixerplugin.core.detection.events.elastic.ElasticLoggingAutoConfiguration;
import eu.xword.nixer.nixerplugin.core.detection.filter.FilterConfiguration;
import eu.xword.nixer.nixerplugin.core.login.LoginConfiguration;
import eu.xword.nixer.nixerplugin.core.metrics.MetricsConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static eu.xword.nixer.nixerplugin.core.NixerAutoConfiguration.ORDER;

@Configuration
@AutoConfigureOrder(ORDER)
@Import({
        DetectionConfiguration.class,
        FilterConfiguration.class,
        LoginConfiguration.class,
        MetricsConfiguration.class,
        ElasticLoggingAutoConfiguration.class
})
public class NixerAutoConfiguration {

    public static final int ORDER = 10;

}
