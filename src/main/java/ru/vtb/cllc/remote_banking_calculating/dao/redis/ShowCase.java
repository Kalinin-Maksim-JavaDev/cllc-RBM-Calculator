package ru.vtb.cllc.remote_banking_calculating.dao.redis;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.vtb.cllc.remote_banking_calculating.model.Record;

@Data
@RequiredArgsConstructor
public class ShowCase {

    private final Record[] records;
}
