package eu.xword.nixer.nixerplugin.ip.tree;

public class UnibitIpTrie implements IpTree {

    private Node root;

    private static final class Node {
        private Node left;
        private Node right;
        private boolean leaf;
    }

    public void put(IpPrefix prefix) {
        root = put(root, prefix, 0);
    }

    private Node put(Node x, IpPrefix prefix, int counter) {
        if (x == null) {
            x = new Node();
        }

        if (counter == prefix.getMask()) {
            x.leaf = true;
            return x;
        }
        if ((prefix.getAddress() & (1 << (31 - counter))) == 0) {
            x.left = put(x.left, prefix, counter + 1);
        } else {
            x.right = put(x.right, prefix, counter + 1);
        }

        return x;
    }

    public boolean contains(int ip) {
        return search(root, ip, 0) != null;
    }

    private Node search(Node x, int ip, int counter) {
        if (x == null) {
            return null;
        }

        if (x.leaf) {
            return x;
        }
        if ((ip & (1 << (31 - counter))) == 0) {
            return search(x.left, ip, counter + 1);
        } else {
            return search(x.right, ip, counter + 1);
        }

    }

}
