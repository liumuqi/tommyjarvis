package argrith;

/**
 * @Author:lmq
 * @Date: 2020/3/2
 * @Desc:
 **/
public class DpTest {
    /**
     * 1,5,11 3种钱, 拼出15 最少要几张
     * f(n) = min{f(n-1),f(n-5),f(n-11)} + 1   最后一张 是1,5,11的时候最少f(w)  逆向推
     *
     * @param args
     */
    public static void main(String[] args) {
        f(15);
        System.out.println("========================");
    }

    //我从哪里来 逆推 dp 最优子结构,重复
    public static void f(int n) {
        int[] fs = new int[16];
        for (int i = 1; i <= n; i++) {
            int cost = Integer.MAX_VALUE;
            if (i - 5 >= 0) {
                cost = Math.min(cost, fs[i - 5] + 1);
            }
            if (i - 11 >= 0) {
                cost = Math.min(cost, fs[i - 11] + 1);
            }
            if (i - 1 >= 0) {
                cost = Math.min(cost, fs[i - 1] + 1);
            }
            fs[i] = cost;
            System.out.println("f[" + i + "] : " + cost);
        }
    }

}
