package com.github.lzy.hotreload;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.liuzhengyang.hotreload.dynamiccompiler.DynamicCompiler;
import com.google.common.annotations.VisibleForTesting;

/**
 * @author liuzhengyang
 * Make something people want.
 * 2020/4/21
 */
public class HotReloadAgent {

    private static final Logger logger = LoggerFactory.getLogger(HotReloadAgent.class);

    // FIXME multi classes of different classloader ?
    private static Map<String, Class<?>> classCache = new ConcurrentHashMap<>();

    public static void agentmain(String agentArgs, Instrumentation instrumentation)
            throws IOException, UnmodifiableClassException, ClassNotFoundException {
        logger.info(agentArgs);
        if (agentArgs == null) {
            throw new IllegalArgumentException("Agent args is null");
        }
        String[] splits = agentArgs.split(",");
        if (splits.length < 2) {
            throw new IllegalArgumentException(agentArgs);
        }
        logger.info("Start reloading. Current classloader is " + HotReloadAgent.class.getClassLoader());
        doReload(instrumentation, splits);
    }

    private static void doReload(Instrumentation instrumentation, String[] splits)
            throws IOException, ClassNotFoundException, UnmodifiableClassException {
        String className = splits[0];
        String replaceTargetFile = splits[1];
        if (replaceTargetFile == null) {
            logger.error("Invalid argument file is null");
            return;
        }
        File file = Paths.get(replaceTargetFile).toFile();
        if (replaceTargetFile.endsWith(".class")) {
            logger.info("Reload by class file");
            byte[] newClazzByteCode = Files.readAllBytes(file.toPath());
            logger.info("\n" + new String(newClazzByteCode));
            doReloadClassFile(instrumentation, className, newClazzByteCode);
        } else {
            logger.info("Reload by java file");
            byte[] newClazzSourceBytes = Files.readAllBytes(file.toPath());
            logger.info("\n" + new String(newClazzSourceBytes));
            doCompileThenReloadClassFile(instrumentation, className, new String(newClazzSourceBytes, UTF_8));
        }
    }

    private static void doCompileThenReloadClassFile(Instrumentation instrumentation, String className,
             String sourceCode) {
        ClassLoader classLoader = getClassLoader(className, instrumentation);
        logger.info("Target class {} class loader {}", className, classLoader);
        DynamicCompiler dynamicCompiler = new DynamicCompiler(classLoader);
        dynamicCompiler.addSource(className, sourceCode);
        Map<String, byte[]> classNameToByteCodeMap = dynamicCompiler.buildByteCodes();
        classNameToByteCodeMap.forEach((clazzName, bytes) -> {
            try {
                Files.write(Paths.get("/tmp/replace_" + clazzName), bytes);
                doReloadClassFile(instrumentation, clazzName, bytes);
            } catch (Exception e) {
                logger.error("Class " + clazzName + " reload error ");
                e.printStackTrace();
            }
        });
    }

    private static void doReloadClassFile(Instrumentation instrumentation, String className,
          byte[] newClazzByteCode) throws UnmodifiableClassException, ClassNotFoundException {
        Class<?> clazz = getToReloadClass(instrumentation, className, newClazzByteCode);
        if (clazz == null) {
            logger.error("Class " + className + " not found");
        } else {
            instrumentation.redefineClasses(new ClassDefinition(clazz, newClazzByteCode));
            logger.info("no reload redefine=================================");
            logger.info("Congratulations! Reload " + clazz + " success!");
        }
    }

    private static Class<?> getToReloadClass(Instrumentation instrumentation, String className,
                                             byte[] newClazzByteCode) {
        Class<?> clazz = findTargetClass(className, instrumentation);
        if (clazz == null) {
            // 如果不用redefineClasses直接define一个应该也是可以的吧? 不行，一个加载器只能加载一个类一次，否则是重复加载。如果这样的话
            // 需要打破双亲委派模型，自己写一个类加载器。所以这里还是需要redefine
            clazz = defineNewClass(className, newClazzByteCode, clazz);
            logger.info("=========defineNewClass" + clazz);
        }
        return clazz;
    }

    private static Class<?> defineNewClass(String className, byte[] newClazzByteCode, Class<?> clazz) {
        logger.info("Class " + className + " not found, try to define a new class");
        ClassLoader classLoader = HotReloadAgent.class.getClassLoader();
        try {
            Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", String.class,
                    byte[].class, int.class, int.class);
            defineClass.setAccessible(true);
            // defineClass(className, bytes, 0, bytes.length); // 把一个类加载到虚拟机里面。
            clazz = (Class<?>) defineClass.invoke(classLoader, className, newClazzByteCode
                    , 0, newClazzByteCode.length);
            logger.info("Class " + className + " define success " + clazz);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return clazz;
    }

    @VisibleForTesting
    private static ClassLoader getClassLoader(String className, Instrumentation instrumentation) {
        Class<?> targetClass = findTargetClass(className, instrumentation);
        if (targetClass != null) {
            return targetClass.getClassLoader();
        }
        return HotReloadAgent.class.getClassLoader();
    }

    /**
     * 如果classCache缓存了该class信息则返回，否在利用inst找该class信息
     */
    @VisibleForTesting
    static Class<?> findTargetClass(String className, Instrumentation instrumentation) {
        return classCache.computeIfAbsent(className, clazzName -> {
            Class[] allLoadedClasses = instrumentation.getAllLoadedClasses();
            return Arrays.stream(allLoadedClasses)
                    .parallel()
                    .filter(clazz -> clazzName.equals(clazz.getName()))
                    .findFirst()
                    .orElse(null);
        });
    }

}
