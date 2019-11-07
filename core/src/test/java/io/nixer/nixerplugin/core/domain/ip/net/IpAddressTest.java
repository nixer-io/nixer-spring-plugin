package io.nixer.nixerplugin.core.domain.ip.net;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IpAddressTest {

    @Test
    public void should_create_ipv4_address() {
        final IpAddress ipAddress = IpAddress.fromIp("192.168.0.1");

        assertThat(ipAddress).isInstanceOf(Ipv4Address.class);
        assertThat(ipAddress.toString()).isEqualTo("192.168.0.1");
    }

    @Test
    public void should_create_ipv6_address() {
        final IpAddress ip6_1 = IpAddress.fromIp("2400:6500:0:7000::");
        assertThat(ip6_1).isInstanceOf(Ipv6Address.class);
        assertThat(ip6_1.toString()).isEqualToIgnoringCase("2400:6500:0:7000::");

        final IpAddress ip6_2 = IpAddress.fromIp("2400:6500:0:70FF:FFFF:FFFF:FFFF:FFFF");
        assertThat(ip6_2).isInstanceOf(Ipv6Address.class);
        assertThat(ip6_2.toString()).isEqualToIgnoringCase("2400:6500:0:70FF:FFFF:FFFF:FFFF:FFFF");

        final IpAddress ip6_3 = IpAddress.fromIp("::1");
        assertThat(ip6_3).isInstanceOf(Ipv6Address.class);
        assertThat(ip6_3.toString()).isEqualToIgnoringCase("::1");
    }
}
