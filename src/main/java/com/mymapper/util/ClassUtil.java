package com.mymapper.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by huang on 2015/10/5.
 */
public class ClassUtil {
    private static final Logger logger = Logger.newInstance(ClassUtil.class);

    public static List<Class<?>> getClassList(String packagePath) {
        List<Class<?>> classList = new ArrayList<>();
        try {
//            通过给定包名获取包下所有类文件
            Enumeration<URL> urls = getClassLoader().getResources(packagePath.replace(".", "/"));
            while (urls.hasMoreElements()) {
//                TODO 先不考虑扫描到jar包的情况
                URL url = urls.nextElement();
//                可能会扫描到带有空格的路径， 空格可能会变成%20 所以需要替换回空格
                url.getPath().replaceAll("%20", " ");
                addClass(url.toURI().getPath(), classList, packagePath);
            }
        } catch (IOException e) {
            logger.error("获取类文件失败", e);
        } catch (Exception e) {
            logger.error("获取类文件失败", e);
        }

        return classList;
    }

    private static void addClass(final String path, final List<Class<?>> classList, final String packagePath) {
        logger.debug("path: " + path + ", packagePath: " + packagePath);
        final File[] files = new File(path)
            .listFiles(pathname -> pathname.isDirectory() ||
                (pathname.isFile() && pathname.getName().endsWith(".class")));

        for (File file : files) {
            if (file.isDirectory()) {
//                递归调用获取子包下的类文件
                addClass(path + "/" + file.getName(), classList, packagePath + "." + file.getName());
            } else {
                try {
//                    loadClass的参数是类的全名， 如:com.example.A, 不带.class后缀
                    Class<?> clazz = getClassLoader()
                        .loadClass(packagePath + "." + file.getName()
                            .substring(0, file.getName().lastIndexOf(".")));
                    classList.add(clazz);
                } catch (ClassNotFoundException e) {
                    logger.error("类加载失败: " + packagePath + file.getName().substring(0, file.getName().lastIndexOf(".")) + ", " + e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 返回该类的实例
     *
     * @param clazz 需要创建实例的类
     * @return
     */
    public static Object instantiateClass(Class<?> clazz) {
        try {
            Object obj = clazz.newInstance();
            return obj;
        } catch (Exception e) {
            throw new RuntimeException("初始化容器失败");
        }
    }

    public static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}
