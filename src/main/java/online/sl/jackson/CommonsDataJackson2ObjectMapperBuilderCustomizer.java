package online.sl.jackson;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

public class CommonsDataJackson2ObjectMapperBuilderCustomizer implements Jackson2ObjectMapperBuilderCustomizer {

    @Override
    public void customize(Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder) {
        jacksonObjectMapperBuilder.mixIn(Pageable.class, IgnoreTypeTypeMixin.class)
                .mixIn(Sort.class, IgnoreTypeTypeMixin.class);
        jacksonObjectMapperBuilder.serializerByType(SliceImpl.class, new SliceJsonSerializer());
    }

}
