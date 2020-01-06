package com.jmdf.seckill;


import com.jmdf.seckill.service.SpikeCommodityService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;

import java.util.concurrent.CountDownLatch;


/**
 * 高并发测试秒杀业务
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class test {

    @Autowired
    private SpikeCommodityService spikeCommodityService;

    @Test
    public void Test_Seckill() {
// 初始化计数器为 1
        CountDownLatch start=new CountDownLatch(1);
        //模擬16个线程
        for(int i=0;i<10;i++){
            MyTestThread tt =new MyTestThread(start);
            Thread t = new Thread(tt);
            t.start();
        }
        //计数器減 1
        start.countDown();
        //计数器为0，所有线程释放，同时并发
    }
class MyTestThread implements Runnable{

        private final CountDownLatch startSignal;

        public MyTestThread(CountDownLatch startSignal) {
            super();
            this.startSignal = startSignal;
        }
        @Override
        public void run() {
            try {
                startSignal.await();
                //一直阻塞当前线程，直到计时器的值为0
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //实际测试操作
            doWork();
        }
        private void doWork() {
             System.out.println(util.getTel());
            spikeCommodityService.spike(util.getTel(),Long.getLong("666"));
            System.out.println("======================================================");
        }
}
}

