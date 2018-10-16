package cn.fintecher.pangolin.common.exception;

/**
 * Created by ChenChang on 2018/6/8.
 */
public class BadRequestException extends RuntimeException {
    private Object[] args;
    private String objectName;

    public BadRequestException(Object[] args, String objectName, String message) {
        super(message);
        this.objectName = objectName;
        this.args = args;

    }

    public String getObjectName() {
        return objectName;
    }

    public Object[] getArgs() {
        return args;
    }
}
