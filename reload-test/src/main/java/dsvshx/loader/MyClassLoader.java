package dsvshx.loader;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author dongzhonghua
 * Created on 2020-10-22
 */
public class MyClassLoader extends ClassLoader {
    public String rootPath;
    public String[] classPaths;
    public List<String> clazzs;

    public MyClassLoader(String rootPath, String... classPaths) throws Exception {
        System.out.println("============myclassloader:" + MyClassLoader.class.getClassLoader());
        this.rootPath = rootPath;
        clazzs = new ArrayList<>();
        this.classPaths = classPaths;
        // 扫描并加载类，打破双拼委派模型
        for (String classPath : classPaths) {
            this.findClass(classPath);
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class<?> clazz = findLoadedClass(name);
        if (clazz == null) {
            File file = new File(name);
            if (file.isDirectory()) {
                for (File f : Objects.requireNonNull(file.listFiles())) {
                    this.findClass(f.getPath());
                }
            } else {
                try {
                    String fileName = file.getName();
                    String filePath = file.getPath();
                    String endName = fileName.substring(fileName.lastIndexOf(".") + 1);
                    if (endName.equals("class")) {
                        FileInputStream fileInputStream = new FileInputStream(file);
                        byte[] bytes = new byte[(int) file.length()];
                        fileInputStream.read(bytes);
                        String className = filePath.replace(rootPath, "").replaceAll("/", ".");
                        className = className.substring(1, className.lastIndexOf("."));
                        clazzs.add(className);
                        // class文件已经到了虚拟机了
                        return defineClass(className, bytes, 0, bytes.length);
                    }
                } catch (Exception e) {
                    return super.findClass(name);
                }
            }
        }
        return clazz;
    }
}
