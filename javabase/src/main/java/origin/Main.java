package origin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.Properties;

@SpringBootApplication
public class Main {

    public static void main(String[] args) throws Exception {
        Properties properties = System.getProperties();
//        System.out.println(properties);
//        String property = System.getProperty("user.home");
//        System.out.println(property);
        int ii;
        System.out.println("=====================");
        System.out.println("=====================");
        int i = 0xFFFFFFFF;
        int x = i & 0xF;
        System.out.println(Integer.toBinaryString(x));
        System.out.println("Hello World!");
        System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(Main.class, args);
//        SpringApplicationBuilder springApplicationBuilder = new SpringApplicationBuilder();
//        springApplicationBuilder.build().run(args);
        System.out.println("================end========================");
    }
}
