package eu.xword.nixer.nixerplugin.core.domain.ip.range;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

/**
 * Represents set of Ip ranges.
 */
public class IpRanges {

    @JsonProperty("ranges")
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private List<IpRange> ranges = Collections.emptyList();

    public List<IpRange> getRanges() {
        return ranges;
    }

    public List<String> getIpv4Prefixes() {
        return ranges.stream()
                .flatMap(ipRange -> ipRange.getIpv4Prefixes().stream())
                .collect(Collectors.toList());
    }

    public List<String> getIpv6Prefixes() {
        return ranges.stream()
                .flatMap(ipRange -> ipRange.getIpv6Prefixes().stream())
                .collect(Collectors.toList());
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class IpRange {

        private String name;

        private Instant timestamp;

        @JsonProperty("ipv4_prefixes")
        @JsonSetter(nulls = Nulls.AS_EMPTY)
        private List<String> ipv4Prefixes = Collections.emptyList();

        @JsonProperty("ipv6_prefixes")
        @JsonSetter(nulls = Nulls.AS_EMPTY)
        private List<String> ipv6Prefixes = Collections.emptyList();

        public String getName() {
            return name;
        }

        public Instant getTimestamp() {
            return timestamp;
        }

        public List<String> getIpv4Prefixes() {
            return ipv4Prefixes;
        }

        public List<String> getIpv6Prefixes() {
            return ipv6Prefixes;
        }
    }
}
