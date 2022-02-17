package ru.vtb.cllc.remote_banking_calculating.service.resolver;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import ru.vtb.cllc.remote_banking_calculating.model.indicator.AHT;

import java.util.List;

public class AHTResolver implements GraphQLQueryResolver {
    //GET http://localhost:8080/graphql?query={group(name: "byDqqay") {name,groups{name}}}
    public List<AHT> metric() {
        return null;
    }
}
