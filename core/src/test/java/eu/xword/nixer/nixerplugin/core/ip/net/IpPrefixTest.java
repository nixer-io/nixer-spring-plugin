package eu.xword.nixer.nixerplugin.core.ip.net;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IpPrefixTest {

    @Test
    public void should_parse_ipv4_prefix() {
        final IpPrefix<Ipv4Address> prefix = IpPrefix.fromIpv4("127.0.0.0/16");

        assertThat(prefix.getMask()).isEqualTo(16);
        assertThat(prefix.getAddress().toString()).isEqualTo("127.0.0.0");
    }

    @Test
    public void should_fail_to_parse_ipv6_as_ipv4_prefix() {
        assertThrows(IllegalArgumentException.class, () -> IpPrefix.fromIpv4("::1/128"));
    }

    @Test
    public void should_fail_to_parse_ipv4_prefix_with_invalid_mask() {
        assertThrows(IllegalArgumentException.class, () -> IpPrefix.fromIpv4("127.0.0.0/-1"));

        assertThrows(IllegalArgumentException.class, () -> IpPrefix.fromIpv4("127.0.0.0/33"));
    }

    @Test
    public void should_parse_ipv6_prefix() {
        final IpPrefix<Ipv6Address> prefix = IpPrefix.fromIpv6("::1/128");

        assertThat(prefix.getMask()).isEqualTo(128);
        assertThat(prefix.getAddress().toString()).isEqualTo("::1");
    }

    @Test
    public void should_fail_to_parse_ipv4_as_ipv6_prefix() {
        assertThrows(IllegalArgumentException.class, () -> IpPrefix.fromIpv6("127.0.0.1/16"));
    }
}