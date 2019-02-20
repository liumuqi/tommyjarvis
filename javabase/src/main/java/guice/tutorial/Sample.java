package guice.tutorial;

import com.google.inject.*;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Map;
import java.util.Set;


@ImplementedBy(SimpleHelloPrinter.class)
interface IHelloPrinter {
    void print();
}

class ThreadServiceScope implements Scope {
    static ThreadLocal<Object> threadLocal = new ThreadLocal<Object>();

    @Override
    public <T> Provider<T> scope(Key<T> key, final Provider<T> unscoped) {
        return new Provider<T>() {
            @Override
            public T get() {
                T instance = (T) threadLocal.get();
                if (instance == null) {
                    instance = unscoped.get();
                    threadLocal.set(instance);
                }
                return instance;
            }
        };
    }

    @Override
    public String toString() {
        return "Scopes.ThreadServiceScope";
    }
}

@Singleton
public class Sample {
    @Inject
    @Named("hello")
    private HelloPrinter printer;
    @Inject
    @Named("simple")
    private IHelloPrinter samplePrinter;
    @Inject
    @Named("complex")
    private IHelloPrinter complexPrinter;
    @Inject
    @Named("set1")
    private Set<IHelloPrinter> printers;
    @Inject
    @Named("set2")
    private Set<IHelloPrinter> printers2;
    @Inject
    @Named("map1")
    private Map<String, IHelloPrinter> map;

    public Sample() {
    }


    @Inject
    public Sample(@Named("hello") HelloPrinter helloPrinter, @Named("simple") IHelloPrinter simplePrinter, @Named("complex") IHelloPrinter complexPrinter) {
        this.printer = helloPrinter;
        this.samplePrinter = simplePrinter;
        this.complexPrinter = complexPrinter;
    }

    public static void main(String[] args) {
//        Injector injector = Guice.createInjector();
//        Sample sample = injector.getInstance(Sample.class);
//        Sample sample_ = injector.getInstance(Sample.class);
//        sample.hello();
//        sample.ihello();
//        System.out.println(sample.equals(sample_));
        System.out.println("===============module use=====================");
        Injector injector2 = Guice.createInjector(new SampleModule());
        Sample sample2 = injector2.getInstance(Sample.class);
        Sample sample2_ = injector2.getInstance(Sample.class);
        System.out.println(sample2.equals(sample2_));
        sample2.hello();
        sample2.ihello();
        System.out.println("set");
        System.out.println(sample2.printers.size());
        System.out.println(sample2.printers2.size());
        System.out.println("===========scopes test===========================");
        for (int i = 0; i < 3; i++) {
            new Thread("Thread-" + i) {
                public void run() {
                    for (int m = 0; m < 3; m++) {
                        System.out.println(String.format("%s-%d:%d", getName(), m, injector2.getInstance(IHelloPrinter.class).hashCode()));
                        try {
                            Thread.sleep(50L);
                        } catch (Exception e) {
                        }
                    }
                }
            }.start();
        }
    }

    public void hello() {
        printer.print();
    }

    public void ihello() {
        if (samplePrinter != null) {
            samplePrinter.print();
        }
        if (complexPrinter != null) {
            complexPrinter.print();
        }
    }
}

@Singleton
class HelloPrinter {
    public void print() {
        System.out.println("HelloPrinter say Hello, World");
    }
}

@Singleton
class SimpleHelloPrinter implements IHelloPrinter {
    public void print() {
        System.out.println("SimpleHelloPrinter say Hello, Simple World");
    }
}

@Singleton
class ComplexHelloPrinter implements IHelloPrinter {
    public void print() {
        System.out.println("ComplexHelloPrinter say Hello, Complex World");
    }
}

class SampleModule extends AbstractModule {
    @Override
    protected void configure() {
//        bind(IHelloPrinter.class).to(ComplexHelloPrinter.class);
        bind(HelloPrinter.class).annotatedWith(Names.named("hello")).to(HelloPrinter.class);
        bind(IHelloPrinter.class).annotatedWith(Names.named("simple")).to(SimpleHelloPrinter.class);
        bind(IHelloPrinter.class).annotatedWith(Names.named("complex")).to(ComplexHelloPrinter.class);

        Multibinder<IHelloPrinter> printers = Multibinder.newSetBinder(binder(), IHelloPrinter.class, Names.named("set1"));
        printers.addBinding().to(SimpleHelloPrinter.class);
        printers.addBinding().to(ComplexHelloPrinter.class);

        Multibinder<IHelloPrinter> printers2 = Multibinder.newSetBinder(binder(), IHelloPrinter.class, Names.named("set2"));
        printers2.addBinding().to(SimpleHelloPrinter.class);

        MapBinder<String, IHelloPrinter> printers_map = MapBinder.newMapBinder(binder(), String.class, IHelloPrinter.class, Names.named("map1"));
        printers_map.addBinding("simple").to(SimpleHelloPrinter.class).in(new ThreadServiceScope());
        printers_map.addBinding("complex").to(ComplexHelloPrinter.class).in(Scopes.SINGLETON);
    }
}
