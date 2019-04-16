package origin.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @Description 车架号注解
 * @Author daobin<wdb @ winbaoxian.com>
 * @Date 2017/3/24.
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {VinNoValidator.class})
public @interface VinNo {
    String message() default "车架号不合法「不能包含I、O、Q，第十位不能是U、Z、数字0」";

    /**
     * 「
     *
     * @return the regular expression to match
     */
    String regexp() default "^[A-HJ-NP-PR-Z0-9]{9}[A-HJ-NP-PR-TV-Y1-9]{1}[A-HJ-NP-PR-Z0-9]{7}$";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Defines several {@link VinNo} annotations on the same element.
     *
     * @see VinNo
     */
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        VinNo[] value();
    }
}
