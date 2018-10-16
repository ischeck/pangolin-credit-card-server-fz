package cn.fintecher.pangolin.common.web;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Created by ChenChang on 2018/8/12.
 */
public class MoneyDoubleSerializer extends StdSerializer<Double> {
    public final static MoneyDoubleSerializer instance = new MoneyDoubleSerializer();

    public MoneyDoubleSerializer() {
        super(Double.class);
    }


    @Override
    public void serialize(Double value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (null == value) {
            //write the word 'null' if there's no value available
            gen.writeNull();
        } else {
            BigDecimal bigDecimal = new BigDecimal(value);
            BigDecimal roundUp = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
            gen.writeNumber(roundUp);
        }
    }
}
