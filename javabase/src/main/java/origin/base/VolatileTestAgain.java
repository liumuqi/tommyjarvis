package origin.base;

public class VolatileTestAgain implements Runnable {
    private  ObjectA a; //重要点1

    public VolatileTestAgain(ObjectA a) {
        this.a = a;
    }

    public static void main(String[] args) throws InterruptedException {
        // 如果启动的时候加上-server 参数则会 输出 Java HotSpot(TM) Server VM
        System.out.println(System.getProperty("java.vm.name"));
        ObjectA sa = new ObjectA();
        ObjectASub sub = new ObjectASub();
        sa.setSub(sub);
        VolatileTestAgain test = new VolatileTestAgain(sa);
//        sub.setFlag(true);
        sa.getSub().setFlag(false);
        new Thread(test).start();

        Thread.sleep(1000);
        sub.setFlag(false);
        Thread.sleep(1000);
        System.out.println("Main Thread " + sub.isFlag());
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
//        ObjectASub sub = a.getSub();//重要点二: 不能创建临时变量.
//        while (sub.isFlag()) {
        while (a.getSub().isFlag()) {
            i++;
        }
        System.out.println("stop My Thread " + i);
    }

    static class ObjectA {
        private volatile ObjectASub sub;

        public ObjectASub getSub() {
            return sub;
        }

        public void setSub(ObjectASub sub) {
            this.sub = sub;
        }
    }

    static class ObjectASub {
        private boolean flag = true;

        public boolean isFlag() {
            return flag;
        }

        public void setFlag(boolean flag) {
            this.flag = flag;
        }


    }
}
