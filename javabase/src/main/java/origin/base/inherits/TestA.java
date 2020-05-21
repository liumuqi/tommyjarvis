package origin.base.inherits;

/**
 * @Author:lmq
 * @Date: 2020/4/10
 * @Desc:
 **/
public class TestA extends Test {
    public void setPd() {
        pushDataCount = 333;
    }

    @Override
    public void p() {
        System.out.println(" second p");
    }
}
