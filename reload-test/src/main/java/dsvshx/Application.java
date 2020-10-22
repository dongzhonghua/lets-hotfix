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
        System.out.println("启动。。。。。。。。。");
        Test test = new Test();
        test.hello();
        test.say();
        System.out.println(String.class.getClassLoader());
        System.out.println(FileLisener.class.getClassLoader());
        System.out.println(Test.class.getClassLoader());
        System.out.println(MyClassLoader.class.getClassLoader());
    }

    public static void start0(MyClassLoader myClassLoader) throws Exception {
        Class<?> aClass = myClassLoader.loadClass("dsvshx.Application");
        Object o = aClass.newInstance();
        aClass.getMethod("start").invoke(o);
    }
}
