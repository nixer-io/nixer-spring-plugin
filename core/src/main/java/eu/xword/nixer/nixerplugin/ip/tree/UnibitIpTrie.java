package eu.xword.nixer.nixerplugin.ip.tree;

import eu.xword.nixer.nixerplugin.ip.net.IpAddress;
import eu.xword.nixer.nixerplugin.ip.net.IpPrefix;
import org.springframework.util.Assert;

public class UnibitIpTrie<T extends IpAddress> implements IpTree<T> {

    private Node root;

    private static final class Node {
        private Node left;
        private Node right;
        private boolean leaf;
    }

    // TODO consider detecting prefixes duplicates/over-lapping
    public void put(IpPrefix<T> prefix) {
        Assert.notNull(prefix, "Prefix must not be null");

        root = put(root, prefix, 0);
    }

    private Node put(Node x, IpPrefix<T> prefix, int counter) {
        if (x == null) {
            x = new Node();
        }

        if (counter == prefix.getMask()) {
            x.leaf = true;
            return x;
        }

        if (prefix.getAddress().getBit(counter)) {
            x.right = put(x.right, prefix, counter + 1);
        } else {
            x.left = put(x.left, prefix, counter + 1);
        }

        return x;
    }

    public boolean contains(T ip) {
        Assert.notNull(ip, "Ip must not be null");

        return search(root, ip, 0) != null;
    }

    private Node search(Node x, T ip, int counter) {
        if (x == null) {
            return null;
        }

        if (x.leaf) {
            return x;
        }
        if (ip.getBit(counter)) {
            return search(x.right, ip, counter + 1);
        } else {
            return search(x.left, ip, counter + 1);
        }

    }

}
