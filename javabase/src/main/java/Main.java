public class Main {

    public static void main(String[] args) {
        int i = 0xFFFFFFFF;
        int x = i & 0xF;
        System.out.println(Integer.toBinaryString(x));
        System.out.println("Hello World!");
    }
}
