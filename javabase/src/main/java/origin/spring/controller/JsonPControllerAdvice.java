package origin.spring.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.servlet.mvc.method.annotation.AbstractJsonpResponseBodyAdvice;

/**
 * @author muqi.lmq
 * @date 18/11/2016.
 */
@ControllerAdvice(basePackageClasses = {JsonPControllerAdvice.class})
public class JsonPControllerAdvice {
    // remove in spring 5.1
//        extends AbstractJsonpResponseBodyAdvice {
//    public JsonPControllerAdvice() {
//        super("callback");
//    }
}
