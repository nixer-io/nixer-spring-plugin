package eu.xword.nixer.nixerplugin.core.metrics;

/**
 * Represents ID to be used for lookup for a meter in {@link MetersRepository} registry.
 * <p>
 * <b>Not to be confused with the Micrometer metric name</b>
 * </p>
 *
 * Created on 17/10/2019.
 *
 * @author gcwiak
 */
public interface MetricsLookupId {

    String lookupId();
}
