package io.nixer.nixerplugin.core.domain.ip.tree;

import io.nixer.nixerplugin.core.domain.ip.net.IpAddress;
import io.nixer.nixerplugin.core.domain.ip.net.IpPrefix;
import io.nixer.nixerplugin.core.domain.ip.net.IpAddress;
import io.nixer.nixerplugin.core.domain.ip.net.IpPrefix;

/**
 * Represents tree of ip addresses.
 * @param <T> type of ip address
 */
public interface IpTree<T extends IpAddress> {

    void put(IpPrefix<T> ipPrefix);

    boolean contains(T ip);

}
