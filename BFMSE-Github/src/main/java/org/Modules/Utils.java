package org.Modules;

import java.util.ArrayList;
import java.util.BitSet;

public class Utils {

    public static long BitSetToLong(MyBitset bitSet) {
        long result = 0L;
        int x=0;
        if(!bitSet.get(0)){
            for (int i = 63; i >=1; i--) {
                if (bitSet.get(i)) {
                    result = result + (1L << (x));
                }
                x++;
            }
        }
        else{
            for (int i = 63; i >=1; i--) {
                if (!bitSet.get(i)) {
                    result = result - (1L << (x));
                }
                x++;
            }
            result=result-1;
        }

        return result;
    }

    public static long[] BitSetToLongArray(MyBitset mybitSet){
        if(mybitSet.size()==0){
            return new long[1];
        }
        int length= (int)(Math.ceil(mybitSet.length()/64.0)*64);
        MyBitset Padding64=new MyBitset(length);
        long[] ret=new long[length/64];

        for (int i=mybitSet.nextSetBit(0);i>=0;i = mybitSet.nextSetBit(i + 1))
            Padding64.set(i);
        for (int i=0;i<ret.length;i++){
            ret[i]= BitSetToLong(Padding64.get(i*64,(i+1)*64));
        }
        return ret;
    }

    public static MyBitset longToBitSet(long value) {
        MyBitset ret = new MyBitset(64);
        for (int i = 0; i < 64; i++) {
            if ((value & (1L << i)) != 0) {
                ret.set(63-i);
            }
        }
        return ret;
    }

    public static MyBitset longToBitSet(long value,int BitSet_length) {
        MyBitset ret = new MyBitset(BitSet_length);

        for (int i = 0; i < BitSet_length; i++) {
            if ((value & (1L << i)) != 0) {
                ret.set(BitSet_length-1-i);
            }
        }
        return ret;
    }

    public static MyBitset ExtractBits(long[] longArray, int x, int y) {
        MyBitset ret = new MyBitset(y - x);
        if (x < 0 || y <= x || y > longArray.length * 64) {
            return ret;
        }

        int startLongIndex = x / 64;
        int startBitIndex = x % 64;

        int endLongIndex = (y-1) / 64;
        int endBitIndex = (y-1) % 64;
        int bitIndex = 0;
        for (int i = startLongIndex; i <= endLongIndex; i++) {
            long currentLong = longArray[i];
            BitSet temp=longToBitSet(currentLong);
            int fromBit = (i == startLongIndex) ? startBitIndex : 0;
            int toBit = (i == endLongIndex) ? endBitIndex : 63;
            for (int j = fromBit; j <= toBit; j++) {
                if (temp.get(j)) {
                    ret.set(bitIndex);
                }
                bitIndex++;
            }
        }
        return ret;
    }

    public static MyBitset ConcatenateBitSets(MyBitset bitSet1, MyBitset bitSet2) {
        MyBitset result = new MyBitset(bitSet1.length() + bitSet2.length());
        for (int i = bitSet1.nextSetBit(0); i >= 0; i = bitSet1.nextSetBit(i + 1)) {
            result.set(i);
        }

        for (int i = bitSet2.nextSetBit(0); i >= 0; i = bitSet2.nextSetBit(i + 1)) {
            result.set(i + bitSet1.length());
        }

        return result;
    }

    public static BitSet My_xor(MyBitset bitSet1, MyBitset bitSet2){
            MyBitset result;
            if(bitSet1.length()<bitSet2.length()){
                result=bitSet2.get(0,bitSet1.length());
                result.xor(bitSet1);
            }else {
                result=bitSet1.get(0,bitSet2.length());
                result.xor(bitSet2);
            }
            return result;
    }

    public static ArrayList<Integer> SplitBitSetToIntList(long[] data, int StartPos, int EndPos, int Set_bit_length){
        ArrayList<Integer> ret=new ArrayList<>();
        if(data.length == 0 && EndPos==StartPos )
            return ret;
        else if (data.length == 0){
            ret.add(0);
            return ret;
        }

        int index_0=StartPos/64,index_1=Set_bit_length-1,temp=0;
        long CurrentLong=data[index_0];

        for (int i=StartPos; i<EndPos ;i++){
            if(i >= data.length*64){
                ret.add(temp);
                break;
            }

            if(i/64 != index_0){
                index_0=i/64;
                CurrentLong=data[index_0];
            }

            if((CurrentLong & (1L<<(i%64))) != 0){
                temp=temp | 1<<index_1 ;
            }

            index_1--;
            if(index_1 < 0)
            {
                ret.add(temp);
                temp=0;
                index_1=Set_bit_length-1;
            }
        }

        return ret;
    }


    public static MyBitset Bitset_to_MyBitset(BitSet set, int len) {
        MyBitset bits = new MyBitset(len);
        bits.or(set);
        return bits;
    }

    public static void CopyBitset0ToBitset1(MyBitset bitset_0,MyBitset bitset_1,int StartPos){
        int i=0;
        while (i<bitset_0.length() && (i+StartPos)<bitset_1.length()){
            bitset_1.set(i+StartPos, bitset_0.get(i));
            i++;
        }
    }

    public static MyBitset ExtractBits(long data, int x, int y) {
        MyBitset ret = new MyBitset(y - x);
        if (x < 0 || y <= x || y >  64) {
            return ret;
        }
        MyBitset temp=longToBitSet(data);
        for (int i=x;i<y;i++){
            if(temp.get(i))
                ret.set(i-x);
        }
        return ret;
    }

    public static long ExtractLong(int x, int y) {
        long ret = 0;
        if (x < 0 || y < 0 || y > 63 || x > 63 || x > y) {
            return ret;
        }
        for (int i = x; i <= y; i++)
            ret = ret | 1L << (63 - i);
        return ret;
    }
}
