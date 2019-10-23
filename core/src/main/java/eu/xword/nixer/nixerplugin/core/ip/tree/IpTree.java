package eu.xword.nixer.nixerplugin.core.ip.tree;

import eu.xword.nixer.nixerplugin.core.ip.net.IpAddress;
import eu.xword.nixer.nixerplugin.core.ip.net.IpPrefix;

/**
 * Represents tree of ip addresses.
 * @param <T> type of ip address
 */
public interface IpTree<T extends IpAddress> {

    void put(IpPrefix<T> ipPrefix);

    boolean contains(T ip);

}
