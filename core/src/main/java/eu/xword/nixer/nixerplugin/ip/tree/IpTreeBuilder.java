package eu.xword.nixer.nixerplugin.ip.tree;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.xword.nixer.nixerplugin.ip.range.IpRanges;

public class IpTreeBuilder {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.findAndRegisterModules();
    }

    private IpRanges ipRanges;
    private IpTree.IpTreeFactory ipTreeFactory = IpTreeFactories.UNIBIT;

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

    public IpTree buildIpv4Tree() {
        final IpTree ipTree = ipTreeFactory.create();

        ipRanges.getRanges().forEach(ranges -> {
            for (String prefix : ranges.getIpv4Prefixes()) {
                ipTree.put(IpPrefix.fromIp(prefix));
            }
        });

        return ipTree;
    }

    public IpTree buildIpv6Tree() {
        final IpTree ipTree = ipTreeFactory.create();
        return ipTree;
//        ipRanges.getRanges().forEach(ranges -> {
//            for (String prefix : ranges.getIpv6Prefixes()) {
//                ipTree.put(IpPrefix.fromIp(prefix));
//            }
//        });

//        return ipTree;
    }
}
