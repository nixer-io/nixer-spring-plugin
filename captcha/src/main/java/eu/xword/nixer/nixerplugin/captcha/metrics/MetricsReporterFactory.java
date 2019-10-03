package eu.xword.nixer.nixerplugin.captcha.metrics;

public interface MetricsReporterFactory {

    MetricsReporter createMetricsReporter(final String action);
}
