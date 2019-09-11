package eu.xword.nixer.nixerplugin.ip.range;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public class IpRanges {

    @JsonProperty("ranges")
    private List<IpRange> ranges;

    public List<IpRange> getRanges() {
        return ranges;
    }

    public List<String> getIpv4Prefixes() {
        return ranges.stream()
                .flatMap(ipRange -> Stream.of(ipRange.getIpv4Prefixes()))
                .collect(Collectors.toList());
    }

    public List<String> getIpv6Prefixes() {
        return ranges.stream()
                .flatMap(ipRange -> Stream.of(ipRange.getIpv6Prefixes()))
                .collect(Collectors.toList());
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class IpRange {

        private String name;

        private Instant timestamp;

        @JsonProperty("ipv4_prefixes")
        private String[] ipv4Prefixes;
        @JsonProperty("ipv6_prefixes")
        private String[] ipv6Prefixes;

        public String getName() {
            return name;
        }

        public Instant getTimestamp() {
            return timestamp;
        }

        public String[] getIpv4Prefixes() {
            return ipv4Prefixes;
        }

        public String[] getIpv6Prefixes() {
            return ipv6Prefixes;
        }
    }
}
