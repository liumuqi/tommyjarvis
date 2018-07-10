package base;

/**
 * @author muqi.lmq
 * @date 2018/6/21.
 */
public class RegexTest {
    public static void main(String[] args) {
        //加上_%就会很快 是因为回溯问题
//        String badRegex = "^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~_%\\\\/])+$";
        String badRegex = "^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)++([A-Za-z0-9-~\\\\/])++$";
        String bugUrl = "http://www.fapiao.com/dddp-web/pdf/download?request=6e7JGxxxxx4ILd-kExxxxxxxqJ4-CHLmqVnenXC692m74H38sdfdsazxcUmfcOH2fAfY1Vw__%5EDadIfJgiEf";
        if (bugUrl.matches(badRegex)) {
            System.out.println("match!!");
        } else {
            System.out.println("no match!!");
        }
        replaceAll();
    }

    public static void replaceAll() {
        String ss2 = "testABc".replaceAll("([A-Z])", "_$1");
        System.out.println(ss2);
    }
}
