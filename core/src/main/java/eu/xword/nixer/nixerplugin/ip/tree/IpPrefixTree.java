package eu.xword.nixer.nixerplugin.ip.tree;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class IpPrefixTree implements IpTree {

    private final HashMap<Byte, IpPrefixTree> nodes = new HashMap<>();
    private int prefix;

    public void put(IpPrefix prefix) {
        if (prefix.getAddress() == 0) {
            this.prefix = prefix.getMask();
            return;
        }
        byte octet = (byte) ((prefix.getAddress() & (0xff << (3 * 8))) >>> (3 * 8));
        final IpPrefixTree tree = nodes.getOrDefault(octet, new IpPrefixTree());
        nodes.put(octet, tree);
        tree.put(new IpPrefix(prefix.getAddress() << 8, prefix.getMask()));
    }

    public List<IpPrefix> traverse() {
        final LinkedList<IpPrefix> prefixes = new LinkedList<>();

        traverse(IpPrefixCreator.creator(), prefixes);

        return prefixes;
    }

    private void traverse(IpPrefixCreator prefixCreator, List<IpPrefix> results) {
        if (nodes.isEmpty()) {
            results.add(prefixCreator.build(prefix));
        }
        nodes.forEach((key, value) -> {
            final IpPrefixCreator prefixCopy = prefixCreator.copy().addOctet(key);
            value.traverse(prefixCopy, results);
        });
    }

    public boolean contains(int ip) {
        if (ip == 0 && prefix > 0) {
            return true;
        }
//        final int startAddress = ip & prefix;
        byte octet = (byte) ((ip & (0xff << (3 * 8))) >>> (3 * 8));
        final IpPrefixTree tree = nodes.get(octet);
        return tree != null && tree.contains(ip << 8);
    }

}
