package com.fm.SequenceBased;

import POJO.ODS.RealTimeRankData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.stereotype.Component;

@Component
public class RealTimeRankDataSerializer implements Serializer<RealTimeRankData> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static{
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public byte[] serialize(String topic, RealTimeRankData data) {
        if(data == null){
            return null;
        }
        try{
            return objectMapper.writeValueAsString(data).getBytes();
        }catch (Exception e){
            throw new RuntimeException("序列化RealTimeRankData失败", e);
        }
    }
}
