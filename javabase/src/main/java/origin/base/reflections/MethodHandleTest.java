package origin.base.reflections;

import java.lang.invoke.*;

/**
 * @Author:lmq
 * @Date: 2020/4/16
 * @Desc:
 **/
public class MethodHandleTest {

    class GrandFather {
        void thinking() {
            System.out.println("I 'm grandFather!");
        }
    }

    class Father extends GrandFather {
        void thinking() {
            System.out.println("I 'm father!");
        }
    }

    class Son extends Father {
        void thinking() {
            //实现祖父类的thinking(),打印 I 'm grandFather
            MethodType mt = MethodType.methodType(void.class);
            try {
                MethodHandle md = MethodHandles.lookup().findSpecial(GrandFather.class, "thinking", mt, this.getClass());
                md.invoke(this);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Throwable {
//        MethodHandleTest.Son son = new MethodHandleTest().new Son();
//        son.thinking();
        constantCallSite();
        System.out.println("done constant-----");
        useMutableCallSite();
    }

    public static void constantCallSite() throws Throwable {
        MethodType type = MethodType.methodType(String.class, int.class, int.class);
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle handle = lookup.findVirtual(String.class, "substring", type);
        //这样也可以
//        Object hello = handle.invoke("Hello", 2, 3);
//        System.out.println(hello);
        ConstantCallSite callSite = new ConstantCallSite(handle);
        MethodHandle invoker = callSite.dynamicInvoker();
        String result = (String) invoker.invoke("Hello", 2, 3);
        System.out.println(result);
    }

    /**
     * MutableCallSite 允许对其所关联的目标方法句柄通过setTarget方法来进行修改。
     * 以下为 创建一个 MutableCallSite，指定了方法句柄的类型，则设置的其他方法也必须是这种类型。
     * MutableCallSite.syncAll() 提供了方法来强制要求各个线程中 MutableCallSite 的使用者立即获取最新的目标方法句柄。
     * 但这个时候也可以选择使用 VolatileCallSite。VolatileCallSite
     * 作用与 MutableCallSite 类似，不同的是它适用于多线程情况，用来保证对于目标方法句柄所做的修改能够被其他线程看到。
     */
    public static void useMutableCallSite() throws Throwable {
        MethodType type = MethodType.methodType(int.class, int.class, int.class);
        MutableCallSite callSite = new MutableCallSite(type);
        MethodHandle invoker = callSite.dynamicInvoker();

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle maxHandle = lookup.findStatic(Math.class, "max", type);
        callSite.setTarget(maxHandle);
        int result = (int) invoker.invoke(3, 5);
        System.out.println(result == 5);

        MethodHandle minHandle = lookup.findStatic(Math.class, "min", type);
        callSite.setTarget(minHandle);
        result = (int) invoker.invoke(3, 5);
        System.out.println(result == 3);
    }

}
