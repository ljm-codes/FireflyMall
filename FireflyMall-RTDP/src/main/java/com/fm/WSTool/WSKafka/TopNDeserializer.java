package com.fm.WSTool.WSKafka;

import POJO.ADS.TopNData;
import POJO.DataList;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class TopNDeserializer implements Deserializer<DataList<TopNData>> {

    private final static ObjectMapper mapper = new ObjectMapper();

    private final TypeReference<DataList<TopNData>> typeRef = new TypeReference<>() {};

    @Override
    public DataList<TopNData> deserialize(String topic, byte[] data) {
        try {
            if (data == null)
                return null;
            else
                return mapper.readValue(new String(data, StandardCharsets.UTF_8), typeRef);
        } catch (Exception e) {
            throw new SerializationException("Error when deserializing byte[] to TopNData");
        }
    }
}

