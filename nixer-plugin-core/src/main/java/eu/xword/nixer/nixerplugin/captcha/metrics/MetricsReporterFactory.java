package eu.xword.nixer.nixerplugin.captcha.metrics;

public interface MetricsReporterFactory {

    MetricsReporter createMetricsReporter(String action);
}
