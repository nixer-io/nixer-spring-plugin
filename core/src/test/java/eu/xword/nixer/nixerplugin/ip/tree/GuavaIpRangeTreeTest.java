package eu.xword.nixer.nixerplugin.ip.tree;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;
import com.google.common.net.InetAddresses;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import static org.assertj.core.api.Assertions.assertThat;

class GuavaIpRangeTreeTest {

    private IpTree ipPrefixTree;

    @BeforeEach
    public void setup() {
        this.ipPrefixTree = new GuavaIpRangeTree();
    }

    @Test
    public void should_add_ip_prefix() {
        ipPrefixTree.put(IpPrefix.fromIp("108.175.52.0/22"));
        ipPrefixTree.put(IpPrefix.fromIp("108.175.56.0/22"));
        ipPrefixTree.put(IpPrefix.fromIp("103.4.8.0/21"));
        ipPrefixTree.put(IpPrefix.fromIp("13.248.100.0/24"));

//        assertThat(ipPrefixTree.traverse()).contains(
//                IpPrefix.fromIp("108.175.52.0/22"),
//                IpPrefix.fromIp("108.175.56.0/22"),
//                IpPrefix.fromIp("103.4.8.0/21"),
//                IpPrefix.fromIp("13.248.100.0/24")
//        );
    }

    @Test
    public void should_contain_both_starting_and_ending_address() {
        ipPrefixTree.put(IpPrefix.fromIp("108.175.52.0/22"));

        assertThat(ipPrefixTree.contains(ipToInt("108.175.52.0"))).isTrue();
        assertThat(ipPrefixTree.contains(ipToInt("108.175.55.255"))).isTrue();
    }

    @Test
    public void should_load_all_prefixes() throws IOException {
        final File file = ResourceUtils.getFile("classpath:ip-ranges.json");
        final IpTreeBuilder ipTreeBuilder = IpTreeBuilder.fromFile(file);

        this.ipPrefixTree = ipTreeBuilder.buildIpv4Tree();

        List<String> ipv4Prefixes = ipTreeBuilder.ipRanges().getIpv4Prefixes();
//        Collections.sort(ipv4Prefixes);
//
//        final List<String> visitedPrefixes = ipPrefixTree.traverse()
//                .stream()
//                .map(IpPrefix::toString)
//                .sorted()
//                .collect(Collectors.toList());
//
//        Assertions.assertThat(visitedPrefixes).containsAll(ipv4Prefixes);


        final Stopwatch stopwatch = Stopwatch.createStarted();
        final long startTime = System.nanoTime();
//        final List<String> ipsNotFound = new ArrayList<>();
        ipv4Prefixes.forEach(prefix -> {
            final IpPrefix ipPrefix = IpPrefix.fromIp(prefix);


            assertThat(ipPrefixTree.contains(ipPrefix.getAddress())).isTrue()
                    .describedAs("Expected %s to be in tree", ipPrefix.toString());
//            if (!ipPrefixTree.contains(ipPrefix.getAddress())) {
//                ipsNotFound.add(ipPrefix.toString());
//            }
        });

        System.err.println(String.format("Insert time : %dms", stopwatch.elapsed(TimeUnit.MILLISECONDS)));
//        System.out.println(String.format("Found %d/%d", (ipv4Prefixes.size() - ipsNotFound.size()), ipv4Prefixes.size()));
//        System.out.println("Ips not found " + ipsNotFound);
    }

    private int ipToInt(String ip) {
        final InetAddress address = InetAddresses.forString(ip);
        return InetAddresses.coerceToInteger(address);
    }
}