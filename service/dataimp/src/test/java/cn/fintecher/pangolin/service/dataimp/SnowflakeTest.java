package cn.fintecher.pangolin.service.dataimp;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by ChenChang on 2018/8/2.
 */
public class SnowflakeTest {
    static Set<Long> ids = Collections.synchronizedSet(new HashSet<>());
    static Set<Thread> threads = Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
//            Thread thread = new Thread(new IdCrateThred());
            Thread thread = new IdCrateThread();
            thread.start();
            threads.add(thread);
        }

        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("run next process.");
        System.out.println("10000000=" + ids.size());


    }

    static class IdCrateThread extends Thread {

        @Override
        public void run() {
            int node = (int) (System.currentTimeMillis() % 1024);
//            Snowflake snowflake = new Snowflake(1, Thread.currentThread().getId());
            System.err.println(node);
            cn.fintecher.pangolin.common.utils.Snowflake snowflake = new cn.fintecher.pangolin.common.utils.Snowflake(node);
//            cn.fintecher.pangolin.common.utils.Snowflake snowflake=new cn.fintecher.pangolin.common.utils.Snowflake(1);

            for (int i = 0; i < 100000; i++) {
                ids.add(snowflake.next());
            }
        }
    }

}
