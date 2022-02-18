package ru.vtb.cllc.remote_banking_calculating.dao.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Repository
public class ShowCaseRepository {

    public static final String KEY_PREFIX = "noagg";
    private final ValueOperations<String, ShowCase> valueOperations;

    public ShowCaseRepository(RedisTemplate redisTemplate) {
        this.valueOperations = redisTemplate.opsForValue();
    }

    public List<ShowCase> get(LocalDate date) {

        List<String> keys = List.of(KEY_PREFIX.concat("_").concat(date.format(DateTimeFormatter.ISO_DATE)));
        return valueOperations.multiGet(keys);
    }
}
