package origin.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import origin.utils.DateUtils;

import java.io.IOException;

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
            String s = "未知";
            gen.writeString(s);
        }
    }

    /**
     * 城市
     */
    public static class AdcodeSerializer extends JsonSerializer<Integer> {
//        @Autowired
//        private SService sService;

        public AdcodeSerializer() {
            SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        }

        @Override
        public void serialize(Integer value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString("");
        }
    }
}
