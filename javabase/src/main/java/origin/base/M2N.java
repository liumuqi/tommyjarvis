package origin.base;

import java.math.BigInteger;

/**
 * @Author:qishan
 * @Date: 2019-04-08
 * @Desc:
 **/
public class M2N {
    private static String template = "0123456789abcdefghigklmnopqrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ!@#$%^&*()_+|~-=\\`[]{};:,./<>?";
//    private static String template = "01";

    static String transRadix(BigInteger num, int toRadix) {
        StringBuilder sb = new StringBuilder();
        BigInteger m = BigInteger.valueOf(toRadix);
        if (num.intValue() == 0) {
            return "0";
        }
        while (num.intValue() != 0) {
//            sb.append(chs[Integer.parseInt(String.valueOf(num % toRadix))]);
            sb.append(template.charAt(Integer.parseInt(String.valueOf(num.mod(m)))));
            num = num.divide(m);
        }
        return sb.reverse().toString();
    }

    // 其他进制转为10进制，按权展开
    public static BigInteger N210(String number, int N) {
        char ch[] = number.toCharArray();
        int len = ch.length;
        BigInteger result = new BigInteger("0");
        if (N == 10) {
            return new BigInteger(number);
        }
        BigInteger base = new BigInteger("1");
        for (int i = len - 1; i >= 0; i--) {
            int index = template.indexOf(ch[i]);
            result = result.add(base.multiply(BigInteger.valueOf(index)));
            base = base.multiply(BigInteger.valueOf(N));
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(transRadix(new BigInteger("1"), 82));
        StringBuilder unixtime = new StringBuilder("1554260402");
        StringBuilder adcode = new StringBuilder("110000");
        StringBuilder link = new StringBuilder("5121625316541857815");
        System.out.println(unixtime.toString() + adcode + link);
        String x1 = unixtime.reverse().toString() + adcode.reverse() + link.reverse();
        System.out.println(x1);
        String x = transRadix(new BigInteger(x1), 82);
        System.out.println(x);//uuytUFnoM$ntCwMmCr
        System.out.println(N210(x, 82).toString());
    }
}
