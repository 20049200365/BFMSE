package org.Modules;

import java.util.BitSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MyBitset extends BitSet {
    private int len;

    public MyBitset(int len)
    {
        super(len);
        this.len = len;
    }

    @Override
    public int length() {
        return len;
    }

    public MyBitset get(int fromIndex, int toIndex) {
        BitSet set = super.get(fromIndex, toIndex);
        int index_length = toIndex - fromIndex;
        return Utils.Bitset_to_MyBitset(set,index_length);
    }

    public String toString() {
        if (this == null) {
            return null;
        }
        return IntStream.range(0, this.length())
                .mapToObj(b -> String.valueOf(this.get(b) ? 1 : 0))
                .collect(Collectors.joining());
    }


}
