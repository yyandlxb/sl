package online.sl.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import online.sl.jackson.CommonsDataJackson2ObjectMapperBuilderCustomizer;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Pageable;

@Configurable
@ConditionalOnClass(Pageable.class)
public class CommonsDataAutoConfiguration {

    @Bean
    @ConditionalOnClass(ObjectMapper.class)
    public Jackson2ObjectMapperBuilderCustomizer commonsDataJackson2ObjectMapperBuilderCustomizer() {
        return new CommonsDataJackson2ObjectMapperBuilderCustomizer();
    }

}
