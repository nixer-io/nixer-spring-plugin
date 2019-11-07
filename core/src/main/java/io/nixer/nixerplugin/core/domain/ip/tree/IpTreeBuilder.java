package io.nixer.nixerplugin.core.domain.ip.tree;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.nixer.nixerplugin.core.domain.ip.net.IpPrefix;
import io.nixer.nixerplugin.core.domain.ip.net.Ipv4Address;
import io.nixer.nixerplugin.core.domain.ip.net.Ipv6Address;
import io.nixer.nixerplugin.core.domain.ip.range.IpRanges;
import io.nixer.nixerplugin.core.domain.ip.net.IpPrefix;
import io.nixer.nixerplugin.core.domain.ip.net.Ipv6Address;

public class IpTreeBuilder {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JavaTimeModule());
    }

    private IpRanges ipRanges;

    public static IpTreeBuilder fromFile(File file) throws IOException {
        return from(readFile(file));
    }

    public static IpTreeBuilder from(IpRanges ipRanges) {
        final IpTreeBuilder builder = new IpTreeBuilder();
        builder.ipRanges = ipRanges;
        return builder;
    }

    private static IpRanges readFile(File file) throws IOException {
        return objectMapper.readValue(file, IpRanges.class);
    }

    public IpRanges ipRanges() {
        return this.ipRanges;
    }

    public IpTree<Ipv4Address> buildIpv4Tree() {
        final IpTree<Ipv4Address> ipTree = new UnibitIpTrie<>();

        ipRanges.getIpv4Prefixes().forEach(prefix -> ipTree.put(IpPrefix.fromIpv4(prefix)));

        return ipTree;
    }

    public IpTree<Ipv6Address> buildIpv6Tree() {
        final IpTree<Ipv6Address> ipTree = new UnibitIpTrie<>();

        ipRanges.getIpv6Prefixes().forEach(prefix -> ipTree.put(IpPrefix.fromIpv6(prefix)));

        return ipTree;
    }
}
