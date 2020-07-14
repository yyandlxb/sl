package online.sl.format;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

@Configuration
@ConditionalOnWebApplication
public class FormatterRegistrarAutoConfiguration {

    @Configuration
    @ConditionalOnClass(Timestamp.class)
    public static class SqlDateTimeFormatterRegistrar implements WebMvcConfigurer {

        @Override
        public void addFormatters(FormatterRegistry registry) {
            registry.addConverter(new UtilDateToTimestampConverter());
            registry.addConverter(new UtilDateToDateConverter());
            registry.addConverter(new UtilDateToTimeConverter());
        }

        public static class UtilDateToTimestampConverter implements Converter<Date, Timestamp> {

            @Override
            public Timestamp convert(Date source) {
                return new Timestamp(source.getTime());
            }

        }

        public static class UtilDateToDateConverter implements Converter<Date, java.sql.Date> {

            @Override
            public java.sql.Date convert(Date source) {
                return new java.sql.Date(source.getTime());
            }

        }

        public static class UtilDateToTimeConverter implements Converter<Date, Time> {

            @Override
            public Time convert(Date source) {
                return new Time(source.getTime());
            }

        }

    }

}
