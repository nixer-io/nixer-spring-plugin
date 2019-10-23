package eu.xword.nixer.nixerplugin.core.ip.tree;

import java.io.File;
import java.io.IOException;
import java.util.List;

import eu.xword.nixer.nixerplugin.core.ip.net.IpAddress;
import eu.xword.nixer.nixerplugin.core.ip.net.IpPrefix;
import eu.xword.nixer.nixerplugin.core.ip.net.Ipv4Address;
import eu.xword.nixer.nixerplugin.core.ip.net.Ipv6Address;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import static org.assertj.core.api.Assertions.assertThat;

class UnibitIpTrieTest {

    @Test
    public void should_match_ipv4_prefixes() {
        IpTree<Ipv4Address> ipPrefixTree = new UnibitIpTrie<>();

        ipPrefixTree.put(IpPrefix.fromIpv4("108.175.52.0/22"));
        ipPrefixTree.put(IpPrefix.fromIpv4("108.175.56.0/22"));
        ipPrefixTree.put(IpPrefix.fromIpv4("108.174.0.0/16"));


        assertThat(ipPrefixTree.contains(ipv4("108.175.51.255"))).isFalse();

        for (int i = 0; i < 255; i++) {
            assertThat(ipPrefixTree.contains(ipv4("108.175.52." + i))).isTrue();
        }
        assertThat(ipPrefixTree.contains(ipv4("108.175.53.255"))).isTrue();

        assertThat(ipPrefixTree.contains(ipv4("108.175.56.255"))).isTrue();
        assertThat(ipPrefixTree.contains(ipv4("108.175.57.255"))).isTrue();

        assertThat(ipPrefixTree.contains(ipv4("108.174.0.0"))).isTrue();
        assertThat(ipPrefixTree.contains(ipv4("108.174.255.255"))).isTrue();
    }

    @Test
    public void should_match_ipv6_prefixes() {
        IpTree<Ipv6Address> ipPrefixTree = new UnibitIpTrie<>();

        ipPrefixTree.put(IpPrefix.fromIpv6("2400:6500:0:7000::/56"));
        ipPrefixTree.put(IpPrefix.fromIpv6("2400:6500:0:7200::/56"));
        ipPrefixTree.put(IpPrefix.fromIpv6("2400:6500:0:7300::/56"));
        ipPrefixTree.put(IpPrefix.fromIpv6("2400:6500:0:7400::/56"));
        ipPrefixTree.put(IpPrefix.fromIpv6("2400:6500:0:7500::/56"));
        ipPrefixTree.put(IpPrefix.fromIpv6("2400:6500:0:7700::/56"));


        assertThat(ipPrefixTree.contains(ipv6("2400:6500:0:6F00::0"))).isFalse();

        assertThat(ipPrefixTree.contains(ipv6("2400:6500:0:7000::0"))).isTrue();
        assertThat(ipPrefixTree.contains(ipv6("2400:6500:0:7000::FFFF"))).isTrue();
        assertThat(ipPrefixTree.contains(ipv6("2400:6500:0:70FF:FFFF:FFFF:FFFF:FFFF"))).isTrue();


        assertThat(ipPrefixTree.contains(ipv6("2400:6500:0:7100::0"))).isFalse();
    }

    @Test
    public void should_check_ipv4_prefixes() throws IOException {
        final File file = ResourceUtils.getFile("classpath:ip-ranges.json");
        final IpTreeBuilder ipTreeBuilder = IpTreeBuilder.fromFile(file);

        IpTree<Ipv4Address> ipPrefixTree = ipTreeBuilder.buildIpv4Tree();

        List<String> ipv4Prefixes = ipTreeBuilder.ipRanges().getIpv4Prefixes();

        ipv4Prefixes.forEach(prefix -> {
            final IpPrefix<Ipv4Address> ipPrefix = IpPrefix.fromIpv4(prefix);
            assertThat(ipPrefixTree.contains(ipPrefix.getAddress()))
                    .describedAs("Expected %s to be in tree", ipPrefix.toString())
                    .isTrue();
        });
    }

    @Test
    public void should_check_ipv6_prefixes() throws IOException {
        final File file = ResourceUtils.getFile("classpath:ip-ranges.json");
        final IpTreeBuilder ipTreeBuilder = IpTreeBuilder.fromFile(file);

        IpTree<Ipv6Address> ipPrefixTree = ipTreeBuilder.buildIpv6Tree();

        List<String> ipv6Prefixes = ipTreeBuilder.ipRanges().getIpv6Prefixes();

        ipv6Prefixes.forEach(prefix -> {
            final IpPrefix<Ipv6Address> ipPrefix = IpPrefix.fromIpv6(prefix);
            assertThat(ipPrefixTree.contains(ipPrefix.getAddress()))
                    .describedAs("Expected %s to be in tree", ipPrefix.toString())
                    .isTrue();
        });
    }

    private Ipv4Address ipv4(String ip) {
        return (Ipv4Address) IpAddress.fromIp(ip);
    }

    private Ipv6Address ipv6(String ip) {
        return (Ipv6Address) IpAddress.fromIp(ip);
    }
}