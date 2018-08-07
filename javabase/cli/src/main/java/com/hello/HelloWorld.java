package com.lesson.one.com.hello;

/**
 * @author muqi.lmq
 * @date 2018/7/29.
 * @desc
 * mkdir -p mods/com.greetings
 * javac -d mods/com.greetings \
 *         src/com.greetings/module-info.java \
 *         src/com.greetings/com/greetings/Main.java
 *
 * run : java -modulepath mods -m com.greetings/com.greetings.Main
 * $ mkdir mlib
 *
 * $ jar --create --archive=mlib/org.astro@1.0.jar \
 *         --module-version=1.0 -C mods/org.astro .
 *
 * $ jar --create --archive=mlib/com.greetings.jar \
 *         --main-class=com.greetings.Main -C mods/com.greetings .
 *
 * $ ls mlib
 */
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Test module greetings!");
    }
}
