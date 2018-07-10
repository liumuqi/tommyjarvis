package json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import utils.DateUtils;

import java.io.IOException;

/**
 * @author muqi.lmq
 * @date 16/10/2017.
 */
public class JacksonSerializers {

    /**
     * 日期
     */
    public static class SecondToFORMATyyyyMMddHHmmss extends JsonSerializer<Long> {
        @Override
        public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            String dayString = DateUtils.getDayString(value, DateUtils.FORMAT_1);
            gen.writeString(dayString);
        }
    }

    /**
     * 真值
     */
    public static class StateJsonSerializer extends JsonSerializer<Integer> {
        @Override
        public void serialize(Integer value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
//            BaseStateEnum baseStateEnum = BaseStateEnum.valueOfCode(value);
            String s = "未知";
            gen.writeString(s);
        }
    }

    /**
     * 城市
     */
    public static class AdcodeSerializer extends JsonSerializer<Integer> {
//        @Autowired
//        private SystemService systemService;

        public AdcodeSerializer() {
            SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        }

        @Override
        public void serialize(Integer value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
//            String cityByAdcode = systemService.getCityByAdcode(value);
            gen.writeString("");
        }
    }
}
