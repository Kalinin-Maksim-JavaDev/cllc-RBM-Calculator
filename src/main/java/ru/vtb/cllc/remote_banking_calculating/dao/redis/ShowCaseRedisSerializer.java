package ru.vtb.cllc.remote_banking_calculating.dao.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Component;
import ru.vtb.cllc.remote_banking_calculating.model.Record;

import java.io.IOException;

@Component
class ShowCaseRedisSerializer implements RedisSerializer<ShowCase> {
    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(ShowCase showCase) throws SerializationException {
        try {
            return objectMapper.writeValueAsString(showCase.getRecords()).getBytes();
        } catch (JsonProcessingException e) {
            throw new SerializationException(e.getMessage());
        }
    }

    @Override
    public ShowCase deserialize(byte[] bytes) throws SerializationException {

        objectMapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
        try {
            return new ShowCase(objectMapper.readValue(bytes, Record[].class));
        } catch (IOException e) {
            throw new SerializationException(e.getMessage());
        }
    }
}
