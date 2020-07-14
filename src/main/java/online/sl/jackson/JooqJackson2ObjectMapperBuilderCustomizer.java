package online.sl.jackson;

import org.jooq.Query;
import org.jooq.Table;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

public class JooqJackson2ObjectMapperBuilderCustomizer implements Jackson2ObjectMapperBuilderCustomizer {

    @Override
    public void customize(Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder) {
        jacksonObjectMapperBuilder.mixIn(Table.class, IgnoreTypeTypeMixin.class)
                .mixIn(Query.class, IgnoreTypeTypeMixin.class);
    }

}