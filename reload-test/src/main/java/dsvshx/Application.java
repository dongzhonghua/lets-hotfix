package dsvshx;

import java.io.File;

import dsvshx.loader.MyClassLoader;
import dsvshx.util.FileLisener;

/**
 * @author dongzhonghua
 * Created on 2020-10-22
 */
public class Application {
    public static String rootPath;

    public static void run(Class<?> clazz) throws Exception {
        String path = clazz.getResource("/").getPath().replaceAll("%20", " ");
        String rootPath = new File(path).getPath();
        Application.rootPath = rootPath;
        FileLisener.startFileMino(rootPath);
        MyClassLoader myClassLoader = new MyClassLoader(rootPath, rootPath + "/dsvshx");
        start0(myClassLoader);
    }

    public void start() {
        System.out.println("===========启动。。。。。。。。。");
        Test test = new Test();
        test.hello();
        System.out.println(Test.class.getClassLoader());
    }

    public static void start0(MyClassLoader myClassLoader) throws Exception {
        Class<?> aClass = myClassLoader.loadClass("dsvshx.Application");
        Object o = aClass.newInstance();
        aClass.getMethod("start").invoke(o);
    }

    public static void main(String[] args) throws Exception {
        Application.run(MyClassLoader.class);
    }

    public static void main0(String[] args) throws Exception {
        // 模拟一个web项目，一直运行。如果是一个web项目，可以提供一个接口，调用接口就reload。
        while (true) {
            System.out.println(Application.class.getClassLoader());
            // 同一个classloader只会存在同一个类的一份class，所以如果需要替换之前的class需要new一个classloader。
            String path = MyClassLoader.class.getResource("/").getPath().replaceAll("%20", " ");
            String rootPath = new File(path).getPath();
            MyClassLoader myClassLoader = new MyClassLoader(rootPath, rootPath + "/dsvshx");
            Class<?> aClass = myClassLoader.loadClass("dsvshx.Test");
            Object o = aClass.newInstance();
            aClass.getMethod("hello").invoke(o);
            // 这种方式的话new一个对象的方式还是不能使用热加载的类。如何改变new的对象的classloader？
            new Test().hello();
            Thread.sleep(3000);
        }
        // 全盘委托 该方法所述的类是由哪个类加载器加载的，那么这个方法中new出来的对象也都是由改类加载器加载的。
    }
}
