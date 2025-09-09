package org.Modules;

import com.google.common.hash.Hashing;

import java.util.Random;

public class MurMurHashUtils {
    private int SEEDs[];

    public MurMurHashUtils(int seed, int num)
    {
        SEEDs = new int[num];
        for(int i = 0; i<num;i++)
        {
            SEEDs[i] = seed+i;
        }

    }

    public long hash(int index, byte[] bytes)
    {
        return  Hashing.murmur3_32_fixed(SEEDs[index]).newHasher().putBytes(bytes).hash().padToLong();
    }

    public long hash(int index, long data){
        return Hashing.murmur3_32_fixed(SEEDs[index]).newHasher().putLong(data).hash().padToLong();
    }

}
