package dsvshx.util;

import java.io.File;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import dsvshx.Application;
import dsvshx.loader.MyClassLoader;

/**
 * @author dongzhonghua
 * Created on 2020-10-22
 */
public class FileLisener extends FileAlterationListenerAdaptor {

    @Override
    public void onFileChange(File file) {
        if (file.getPath().contains(".class")) {
            try {
                MyClassLoader myClassLoader = new MyClassLoader(Application.rootPath, Application.rootPath + "/dsvshx");
                Application.start0(myClassLoader);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void startFileMino(String rootPath) throws Exception{
        FileAlterationObserver fileAlterationObserver = new FileAlterationObserver(rootPath);
        fileAlterationObserver.addListener(new FileLisener());
        FileAlterationMonitor fileAlterationMonitor = new FileAlterationMonitor(5000);
        fileAlterationMonitor.addObserver(fileAlterationObserver);
        fileAlterationMonitor.start();
    }
}
