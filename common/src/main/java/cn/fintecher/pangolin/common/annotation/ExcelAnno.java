package cn.fintecher.pangolin.common.annotation;

import java.lang.annotation.*;

/**
 * @Author: PeiShouWen
 * @Description: Excel导入注解
 * @Date 15:04 2017/3/3
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ExcelAnno {
    /**
     * 在excel文件中某列数据的名称
     *
     * @return 名称
     */
    String cellName() default "";

    /**
     * 实体中的字段类型
     * @return
     */

    FieldDataType fieldDataType() default FieldDataType.STRING;


    FieldType fieldType() default FieldType.CASE;

    FieldInput fieldInput() default FieldInput.NO;


    /**
     * 实体中的字段类型枚举
     */
    enum FieldDataType{
        STRING,
        INTEGER,
        DOUBLE,
        DATE
    }

    /**
     * 字段是否必输
     */
    enum FieldInput{
        YES,
        NO
    }

    /**
     * 字段属性类型
     */
    enum FieldType{
        CASE,
        PRODUCT,
        PERSONAL,
        ACCOUNT,
        TEXTAREA,
        SELECT,
        DATEPICKER,
        RADIO,
        INPUT
    }


}
