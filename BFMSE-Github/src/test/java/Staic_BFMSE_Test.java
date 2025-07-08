import org.Modules.Static_BFMSE;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class Staic_BFMSE_Test {
    public static void main(String[] args) {
        int data_size=1000,each_set_num=16,Max_set_num=64,bit_length=15000;
        int[] keys=new int[data_size];
        double fpp=Math.pow(2,-17);
        ArrayList<Integer>[] values=new ArrayList[data_size];
        Random random=new Random();
        if(Max_set_num>each_set_num) {
            for (int i = 0; i < data_size; i++) {
                keys[i] = i ;
                values[i] = new ArrayList<>();
                while (true) {
                    values[i].add(Math.abs(random.nextInt()) % Max_set_num);
                    if (values[i].size() == each_set_num) {
                        values[i] = new ArrayList<>(new HashSet<>(values[i]));
                        if (values[i].size() == each_set_num)
                            break;
                    }
                }
            }
        }else {
            throw new ArrayIndexOutOfBoundsException("Max_set_num must larger than each_set_num !");
        }

        Static_BFMSE test= Static_BFMSE.construct(keys,values,Max_set_num,fpp);

        boolean isRight=true;
        for (int i=0;i<data_size;i++){
            ArrayList<Integer> temp=test.query(keys[i]);
            if(!temp.equals(values[i])){
                isRight=false;
                System.out.println(i);
                System.out.println("value: "+values[i]);
                System.out.println("query: "+temp);
                break;
            }
        }
        if(isRight)
            System.out.println("Right!");
        else
            System.out.println("Error!");


        int test_count=1000000,fpp_count=0;
        for (int i=data_size;i<data_size+test_count;i++){
            Boolean test_value= test.query_fpp_test(i);
            if(test_value)
                fpp_count++;
        }

        System.out.println("Experimental FPP rate: "+1.0*fpp_count/test_count);

        int Speed_test_count=10000000;
        double start,end;
        start=System.nanoTime();
        for (int i=0;i<Speed_test_count;i++){
            test.query(keys[Math.abs(random.nextInt())%data_size]);
        }
        end=System.nanoTime();
        double time=(end - start) / 1000000;
        System.out.println("Time: "+ time + "ms");
        System.out.println("Speed: "+ Speed_test_count/time);


    }
}
