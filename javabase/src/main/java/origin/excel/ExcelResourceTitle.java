package origin.excel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author muqi.lmq
 * @date 23/05/2017.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelResourceTitle {
    /**
     * 属性的标题名称
     *
     * @return
     */
    String title();

    /**
     * 在excel的顺序
     *
     * @return
     */
    int order() default 9999;
}
