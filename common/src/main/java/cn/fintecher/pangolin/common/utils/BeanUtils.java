package cn.fintecher.pangolin.common.utils;

import com.google.common.collect.Lists;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.FeatureDescriptor;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by ChenChang on 2017/8/23.
 */
public class BeanUtils extends org.springframework.beans.BeanUtils {

    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
        return Stream.of(wrappedSource.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
                .toArray(String[]::new);
    }

    public static void copyPropertiesIgnoreNull(Object source, Object target) {
        BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }

    public static void copyPropertiesIgnoreNull(Object source, Object target, String... ignoreProperties) {
        List<String> list = Lists.newArrayList(ignoreProperties);
        list.addAll(Lists.newArrayList(getNullPropertyNames(source)));
        BeanUtils.copyProperties(source, target, getNullPropertyNames(list.toArray()));
    }
}
