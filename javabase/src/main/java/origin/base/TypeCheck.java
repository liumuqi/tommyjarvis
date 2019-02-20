package origin.base;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TypeCheck {
    public static void main(String[] args) {
        System.out.println(((ParameterizedType) MyStringSubClass.class.getGenericSuperclass()).getActualTypeArguments()[0]);
        System.out.println(MyExtend.class.getGenericSuperclass());
        List<String> aa = new ArrayList<>();
        aa.add("avs");
        System.out.println(aa.getClass().getTypeParameters()[0]);
    }

    class MyGenericClass<T> {
    }

    class MyStringSubClass extends MyGenericClass<String> {
    }

    static class MyClass {
    }

    class MyExtend extends MyClass {
    }
}
