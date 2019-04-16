package origin.base;

public class VolatileTest implements Runnable {
    private volatile ObjectA a; // <span style="color:#ff0000;"><strong>加上volatile 就可以正常结束While循环了  </strong></span>

    public VolatileTest(ObjectA a) {
        this.a = a;
    }
    public static void main(String[] args) throws InterruptedException {
        System.out.println(System.getProperty("java.vm.name"));
        VolatileTest test = new VolatileTest(new ObjectA());
        Thread thread = new Thread(test);
        thread.start();
        Thread.sleep(1000);
        test.stop();
        Thread.sleep(1000);
    }

    public ObjectA getA() {
        return a;
    }

    public void setA(ObjectA a) {
        this.a = a;
    }

    @Override
    public void run() {
        long i = 0;
        while (a.isFlag()) {
            i++;
//            System.out.println("------------------");
        }
        System.out.println("stop My Thread " + i);
    }

    public void stop() {
        a.setFlag(false);
    }

    static class ObjectA {
        private  boolean flag = true;

        public boolean isFlag() {
            return flag;
        }

        public void setFlag(boolean flag) {
            this.flag = flag;
        }

    }
}
