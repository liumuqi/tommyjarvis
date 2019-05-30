package origin.perf.bttracetest;

//import static com.sun.btrace.BTraceUtils.println;
//import static com.sun.btrace.BTraceUtils.str;
//import static com.sun.btrace.BTraceUtils.strcat;
//import static com.sun.btrace.BTraceUtils.timeMillis;
//
//import com.sun.btrace.annotations.BTrace;
//import com.sun.btrace.annotations.Kind;
//import com.sun.btrace.annotations.Location;
//import com.sun.btrace.annotations.OnMethod;
//import com.sun.btrace.annotations.ProbeClassName;
//import com.sun.btrace.annotations.ProbeMethodName;
//import com.sun.btrace.annotations.TLS;
//
//@BTrace
public class BtraceTest {
    //    @TLS
//    private static long startTime = 0;
//
//    @OnMethod(clazz = "my.app.test.HelloWorld", method = "execute")
//    public static void startMethod() {
//        startTime = timeMillis();
//    }
//
//    @OnMethod(clazz = "my.app.test.HelloWorld", method = "execute", location = @Location(Kind.RETURN))
//    public static void endMethod() {
//        println(strcat("the class method execute time=>", str(timeMillis() - startTime)));
//        println("-------------------------------------------");
//    }
//
//    @OnMethod(clazz = "my.app.test.HelloWorld", method = "execute", location = @Location(Kind.RETURN))
//    public static void traceExecute(@ProbeClassName String name, @ProbeMethodName String method, int sleepTime) {
//        println(strcat("the class name=>", name));
//        println(strcat("the class method=>", method));
//        println(strcat("the class method params=>", str(sleepTime)));
//    }
//    class CaseObject {
//        private static int sleepTotalTime = 0;
//        public boolean execute(int sleepTime) throws Exception {
//            System.out.println("sleep: " + sleepTime);
//            sleepTotalTime += sleepTime;
//            Thread.sleep(sleepTime);
//            return true;
//        }
//
//        public static boolean executeStatic(int sleepTime) throws InterruptedException {
//            System.out.println("sleep: " + sleepTime);
//            sleepTotalTime += sleepTime;
//            Thread.sleep(sleepTime);
//            return true;
//        }
//    }
//    @OnMethod(clazz = "CaseObject", method = "execute", location = @Location(Kind.RETURN))
//    public static void traceExecute(@Self Object instance, int sleepTime, @Return boolean result,
//                                    @Duration long durationL) {
//        println();
//        println("======================================");
//        println("call CaseObject.execute");
//        println(strcat("sleepTime is:", str(sleepTime)));
//        println(strcat("sleepTotalTime is:",
//                str(get(field("CaseObject", "sleepTotalTime"), instance))));
//        println(strcat("return value is:", str(result)));
//        println(strcat("duration value is:", str(durationL)));
//        println("======================================");
//        println();
//    }
}
