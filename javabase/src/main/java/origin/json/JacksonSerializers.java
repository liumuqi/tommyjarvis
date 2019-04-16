package origin.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import net.sf.json.test.JSONAssert;
import org.skyscreamer.jsonassert.ArrayValueMatcher;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.ValueMatcher;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import origin.utils.DateUtils;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.skyscreamer.jsonassert.comparator.JSONComparator;

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

    public static boolean compareJsonInArrayNode(String j1, String j2) {
        try {
            ValueMatcher<Object> valueMatcher = (o1, o2) -> true;
            JSONComparator jc = new CustomComparator(JSONCompareMode.LENIENT, Customization.customization("*.desc",valueMatcher));
            ArrayValueMatcher<Object> arrayValueMatcher = new ArrayValueMatcher<>(jc);
            Customization customization = new Customization("d_111", arrayValueMatcher);
            JSONAssert.assertEquals(j1, j2, new CustomComparator(JSONCompareMode.LENIENT, customization));
            return true;
        } catch (Exception|Error e) {
            return false;
        }
    }
}
