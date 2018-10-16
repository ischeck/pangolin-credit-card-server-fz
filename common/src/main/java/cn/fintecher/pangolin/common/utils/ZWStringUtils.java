package cn.fintecher.pangolin.common.utils;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by jwdstef on 2017/3/8.
 * 字符串工具类
 */
public class ZWStringUtils {
    /**
     * 判断对象为空
     *
     * @param obj 对象名
     * @return 是否为空
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        } else if (obj instanceof String && ((String) obj).trim().equals("")) {
            return true;
        } else if (obj instanceof Collection && ((Collection) obj).isEmpty()) {
            return true;
        } else if (obj instanceof Map && ((Map) obj).isEmpty()) {
            return true;
        } else if (obj instanceof Object[] && ((Object[]) obj).length == 0) {
            return true;
        }
        return false;
    }

    /**
     * 判断对象不为空
     *
     * @param obj 对象名
     * @return 是否不为空
     */
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    /**
     * @param str  String
     * @param len  int
     * @param type int 0:后补space；1：后补0；2：前补space；3：前补0
     * @return String
     */
    public static String formatString(String str, int len, int type) {
        str = str == null ? "" : str;
        StringBuffer buffer = new StringBuffer(str);
        if (str.length() > len) {
            return str.substring(str.length() - len, str.length());
        }
        for (int i = 0; i < len - str.length(); i++) {
            switch (type) {
                case 0:
                    buffer.append(' ');
                    break;
                case 1:
                    buffer.append('0');
                    break;
                case 2:
                    buffer.insert(0, ' ');
                    break;
                case 3:
                    buffer.insert(0, '0');
                    break;
                default:
                    break;
            }
        }
        return buffer.toString();
    }

    /**
     * 将链表集合转为String字符串
     *
     * @param collection
     * @param separator
     * @return
     */
    public static String collectionToString(Collection collection, String separator) {
        StringBuilder sb = new StringBuilder();
        Stream.of(collection).forEach(item -> sb.append(item).append(separator));
        sb.deleteCharAt(sb.lastIndexOf(separator));
        return sb.toString().replace(" ","");
    }
}
