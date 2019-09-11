package eu.xword.nixer.nixerplugin.ip.tree;

import com.google.common.collect.Range;
import com.google.common.collect.TreeRangeSet;

public class GuavaIpRangeTree implements IpTree {

    private final TreeRangeSet<Integer> ranges = TreeRangeSet.create();

    public void put(IpPrefix prefix) {
        ranges.add(Range.closed(prefix.getAddress(), prefix.getAddress() | (~prefix.getNetmask())));
    }

    public boolean contains(int ip) {
        return ranges.contains(ip);
    }

}
