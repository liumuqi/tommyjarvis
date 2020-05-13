package origin.concurrency.threads;

/**
 * @Author:lmq
 * @Date: 2020/5/13
 * @Desc:
 **/
public class ThreadTest {
    public static void main(String[] args) {
        Thread thread = new Thread(()->{
            int i =0;
            while (i<3){
                try {
                    System.out.println("----");
                    i++;
                    Thread.currentThread().sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.setDaemon(true);//不用等待子线程
        thread.start();
        System.out.println("done");
    }
}
