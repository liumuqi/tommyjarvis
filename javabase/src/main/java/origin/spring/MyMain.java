package origin.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Properties;

@SpringBootApplication
//@ServletComponentScan
//@EnableDiscoveryClient
//@EnableTransactionManagement
//@EnableConfigurationProperties
public class MyMain {

    public static void main(String[] args) {
//        JmonitorBootstrap bootstrap = JmonitorBootstrap.getInstance();
//        bootstrap.start();
//        System.setProperty("spring.devtools.restart.enabled", "false");
//        SpringApplicationBuilder springApplicationBuilder = new SpringApplicationBuilder(Main.class);
        SpringApplication.run(MyMain.class, args);
//        springApplicationBuilder.properties("spring.config.location=classpath:/data/,classpath:/","spring.config.name=application,datas,redis-config").run(args);
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
