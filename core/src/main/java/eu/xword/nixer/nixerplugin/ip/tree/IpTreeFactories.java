package eu.xword.nixer.nixerplugin.ip.tree;

import eu.xword.nixer.nixerplugin.ip.tree.IpTree.IpTreeFactory;

public enum IpTreeFactories implements IpTreeFactory {

    GUAVA_RANGE {
        @Override
        public IpTree create() {
            return new GuavaIpRangeTree();
        }
    },
    UNIBIT {
        @Override
        public IpTree create() {
            return new UnibitIpTrie();
        }
    };

    @Override
    public abstract IpTree create();
}
