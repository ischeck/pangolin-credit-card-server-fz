package cn.fintecher.pangolin.common.model;

import lombok.Data;

/**
 * Created by ChenChang on 2018/6/12.
 */
@Data
public class DataDict {
    private String code;
    private String name;

    public DataDict(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
