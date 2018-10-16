package cn.fintecher.pangolin.common.utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author:peishouwen
 * @Desc: json Object 互转
 * @Date:Create in 9:54 2017/12/20
 */
public class JacksonUtil {
    private static ObjectMapper mapper = new ObjectMapper();

    /**
     * 实体对象转为JSON串
     * @param obj
     * @return
     */
    public static String obj2Json(Object obj)  {
        StringWriter sw=null;
        JsonGenerator gen=null;
        try {
            sw = new StringWriter();
            gen=new JsonFactory().createGenerator(sw);
            mapper.writeValue(gen, obj);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                gen.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sw.toString();
    }

    /**
     * json串转为Object
     * @param jsonStr
     * @param objClass
     * @return
     * @throws IOException
     */
    public static Object json2Obj(String jsonStr,boolean isCollection,Class collectionClass, Class objClass) {
        try {
            mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,true);
            mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY,true);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,true);
            if(isCollection){
                return mapper.readValue(jsonStr,  mapper.getTypeFactory().constructCollectionType(collectionClass, objClass));
            }
            return mapper.readValue(jsonStr,objClass);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static Map parseNames(String names, Object sourceObject, Class<?> cl) {
        Map jsonMap = new HashMap<String,Object>();
        for (String name : names.split(",")) {
            try {
                // 生成值到新的对象中
                String upChar = name.substring(0, 1).toUpperCase();
                String getterStr = "get" + upChar + name.substring(1);
                Method getMethod = cl.getMethod(getterStr, new Class[] {});
                Object objValue = getMethod.invoke(sourceObject, new Object[] {});
                jsonMap.put(name, objValue);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return jsonMap;
    }


}
