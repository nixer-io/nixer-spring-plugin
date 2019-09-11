package eu.xword.nixer.nixerplugin.ip.tree;

public interface IpTree {

    void put(IpPrefix ipPrefix);

    boolean contains(int ip);

    interface IpTreeFactory {
        IpTree create();
    }
}
