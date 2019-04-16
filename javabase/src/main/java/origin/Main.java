package origin;

import java.util.Properties;

public class Main {

    public static void main(String[] args) {
        Properties properties = System.getProperties();
        System.out.println(properties);
        String property = System.getProperty("user.home");
        System.out.println(property);
        int ii;
        System.out.println("=====================");
        System.out.println("=====================");
        int i = 0xFFFFFFFF;
        int x = i & 0xF;
        System.out.println(Integer.toBinaryString(x));
        System.out.println("Hello World!");
    }
}
