package org.Modules;
import org.openjdk.jol.info.GraphLayout;

import java.util.*;

public class Dynamic_BFMSE {
    private static final int ARITY = 3;

    private final int segmentCount;
    private final int segmentCountLength;
    private final int segmentLength;
    private final int segmentLengthMask;
    private final int arrayLength;
    public int total_bit;

    private int fingerprints_bit_len;
    private long Mod_fingerprints;
    private int Set_bit_length;

    private Random random=new Random(0);
    private long seed=random.nextLong();
    private ArrayList<Integer>[] values_in_array;

    private long[] fingerprint_in_array;
    private int[] hash_index_in_array;
    private int[] invoke_record;

    public Dynamic_BFMSE(int segmentCount, int segmentLength, int fingerprint_length) {
        if (segmentLength < 0 || Integer.bitCount(segmentLength) != 1) {
            throw new IllegalArgumentException("Segment length needs to be a power of 2, is " + segmentLength);
        }
        if (segmentCount <= 0) {
            throw new IllegalArgumentException("Illegal segment count: " + segmentCount);
        }
        this.segmentLength = segmentLength;
        this.segmentCount = segmentCount;
        this.segmentLengthMask = segmentLength - 1;
        this.segmentCountLength = segmentCount * segmentLength;
        this.arrayLength = (segmentCount + ARITY - 1) * segmentLength;
        values_in_array=new ArrayList[this.arrayLength];
        fingerprint_in_array=new long[this.arrayLength];
        hash_index_in_array=new int[this.arrayLength];

        for (int i=0;i<values_in_array.length;i++)
            values_in_array[i]=new ArrayList<>();
        this.fingerprints_bit_len=fingerprint_length;
        this.Mod_fingerprints=1L<<fingerprint_length;

        this.invoke_record=new int[arrayLength];
        System.out.println("Fingerprint length:" + fingerprint_length);
    }

    static int calculateSegmentLength(int arity, int size) {
        int segmentLength;
        if (arity == 3) {
            segmentLength = 1 << (int) Math.floor(Math.log(size) / Math.log(3.33) + 2.11);
        } else if (arity == 4) {
            segmentLength = 1 << (int) Math.floor(Math.log(size) / Math.log(2.91) - 0.5);
        } else {
            segmentLength = 65536;
        }
        return segmentLength;
    }

    static double calculateSizeFactor(int arity, int size) {
        double sizeFactor;
        if (arity == 3) {
            sizeFactor = Math.max(1.125, 0.875 + 0.25 * Math.log(1000000) / Math.log(size));
        } else if (arity == 4) {
            sizeFactor = Math.max(1.075, 0.77 + 0.305 * Math.log(600000) / Math.log(size));
        } else {
            sizeFactor = 2.0;
        }
        return sizeFactor;
    }

    public static Dynamic_BFMSE construct(int[] keys, ArrayList<Integer>[] values, int Max_set_num, double fpp) {
        int size = keys.length;
        int segmentLength = calculateSegmentLength(ARITY, size);
        if (segmentLength > (1 << 18)) {
            segmentLength = (1 << 18);
        }
        double sizeFactor = calculateSizeFactor(ARITY, size);
        int capacity = (int) (size * sizeFactor);
        int segmentCount = (capacity + segmentLength - 1) / segmentLength - (ARITY - 1);
        int arrayLength = (segmentCount + ARITY - 1) * segmentLength;
        segmentCount = (arrayLength + segmentLength - 1) / segmentLength;
        segmentCount = segmentCount <= ARITY - 1 ? 1 : segmentCount - (ARITY - 1);
        int fingerprint_length=(int)(-Math.log(fpp)/Math.log(2.0));

        System.out.println("Theoretical FPP rate: "+keys.length*Math.pow(2,-fingerprint_length));

        if(fingerprint_length>64)
            fingerprint_length=64;
        if(fingerprint_length<=0){
            throw new ArrayIndexOutOfBoundsException("Fpp is set to an error value");
        }

        Dynamic_BFMSE filter = new Dynamic_BFMSE(segmentCount, segmentLength,fingerprint_length);
        filter.addAll(keys,values, Max_set_num);
        return filter;
    }

    public static Dynamic_BFMSE construct_empty(int data_size, double fpp){
        int segmentLength = calculateSegmentLength(ARITY, data_size);
        if (segmentLength > (1 << 18)) {
            segmentLength = (1 << 18);
        }
        double sizeFactor = calculateSizeFactor(ARITY, data_size);
        int capacity = (int) (data_size * sizeFactor);
        int segmentCount = (capacity + segmentLength - 1) / segmentLength - (ARITY - 1);
        int arrayLength = (segmentCount + ARITY - 1) * segmentLength;
        segmentCount = (arrayLength + segmentLength - 1) / segmentLength;
        segmentCount = segmentCount <= ARITY - 1 ? 1 : segmentCount - (ARITY - 1);
        int fingerprint_length=(int)(-Math.log(fpp)/Math.log(2.0));
        if(fingerprint_length>64)
            fingerprint_length=64;
        if(fingerprint_length<=0){
            throw new ArrayIndexOutOfBoundsException("Fpp is set to an error value");
        }

        return new Dynamic_BFMSE(segmentCount, segmentLength,fingerprint_length);
    }

    public static Dynamic_BFMSE construct(int[] keys, ArrayList<Integer>[] values, int Max_set_num, int bit_length) {
        int size = keys.length;
        int segmentLength = calculateSegmentLength(ARITY, size);
        if (segmentLength > (1 << 18)) {
            segmentLength = (1 << 18);
        }
        double sizeFactor = calculateSizeFactor(ARITY, size);
        int capacity = (int) (size * sizeFactor);
        int segmentCount = (capacity + segmentLength - 1) / segmentLength - (ARITY - 1);
        int arrayLength = (segmentCount + ARITY - 1) * segmentLength;
        segmentCount = (arrayLength + segmentLength - 1) / segmentLength;
        segmentCount = segmentCount <= ARITY - 1 ? 1 : segmentCount - (ARITY - 1);

        int x0=0,x1=(int)(Math.log(Max_set_num)/Math.log(2));
        for (int i=0;i<values.length;i++){
            x0=x0+x1*values[i].size();
        }

        int fingerprint_length=(int)Math.floor((bit_length-x0)*1.0/arrayLength)-2;
        System.out.println("Theoretical False positive rate: "+keys.length*Math.pow(2,-fingerprint_length));
        if(fingerprint_length>63)
            fingerprint_length=63;

        if(fingerprint_length<=0 ){
            throw new ArrayIndexOutOfBoundsException("Bit length is too small");
        }
        Dynamic_BFMSE filter = new Dynamic_BFMSE(segmentCount, segmentLength,fingerprint_length);
        filter.addAll(keys,values, Max_set_num);
        return filter;
    }

    public Boolean Is_fingerprint_collision(int[] keys,int inserted_number){
        HashSet<Long> temp=new HashSet<>();
        for (int i=0;i<inserted_number;i++){
            long fingerprint=Hash.Generate_fingerprint(keys[i],seed,Mod_fingerprints);
            if(temp.contains(fingerprint))
                return true;
            else
                temp.add(fingerprint);
        }
        return false;
    }

    public ArrayList<Integer> query(int key){
        ArrayList<Integer> ret=new ArrayList<>();
        long fingerprint=Hash.Generate_fingerprint(key,seed,Mod_fingerprints);
        long hash = Hash.hash64(fingerprint,seed);
        int[] Hash_set=new int[3];
        Hash_set[0] = Hash.reduce((int) (hash >>> 32), segmentCountLength);
        Hash_set[1] = Hash_set[0] + segmentLength;
        Hash_set[2] = Hash_set[1] + segmentLength;
        Hash_set[1] ^= (int) ((hash >> 18) & segmentLengthMask);
        Hash_set[2] ^= (int) ((hash) & segmentLengthMask);

        for (int i=0;i<3;i++){
            long Stored_fingerprint=fingerprint_in_array[Hash_set[i]];
            int hash_index= hash_index_in_array[Hash_set[i]];

            if(Stored_fingerprint==fingerprint && hash_index==(i+1)){
                ret=values_in_array[Hash_set[i]];
            }
        }

        return ret;
    }

    public boolean update(int key_0, int key_1, ArrayList<Integer> updated_values){
        boolean ret=false;
        long fingerprint=Hash.Generate_fingerprint(key_0,seed,Mod_fingerprints);
        long hash = Hash.hash64(fingerprint,seed);
        int[] Hash_set=new int[3];
        Hash_set[0] = Hash.reduce((int) (hash >>> 32), segmentCountLength);
        Hash_set[1] = Hash_set[0] + segmentLength;
        Hash_set[2] = Hash_set[1] + segmentLength;
        Hash_set[1] ^= (int) ((hash >> 18) & segmentLengthMask);
        Hash_set[2] ^= (int) ((hash) & segmentLengthMask);

        for (int i=0;i<3;i++){
            long Stored_fingerprint=fingerprint_in_array[Hash_set[i]];
            int hash_index= hash_index_in_array[Hash_set[i]];
            if(Stored_fingerprint==fingerprint && hash_index==(i+1)){
                ret=true;
                fingerprint_in_array[Hash_set[i]]=0;
                hash_index_in_array[Hash_set[i]]=0;
                values_in_array[Hash_set[i]].clear();
                Add(key_1,updated_values,100);
            }
        }
        return ret;
    }

    public Boolean query_fpp_test(int key){
        Boolean ret=false;
        long fingerprint=Hash.Generate_fingerprint(key,seed,Mod_fingerprints);
        long hash = Hash.hash64(fingerprint,seed);
        int[] Hash_set=new int[3];
        Hash_set[0] = Hash.reduce((int) (hash >>> 32), segmentCountLength);
        Hash_set[1] = Hash_set[0] + segmentLength;
        Hash_set[2] = Hash_set[1] + segmentLength;
        Hash_set[1] ^= (int) ((hash >> 18) & segmentLengthMask);
        Hash_set[2] ^= (int) ((hash) & segmentLengthMask);

        for (int i=0;i<3;i++){
            long Stored_fingerprint=fingerprint_in_array[Hash_set[i]];
            int hash_index= hash_index_in_array[Hash_set[i]];

            if(Stored_fingerprint==fingerprint && hash_index==(i+1)){
                ret=true;
            }
        }

        if(ret)
            invoke_record[Hash_set[0]]++;
        return ret;
    }

    public void delete(int key){
        long fingerprint=Hash.Generate_fingerprint(key,seed,Mod_fingerprints);
        long hash = Hash.hash64(fingerprint,seed);
        int[] Hash_set=new int[3];
        Hash_set[0] = Hash.reduce((int) (hash >>> 32), segmentCountLength);
        Hash_set[1] = Hash_set[0] + segmentLength;
        Hash_set[2] = Hash_set[1] + segmentLength;
        Hash_set[1] ^= (int) ((hash >> 18) & segmentLengthMask);
        Hash_set[2] ^= (int) ((hash) & segmentLengthMask);


        for (int i=0;i<3;i++){
            long Stored_fingerprint=fingerprint_in_array[Hash_set[i]];
            int Stored_hash_index=hash_index_in_array[Hash_set[i]];
            int hash_index= 0;

            if(Stored_fingerprint==fingerprint && Stored_hash_index==(i+1)){
                fingerprint_in_array[Hash_set[i]]=0;
                hash_index_in_array[Hash_set[i]]=0;
                values_in_array[Hash_set[i]].clear();
            }
        }
    }

    public Boolean Add(int key, ArrayList<Integer> values,int max_evicted_number){
        long fingerprint=Hash.Generate_fingerprint(key,seed,Mod_fingerprints);
        long hash = Hash.hash64(fingerprint,seed);
        int[] Hash_set=new int[3];
        Boolean is_add_success=false;
        Hash_set[0] = Hash.reduce((int) (hash >>> 32), segmentCountLength);
        Hash_set[1] = Hash_set[0] + segmentLength;
        Hash_set[2] = Hash_set[1] + segmentLength;
        Hash_set[1] ^= (int) ((hash >> 18) & segmentLengthMask);
        Hash_set[2] ^= (int) ((hash) & segmentLengthMask);

        for (int i=0;i<3;i++){
            if(fingerprint_in_array[Hash_set[i]]==0 && hash_index_in_array[Hash_set[i]]==0 && values_in_array[Hash_set[i]].isEmpty()){
                fingerprint_in_array[Hash_set[i]]=fingerprint;
                hash_index_in_array[Hash_set[i]]=i+1;
                values_in_array[Hash_set[i]]=values;
                is_add_success=true;
                break;
            }
        }

        if(!is_add_success){
            Random random=new Random();
            int hash_evict_index=random.nextInt(3);
            int evict_index=Hash_set[hash_evict_index];
            return evict(fingerprint,hash_evict_index+1,values,evict_index,max_evicted_number);
        }else {
            return true;
        }
    }

    public Boolean evict(long fingerprint, int hash_index, ArrayList<Integer> values, int index, int max_evicted_number){

        if(max_evicted_number<0)
            return false;

        if(fingerprint_in_array[index]==0 && hash_index_in_array[index]==0 && values_in_array[index].isEmpty()){
            fingerprint_in_array[index]=fingerprint;
            hash_index_in_array[index]=hash_index;
            values_in_array[index]=values;
            return true;
        }else {
            long evicted_fingerprint=fingerprint_in_array[index];
            int evicted_hash_index=hash_index_in_array[index];
            ArrayList<Integer> evicted_value=values_in_array[index];

            fingerprint_in_array[index]=fingerprint;
            hash_index_in_array[index]=hash_index;
            values_in_array[index]=values;

            long hash = Hash.hash64(evicted_fingerprint,seed);
            int[] Hash_set=new int[3];
            Hash_set[0] = Hash.reduce((int) (hash >>> 32), segmentCountLength);
            Hash_set[1] = Hash_set[0] + segmentLength;
            Hash_set[2] = Hash_set[1] + segmentLength;
            Hash_set[1] ^= (int) ((hash >> 18) & segmentLengthMask);
            Hash_set[2] ^= (int) ((hash) & segmentLengthMask);

            Random random=new Random();
            int next_hash_index;
            do {
                next_hash_index = 1+random.nextInt(3);
            } while (next_hash_index == evicted_hash_index);

            return evict(evicted_fingerprint,next_hash_index,evicted_value,Hash_set[next_hash_index-1],max_evicted_number-1);
        }
    }

    private void addAll(int[] keys, ArrayList<Integer>[] values, int Max_set_num){
        if(keys.length!=values.length){
            throw new IllegalArgumentException("Data errors");
        }
        int size=keys.length;
        long[] reverseOrder = new long[size + 1];
        byte[] reverseH = new byte[size];
        int reverseOrderPos = 0;
        HashMap<Long, Integer> Fingerprint_to_Main_position=new HashMap<>();
        byte[] t2count = new byte[arrayLength];
        long[] t2hash = new long[arrayLength];
        int[] alone = new int[arrayLength];
        int hashIndex = 0;

        int[] h012 = new int[5];
        int blockBits = 1;
        while ((1 << blockBits) < segmentCount) {
            blockBits++;
        }
        int block = 1 << blockBits;

        while (true) {
            reverseOrder[size] = 1;
            int[] startPos = new int[block];
            for (int i = 0; i < 1 << blockBits; i++) {
                startPos[i] = (int) ((long) i * size / block);
            }

            for (int i=0;i<keys.length;i++) {
                long hash = Hash.Generate_fingerprint(keys[i],seed,this.Mod_fingerprints);
                int segmentIndex = (int) (hash >>> (64 - blockBits));
                while (reverseOrder[startPos[segmentIndex]] != 0) {
                    segmentIndex++;
                    segmentIndex &= (1 << blockBits) - 1;
                }
                reverseOrder[startPos[segmentIndex]] = hash;
                startPos[segmentIndex]++;
            }

            byte countMask = 0;
            for (int i = 0; i < size; i++) {
                long hash = reverseOrder[i];
                for (int hi = 0; hi < 3; hi++) {
                    int index = getHashFromHash(hash, hi);
                    t2count[index] += 4;
                    t2count[index] ^= hi;
                    t2hash[index] ^= hash;
                    countMask |= t2count[index];
                }
            }
            startPos = null;
            if (countMask < 0) {
                fingerprint_in_array=new long[arrayLength];
                hash_index_in_array=new int[arrayLength];
                return;
            }

            reverseOrderPos = 0;
            int alonePos = 0;
            for (int i = 0; i < arrayLength; i++) {
                alone[alonePos] = i;
                int inc = (t2count[i] >> 2) == 1 ? 1 : 0;
                alonePos += inc;
            }

            while (alonePos > 0) {
                alonePos--;
                int index = alone[alonePos];
                if ((t2count[index] >> 2) == 1) {
                    long hash = t2hash[index];
                    byte found = (byte) (t2count[index] & 3);

                    reverseH[reverseOrderPos] = found;
                    reverseOrder[reverseOrderPos] = hash;
                    h012[0] = getHashFromHash(hash, 0);
                    h012[1] = getHashFromHash(hash, 1);
                    h012[2] = getHashFromHash(hash, 2);

                    int index3 = h012[mod3(found + 1)];
                    alone[alonePos] = index3;
                    alonePos += ((t2count[index3] >> 2) == 2 ? 1 : 0);
                    t2count[index3] -= 4;
                    t2count[index3] ^= mod3(found + 1);
                    t2hash[index3] ^= hash;

                    index3 = h012[mod3(found + 2)];
                    alone[alonePos] = index3;
                    alonePos += ((t2count[index3] >> 2) == 2 ? 1 : 0);
                    t2count[index3] -= 4;
                    t2count[index3] ^= mod3(found + 2);
                    t2hash[index3] ^= hash;

                    reverseOrderPos++;

                    Fingerprint_to_Main_position.put(hash, h012[found]);
                }
            }

            if (reverseOrderPos == size)
            {
                break;
            }
            hashIndex++;
            Arrays.fill(t2count, (byte) 0);
            Arrays.fill(t2hash, 0);
            Arrays.fill(reverseOrder, 0);

            if (hashIndex > 100) {
                fingerprint_in_array=new long[arrayLength];
                hash_index_in_array=new int[arrayLength];
                System.out.println("Construct fails");
                return;
            }
            seed = random.nextLong();
            Hash.setSeed(seed);
        }

        alone = null;
        t2count = null;
        t2hash = null;

        for (int i = reverseOrderPos - 1; i >= 0; i--) {
            long hash = reverseOrder[i];
            int found = reverseH[i];
            h012[0] = getHashFromHash(hash, 0);
            h012[1] = getHashFromHash(hash, 1);
            h012[2] = getHashFromHash(hash, 2);
            fingerprint_in_array[h012[found]]=hash;
            hash_index_in_array[h012[found]]=found+1;
        }


        Set_bit_length=(int)(Math.ceil(Math.log(Max_set_num)/Math.log(2)));
        for (int i=0;i<keys.length;i++){
            int position=Fingerprint_to_Main_position.get(Hash.Generate_fingerprint(keys[i],seed,Mod_fingerprints));
            values_in_array[position].addAll(values[i]);
        }
    }

    private static int mod3(int x) {
        if (x > 2) {
            x -= 3;
        }
        return x;
    }

    private int getHashFromHash(long x, int index) {
        long hash=Hash.hash64(x,seed);
        long h = Hash.reduce((int) (hash >>> 32), segmentCountLength);
        h += (long) index * segmentLength;
        long hh = hash & ((1L << 36) - 1);
        h ^= (int) ((hh >>> (36 - 18 * index)) & segmentLengthMask);
        return (int) h;
    }

    public void MemoryUsageEvaluate(){
        long x0= GraphLayout.parseInstance(hash_index_in_array).totalSize();
        long x1=GraphLayout.parseInstance(fingerprint_in_array).totalSize();
        int[][] twoDArray = Arrays.stream(values_in_array)
                .map(list -> list == null ? new int[0] : list.stream().mapToInt(Integer::intValue).toArray())
                .toArray(int[][]::new);

        long x2=GraphLayout.parseInstance(new Object[]{twoDArray}).totalSize();

        System.out.println("Memory usage: "+(x0+x1+x2)/1024.0/1024.0+ " MB");

    }
}
