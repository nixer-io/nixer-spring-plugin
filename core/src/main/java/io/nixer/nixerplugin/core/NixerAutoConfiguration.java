package io.nixer.nixerplugin.core;

import io.nixer.nixerplugin.core.detection.DetectionConfiguration;
import io.nixer.nixerplugin.core.detection.events.elastic.ElasticLoggingAutoConfiguration;
import io.nixer.nixerplugin.core.detection.events.log.EventLogConfiguration;
import io.nixer.nixerplugin.core.detection.filter.FilterConfiguration;
import io.nixer.nixerplugin.core.login.LoginConfiguration;
import io.nixer.nixerplugin.core.metrics.MetricsConfiguration;
import io.nixer.nixerplugin.core.stigma.StigmaConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static io.nixer.nixerplugin.core.NixerAutoConfiguration.ORDER;

@Configuration
@AutoConfigureOrder(ORDER)
@Import({
        DetectionConfiguration.class,
        FilterConfiguration.class,
        LoginConfiguration.class,
        MetricsConfiguration.class,
        EventLogConfiguration.class,
        ElasticLoggingAutoConfiguration.class,
        StigmaConfiguration.class
})
public class NixerAutoConfiguration {

    public static final int ORDER = 10;

}
