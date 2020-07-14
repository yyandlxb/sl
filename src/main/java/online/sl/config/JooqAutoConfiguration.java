package online.sl.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import online.sl.jackson.JooqJackson2ObjectMapperBuilderCustomizer;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;

@Configurable
@ConditionalOnClass(DSLContext.class)
public class JooqAutoConfiguration {

    @Bean
    @ConditionalOnClass(ObjectMapper.class)
    public Jackson2ObjectMapperBuilderCustomizer jooqJackson2ObjectMapperBuilderCustomizer() {
        return new JooqJackson2ObjectMapperBuilderCustomizer();
    }

}