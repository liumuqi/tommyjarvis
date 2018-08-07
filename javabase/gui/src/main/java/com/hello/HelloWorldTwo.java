package com.lesson.two.com.hello;

/**
 * @author muqi.lmq
 * @date 2018/7/29.
 * @desc mkdir -p mods/com.greetings
 * javac -d mods/com.greetings \
 * src/com.greetings/module-info.java \
 * src/com.greetings/com/greetings/Main.java
 * <p>
 * run : java -modulepath mods -m com.greetings/com.greetings.Main
 */
public class HelloWorldTwo {
    public static void main(String[] args) {
        System.out.println("Test module greetings!");
    }
}
