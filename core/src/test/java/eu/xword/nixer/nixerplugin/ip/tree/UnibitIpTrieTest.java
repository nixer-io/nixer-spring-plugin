package eu.xword.nixer.nixerplugin.ip.tree;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import com.google.common.net.InetAddresses;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import static org.assertj.core.api.Assertions.assertThat;

class UnibitIpTrieTest {

    private IpTree ipPrefixTree;

    @BeforeEach
    public void setup() {
        this.ipPrefixTree = new UnibitIpTrie();
    }

    @Test
    public void should_add_ip_prefix() {
        ipPrefixTree.put(IpPrefix.fromIp("108.175.52.0/22"));
        ipPrefixTree.put(IpPrefix.fromIp("108.175.56.0/22"));
        ipPrefixTree.put(IpPrefix.fromIp("108.174.0.0/16"));


        assertThat(ipPrefixTree.contains(ipToInt("108.175.51.255"))).isFalse();
        assertThat(ipPrefixTree.contains(ipToInt("108.175.52.0"))).isTrue();
        assertThat(ipPrefixTree.contains(ipToInt("108.175.53.255"))).isTrue();

        assertThat(ipPrefixTree.contains(ipToInt("108.175.56.255"))).isTrue();
        assertThat(ipPrefixTree.contains(ipToInt("108.175.57.255"))).isTrue();

        assertThat(ipPrefixTree.contains(ipToInt("108.174.0.0"))).isTrue();
        assertThat(ipPrefixTree.contains(ipToInt("108.174.255.255"))).isTrue();
    }

    @Test
    public void should_contain_both_starting_and_ending_address() {
        ipPrefixTree.put(IpPrefix.fromIp("108.175.52.0/22"));

        assertThat(ipPrefixTree.contains(ipToInt("108.175.55.255"))).isTrue();
        assertThat(ipPrefixTree.contains(ipToInt("108.175.52.0"))).isTrue();
    }

    @Test
    public void should_load_all_prefixes() throws IOException {
        final File file = ResourceUtils.getFile("classpath:ip-ranges.json");
        final IpTreeBuilder ipTreeBuilder = IpTreeBuilder.fromFile(file);

        this.ipPrefixTree = ipTreeBuilder.buildIpv4Tree();

        List<String> ipv4Prefixes = ipTreeBuilder.ipRanges().getIpv4Prefixes();

        ipv4Prefixes.forEach(prefix -> {
            final IpPrefix ipPrefix = IpPrefix.fromIp(prefix);
            assertThat(ipPrefixTree.contains(ipPrefix.getAddress())).isTrue()
                    .describedAs("Expected %s to be in tree", ipPrefix.toString());
        });
    }

    private int ipToInt(String ip) {
        final InetAddress address = InetAddresses.forString(ip);
        return InetAddresses.coerceToInteger(address);
    }
}