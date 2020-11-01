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
    public List<String> clazzs;

    public MyClassLoader(String rootPath, String... classPaths) throws Exception {
        System.out.println("============myclassloader:" + MyClassLoader.class.getClassLoader());
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
}
