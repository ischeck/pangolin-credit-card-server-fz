package cn.fintecher.pangolin.common.utils;

import java.util.Random;

public class RandomUtil {
    static Random rnd = new Random();

    public static String random6() {
        Integer i = (int) (Math.random() * 1000000);
        if (i < 99999) {

            i += 100000;
        }

        return i.toString();
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 5000; i++) {
            getRandomNumber(6);
//            System.out.println(   " "+ getRandomNumber(6));
        }
        long end = System.currentTimeMillis();
        System.err.println("A time=" + (end - start) + " ms");
        start = System.currentTimeMillis();
        for (int i = 0; i < 5000; i++) {
//            System.out.println(   " "+ random6());
            random6();
        }
        end = System.currentTimeMillis();
        System.err.println("B time=" + (end - start) + " ms");

    }

    /**
     * 逐位生成随机整数
     *
     * @param digCount 位数
     * @return
     */
    public static String getRandomNumber(int digCount) {
        StringBuilder sb = new StringBuilder(digCount);
        for (int i = 0; i < digCount; i++)
            sb.append((char) ('0' + rnd.nextInt(10)));
        return sb.toString();
    }

}
