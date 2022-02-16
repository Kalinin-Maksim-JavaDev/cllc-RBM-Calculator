package ru.vtb.cllc.remote_banking_calculating.resolver;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import org.springframework.stereotype.Component;
import ru.vtb.cllc.remote_banking_calculating.model.Group;

import java.util.List;

@Component
public class GroupResolver implements GraphQLQueryResolver {

    public Group group(String name) {
        return new Group(name, List.of());
    }
}