package dsvshx.loader;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dsvshx.Application;
import dsvshx.Test;

/**
 * @author dongzhonghua
 * Created on 2020-10-22
 */
public class MyClassLoader extends ClassLoader {
    public String rootPath;
    public List<String> clazzs;

    public MyClassLoader(String rootPath, String... classPaths) throws Exception {
        this.rootPath = rootPath;
        clazzs = new ArrayList<>();
        // 扫描并加载类，打破双拼委派模型
        for (String classPath : classPaths) {
            loadClassPath(new File(classPath));
            // this.loadClass(classPath);
        }

    }

    public void loadClassPath(File file) throws Exception {
        if (file.isDirectory()) {
            for (File f : Objects.requireNonNull(file.listFiles())) {
                loadClassPath(f);
            }
        } else {
            String fileName = file.getName();
            String filePath = file.getPath();
            String endName = fileName.substring(fileName.lastIndexOf(".") + 1);
            if (endName.equals("class")) {
                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] bytes = new byte[(int) file.length()];
                fileInputStream.read(bytes);
                String className = filePathToClassName(filePath);
                clazzs.add(className);
                // class文件已经到了虚拟机了
                defineClass(className, bytes, 0, bytes.length);
            }
        }
    }

    private String filePathToClassName(String filePath) {
        String className = filePath.replace(rootPath, "").replaceAll("/", ".");
        return className.substring(1, className.lastIndexOf("."));
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        // TODO: 这里需要加锁
        Class<?> clazz = findLoadedClass(name);
        if (clazz == null) {
            if (!clazzs.contains(name)) {
                System.out.println(name);
                return super.loadClass(name);
            } else {
                throw new ClassNotFoundException("加载不到类！");
            }
        }
        return clazz;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }

    public static void main1(String[] args) throws Exception {
        // 模拟一个web项目，一直运行。如果是一个web项目，可以提供一个接口，调用接口就reload。
        while (true) {
            // 同一个classloader只会存在同一个类的一份class，所以如果需要替换之前的class需要new一个classloader。
            String path = MyClassLoader.class.getResource("/").getPath().replaceAll("%20", " ");
            String rootPath = new File(path).getPath();
            MyClassLoader myClassLoader = new MyClassLoader(rootPath, rootPath + "/dsvshx");
            Class<?> aClass = myClassLoader.loadClass("dsvshx.Test");
            Object o = aClass.newInstance();
            aClass.getMethod("hello").invoke(o);
            // 这种方式的话new一个对象的方式还是不能使用热加载的类。如何改变new的对象的classloader？
            new Test().hello();
            Thread.sleep(2000);
        }

        // 双亲委派
        // 全盘委托 该方法所述的类是由哪个类加载器加载的，那么这个方法中new出来的对象也都是由改类加载器加载的。

    }

    public static void main(String[] args) throws Exception {
        Application.run(MyClassLoader.class);
    }
}
