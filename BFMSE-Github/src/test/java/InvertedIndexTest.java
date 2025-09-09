import org.Modules.*;
import org.openjdk.jol.info.GraphLayout;

import java.util.*;

public class InvertedIndexTest {
    public static void Inverted_test(int data_size, int Max_set_num, int each_set_num){
        long[] keys=new long[data_size];
        int[][] values=new int[data_size][];

        Random random = new Random();
        if (Max_set_num > each_set_num) {
            for (int i = 0; i < data_size; i++) {
                keys[i] = i;
                ArrayList<Integer> temp = new ArrayList<>();
                while (true) {
                    temp.add(Math.abs(random.nextInt()) % Max_set_num);
                    if (temp.size() == each_set_num) {
                        temp = new ArrayList<>(new HashSet<>(temp));
                        if (temp.size() == each_set_num)
                            break;
                    }
                }
                values[i]=temp.stream().mapToInt(x -> x).toArray();
            }
        } else {
            throw new ArrayIndexOutOfBoundsException("Max_set_num must larger than each_set_num !");
        }

        System.out.println("----------Experiment of Inverted Index----------");

        double start,end;
        start=System.nanoTime();
        for (int i=0;i<keys.length;i++){
            long key=keys[i];
            for (int j=0;j<keys.length;j++){
                if(keys[j]==key){
                    int[] value=values[j];
                }
            }
        }
        end=System.nanoTime();
        double time=(end - start) / 1000000;
        System.out.println("Speed: "+ keys.length/time);

        long x0= GraphLayout.parseInstance(keys).totalSize();
        long x1=GraphLayout.parseInstance((Object) values).totalSize();
        System.out.println("Memory usage: "+(x0+x1)/1024.0/1024.0+"MB");
    }

    public static void BFMSE_test(int data_size, int Max_set_num, int each_set_num){
        System.out.println("----------Experiment of BFMSE----------");

        int[] keys=new int[data_size];
        double fpp=Math.pow(2,-60);
        ArrayList<Integer>[] values=new ArrayList[data_size];


        boolean worseCase=false;

        Random random = new Random();

        if(!worseCase) {
            if (Max_set_num > each_set_num) {
                for (int i = 0; i < data_size; i++) {
                    keys[i] = i;
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
            } else {
                throw new ArrayIndexOutOfBoundsException("Max_set_num must larger than each_set_num !");
            }
        }else {
            System.out.println("In worseCase !! ");
            for (int i=0;i<values.length;i++){
                keys[i]=i;
                values[i]=new ArrayList<>();
                for (int j=0;j<Max_set_num;j++){
                    values[i].add(j);
                }
            }
        }
        Static_BFMSE test= Static_BFMSE.construct(keys,values,Max_set_num,fpp);

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
        System.out.println("Speed: "+ Speed_test_count/time);

        test.Tempfunction();
    }

    public static void Dynamic_BFMSE_test(int data_size, int Max_set_num, int each_set_num){
        System.out.println("----------Experiment of Dynamic BFMSE----------");

        int[] keys=new int[data_size];
        double fpp=Math.pow(2,-60);
        ArrayList<Integer>[] values=new ArrayList[data_size];
        boolean worseCase=false;

        Random random = new Random();

        if(!worseCase) {
            if (Max_set_num > each_set_num) {
                for (int i = 0; i < data_size; i++) {
                    keys[i] = i;
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
            } else {
                throw new ArrayIndexOutOfBoundsException("Max_set_num must larger than each_set_num !");
            }
        }else {
            System.out.println("In worseCase !! ");
            for (int i=0;i<values.length;i++){
                keys[i]=i;
                values[i]=new ArrayList<>();
                for (int j=0;j<Max_set_num;j++){
                    values[i].add(j);
                }
            }
        }

        Dynamic_BFMSE test= Dynamic_BFMSE.construct(keys,values,Max_set_num,fpp);


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
        System.out.println("Speed: "+ Speed_test_count/time);

        test.TempFunction();

    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Please enter the number of elements: " );
        int data_size = scanner.nextInt();
        System.out.print("Please enter the number of sets: " );
        int Max_set_num = scanner.nextInt();
        System.out.print("Please enter the number of sets per element: " );
        int each_set_num = scanner.nextInt();
        scanner.close();

        Inverted_test(data_size,Max_set_num,each_set_num);
        BFMSE_test(data_size,Max_set_num,each_set_num);
        Dynamic_BFMSE_test(data_size,Max_set_num,each_set_num);

    }
}