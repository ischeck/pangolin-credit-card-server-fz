package cn.fintecher.pangolin.common.utils;

/**
 * Created by ChenChang on 2018/6/29.
 */

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

@SuppressWarnings({"unchecked"})
public class InterfaceHelp {

    private static final String PACKPATH = InterfaceHelp.class.getName().substring(0, InterfaceHelp.class.getName().lastIndexOf("."));
    private static final byte[] JAR_MAGIC = {'P', 'K', 3, 4};

    /**
     * getDataClass
     *
     * @param t
     * @return ArrayList<T> 2016年5月11日-下午1:48:11
     */
    public static <T> ArrayList<T> getDataClass(T t) {
        return getDataClass(null, t);
    }

    /**
     * getDataClass
     *
     * @param PackPath
     * @param t
     * @return ArrayList<T> 2016年5月11日-下午1:48:00
     */
    public static <T> ArrayList<T> getDataClass(String PackPath, T t) {
        ArrayList<T> list = new ArrayList<T>();
        try {
            String path = getPackagePath((PackPath == null || PackPath.equals("")) ? PACKPATH : PackPath);
            List<String> children = list(path);
            for (String child : children) {
                if (child.endsWith(".class")) {
                    String externalName = child.substring(0, child.indexOf('.')).replace('/', '.');
                    ClassLoader loader = Thread.currentThread().getContextClassLoader();
                    Class<?> type = loader.loadClass(externalName);
                    if ((((Class<T>) t).isAssignableFrom(type)) && !isABSorINT(type))
                        list.add((T) type);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * isABSorINT
     *
     * @param clazz
     * @return boolean 2016年5月11日-下午1:47:47
     */
    public static boolean isABSorINT(Class<?> clazz) {
        return clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers());
    }

    /**
     * @param clazz_ 接口
     * @param clazz  验证
     * @return boolean 2016年5月24日-上午9:41:26
     */
    public static boolean IsCanLoadClass(Class<?> clazz_, Class<?> clazz) {
        return clazz_.isAssignableFrom(clazz) && isABSorINT(clazz);
    }

    /**
     * getPackagePath
     *
     * @param packageName
     * @return String 2016年5月11日-下午1:47:40
     */
    private static String getPackagePath(String packageName) {
        return packageName == null ? null : packageName.replace('.', '/');
    }

    /**
     * getInstance
     *
     * @param clazz
     * @return T 2016年5月11日-下午1:47:30
     */
    public static <T> T getInstance(Class<T> clazz) {
        T t = null;
        Constructor<?> constructor;
        try {
            constructor = clazz.getDeclaredConstructor(new Class<?>[]{});
            constructor.setAccessible(true);
            t = (T) constructor.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * getInstance
     *
     * @param clazz
     * @param parType
     * @param pars
     * @return T 2016年5月13日-上午11:00:50
     */
    public static <T> T getInstance(Class<T> clazz, Class<?>[] parType, Object[] pars) {
        T t = null;
        Constructor<?> constructor;
        try {
            constructor = clazz.getDeclaredConstructor(parType);
            constructor.setAccessible(true);
            t = (T) constructor.newInstance(pars);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * getInstance_
     *
     * @param clazz
     * @param parType
     * @param pars
     * @return T 2016年5月13日-上午11:02:53
     */
    public static <T> T getInstance_(Class<T> clazz, Class<?>[] parType, Object... pars) {
        return getInstance(clazz, parType, pars);
    }

    /**
     * getInstance
     *
     * @param clazz
     * @return
     * @throws Exception T 2016年5月11日-下午1:47:06
     */
    public static <T> T getInstance(String clazz) {

        T t = null;
        try {
            t = (T) getInstance(Class.forName(clazz));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * CovertType
     * <li>去除可恶SAX小黄线
     * <p>
     *
     * @param t
     * @return T 2016年5月11日-下午1:46:53
     */
    public static <T> T CovertType(Object t) {
        return (T) t;
    }

    /**
     * CovertType
     * <p>
     * <li>必须继承List
     * <p>
     * <li>去除可恶SAX小黄线
     * <p>
     *
     * @param t
     * @param type
     * @return List<T> 2016年5月12日-上午10:54:20
     */
    public static <T> List<T> CovertType(Object t, T type) {
        return (List<T>) t;
    }

    /**
     * CovertTypeToSting
     *
     * @param t
     * @return String 2016年5月11日-下午1:46:46
     */
    public static <T> String CovertTypeToSting(T t) {
        if (t == null)
            return "";
        if (t instanceof Double)
            return ((Double) t).toString();
        if (t instanceof Long)
            return ((Long) t).toString();
        if (t instanceof Date)
            return new SimpleDateFormat("yyyy-MM-dd").format(CovertType(t));
        return t.toString();
    }

    /**
     * 尚未测试 返回T
     *
     * @param map
     * @param clazz
     * @return T 2016年5月10日-下午3:43:14
     */
    public static <T> T MapToEntity(HashMap<String, Object> map, Class<T> clazz) {
        T obj = getInstance(clazz);
        try {
            for (Map.Entry<String, Object> m : map.entrySet()) {
                clazz.getField(m.getKey()).setAccessible(true);
                clazz.getField(m.getKey()).set(obj, m.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * 尚未测试 返回List<T>
     *
     * @param map
     * @param clazz
     * @return List<T> 2016年5月10日-下午3:51:28
     */
    public static <T> List<T> MapToEntityList(List<HashMap<String, Object>> map, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        for (HashMap<String, Object> tmap : map) {
            list.add(MapToEntity(tmap, clazz));
        }
        return list;
    }


    protected static List<URL> getResources(String path) throws IOException {
        return Collections.list(Thread.currentThread().getContextClassLoader().getResources(path));
    }

    public static List<String> list(String path) throws IOException {
        List<String> names = new ArrayList<String>();
        for (URL url : getResources(path)) {
            names.addAll(list(url, path));
        }
        return names;
    }

    protected static List<String> listResources(JarInputStream jar, String path) throws IOException {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if (!path.endsWith("/")) {
            path = path + "/";
        }
        List<String> resources = new ArrayList<String>();
        for (JarEntry entry; (entry = jar.getNextJarEntry()) != null; ) {
            if (!entry.isDirectory()) {
                String name = entry.getName();
                if (!name.startsWith("/")) {
                    name = "/" + name;
                }
                if (name.startsWith(path)) {
                    resources.add(name.substring(1));
                }
            }
        }
        return resources;
    }

    public static List<String> list(URL url, String path) throws IOException {
        InputStream is = null;
        try {
            List<String> resources = new ArrayList<String>();
            URL jarUrl = findJarForResource(url);
            if (jarUrl != null) {
                is = jarUrl.openStream();
                resources = listResources(new JarInputStream(is), path);
            } else {
                List<String> children = new ArrayList<String>();
                try {
                    if (isJar(url)) {
                        is = url.openStream();
                        JarInputStream jarInput = new JarInputStream(is);
                        for (JarEntry entry; (entry = jarInput.getNextJarEntry()) != null; ) {
                            children.add(entry.getName());
                        }
                        jarInput.close();
                    } else {
                        is = url.openStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                        List<String> lines = new ArrayList<String>();
                        for (String line; (line = reader.readLine()) != null; ) {
                            lines.add(line);
                            if (getResources(path + "/" + line).isEmpty()) {
                                lines.clear();
                                break;
                            }
                        }

                        if (!lines.isEmpty()) {
                            children.addAll(lines);
                        }
                    }
                } catch (FileNotFoundException e) {
                    if ("file".equals(url.getProtocol())) {
                        File file = new File(url.getFile());
                        if (file.isDirectory()) {
                            children = Arrays.asList(file.list());
                        }
                    } else {
                        throw e;
                    }
                }
                String prefix = url.toExternalForm();
                if (!prefix.endsWith("/")) {
                    prefix = prefix + "/";
                }
                for (String child : children) {
                    String resourcePath = path + "/" + child;
                    resources.add(resourcePath);
                    URL childUrl = new URL(prefix + child);
                    resources.addAll(list(childUrl, resourcePath));
                }
            }

            return resources;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    // Ignore
                }
            }
        }
    }

    protected static boolean isJar(URL url, byte[] buffer) {
        InputStream is = null;
        try {
            is = url.openStream();
            is.read(buffer, 0, JAR_MAGIC.length);
            if (Arrays.equals(buffer, JAR_MAGIC)) {
                return true;
            }
        } catch (Exception e) {
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    // Ignore
                }
            }
        }

        return false;
    }

    protected static URL findJarForResource(URL url) throws MalformedURLException {
        try {
            for (; ; ) {
                url = new URL(url.getFile());
            }
        } catch (MalformedURLException e) {
        }

        StringBuilder jarUrl = new StringBuilder(url.toExternalForm());
        int index = jarUrl.lastIndexOf(".jar");
        if (index >= 0) {
            jarUrl.setLength(index + 4);
        } else {
            return null;
        }
        try {
            URL testUrl = new URL(jarUrl.toString());
            if (isJar(testUrl)) {
                return testUrl;
            } else {
                jarUrl.replace(0, jarUrl.length(), testUrl.getFile());
                File file = new File(jarUrl.toString());
                if (!file.exists()) {
                    try {
                        file = new File(URLEncoder.encode(jarUrl.toString(), "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException("Unsupported encoding?  UTF-8?  That's unpossible.");
                    }
                }

                if (file.exists()) {
                    testUrl = file.toURI().toURL();
                    if (isJar(testUrl)) {
                        return testUrl;
                    }
                }
            }
        } catch (MalformedURLException e) {
        }

        return null;
    }

    protected static boolean isJar(URL url) {
        return isJar(url, new byte[JAR_MAGIC.length]);
    }
}