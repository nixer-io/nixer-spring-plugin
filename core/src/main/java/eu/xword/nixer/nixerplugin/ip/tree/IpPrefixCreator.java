package eu.xword.nixer.nixerplugin.ip.tree;

public class IpPrefixCreator {

    private byte[] octets = new byte[4];
    private int index = 3;

    private IpPrefixCreator() {
    }

    private IpPrefixCreator(final byte[] octets, final int index) {
        this.octets = octets;
        this.index = index;
    }

    public static IpPrefixCreator creator() {
        return new IpPrefixCreator();
    }

    public IpPrefixCreator copy() {
        return new IpPrefixCreator(octets, index);
    }

    public IpPrefixCreator addOctet(byte octet) {
        if (index < 0) {
            throw new IllegalStateException("Method called to many times");
        }
        octets[index--] = octet;
        return this;
    }

    public IpPrefix build(int prefix) {
        int address = octets[3] & 0xff;
        address <<= 8;
        address |= octets[2] & 0xff;
        address <<= 8;
        address |= octets[1] & 0xff;
        address <<= 8;
        address |= octets[0] & 0xff;

        return new IpPrefix(address, prefix);
    }

}
