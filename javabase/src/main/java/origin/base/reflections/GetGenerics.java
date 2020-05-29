package origin.base.reflections;

import lombok.SneakyThrows;
import org.springframework.core.ResolvableType;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

/**
 * @Author:lmq
 * @Date: 2020/5/29
 * @Desc:
 **/
public class GetGenerics {

    private HashMap<Integer, List<String>> myMap;

    public static void main(String[] args) {
//        SpringApplication.run(Application.class, args);
        GetGenerics application = new GetGenerics();
        application.example();
    }

    @SneakyThrows
    public void example() {
        Field myMap = getClass().getDeclaredField("myMap");
        Type genericType = myMap.getGenericType();
        ResolvableType t = ResolvableType.forField(myMap);
        t.getSuperType(); // AbstractMap<Integer, List<String>>
        t.asMap(); // Map<Integer, List<String>>
        t.getGeneric(0).resolve(); // Integer
        t.getGeneric(1).resolve(); // List
        t.getGeneric(1); // List<String>
        t.resolveGeneric(1, 0); // String
    }
}

