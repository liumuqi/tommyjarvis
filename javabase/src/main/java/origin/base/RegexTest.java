package origin.base;

/**
 * @author muqi.lmq
 * @date 2018/6/21.
 */
public class RegexTest {
    public static void main(String[] args) {
        //加上_%就会很快 是因为回溯问题  正则有贪婪和非贪婪之分
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

    /**
     * 回溯查找  正则  记忆方式
     * <p>
     * 引用  \0,\1,\2 和 $0, $1, $2  转义+数字
     * <p>
     * 非捕获组  (?:)  引用表达式(()), 本身不被消费(?),引用(:)
     * <p>
     * 前向查找  (?=)  引用子表达式(())，本身不被消费(?), 正向的查找(=)
     * <p>
     * 前向负查找  (?!)  引用子表达式(())，本身不被消费(?), 负向的查找(!)
     * <p>
     * 后向查找  (?<=)  引用子表达式(())，本身不被消费(?), 后向的(<，开口往后)，正的查找(=)
     * <p>
     * 后向负查找  (?<!)  引用子表达式(())，本身不被消费(?), 后向的(<，开口往后)，负的查找(!)
     */
    public static void replaceAll() {
//     * 回溯引用
        String ss2 = "testABc".replaceAll("([A-Z])", "_$1");
        System.out.println(ss2);
        String str = "abc abc 123";
        String all = str.replaceAll("(ab)c", "$1g");
        System.out.println(all);

//     * 如果我们不想子表达式被引用，可以使用非捕获正则(?:regex)这样就可以避免浪费内存。
        String str2 = "scq000";
        String s = str2.replaceAll("(scq00)(?:0)", "_@$1@_,\\$2");//没有$2引用
        System.out.println(s);

//     * 前向查找(?=ily) 如果我想过滤所有以happ开头的副词，那么也可以采用负前向查找的正则happ(?!ily)，就会匹配到happy单词的happ前缀
        String str3 = "xvcz happxxeily happily happcceily happxxxly happbcxsilx  happccvily vvcxzf";
        String ss = str3.replaceAll("happ(?=ily)", "@");
        System.out.println(ss);

//      * 后向查找 /(?<=app)ple/ (?<=regex)的语法就是我们这里要介绍的后向查找 (?<!regex) 语法，这里称为负后向查找
        String str4 = "apple people lvvple";
        String sss = str4.replaceAll("(?<!peo)ple", "@");
        System.out.println(sss);


    }
}
