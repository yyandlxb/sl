package online.sl;

import okhttp3.OkHttpClient;
import online.sl.config.WeChatConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.net.ssl.X509TrustManager;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static online.sl.session.SSLSocketClient.*;

@EnableConfigurationProperties({WeChatConfig.class})
@SpringBootApplication(scanBasePackages = {"online.sl"})
@EnableScheduling
@EnableTransactionManagement(proxyTargetClass = true)
public class Bootstrap implements WebMvcConfigurer {
    private HandlerInterceptor[] interceptors;

    private HandlerMethodArgumentResolver[] argumentResolvers;

    @Autowired
    public void setInterceptors(HandlerInterceptor[] interceptors) {
        this.interceptors = interceptors;
    }

    @Autowired
    public void setArgumentResolvers(HandlerMethodArgumentResolver[] argumentResolvers) {
        this.argumentResolvers = argumentResolvers;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        Stream.of(interceptors).forEach(registry::addInterceptor);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.addAll(Arrays.asList(this.argumentResolvers));
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public OkHttpClient okHttpClient(){
        return new OkHttpClient().newBuilder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .sslSocketFactory(getSSLSocketFactory(), (X509TrustManager) getTrustManager()[0])//配置
                .hostnameVerifier(getHostnameVerifier())//配置
                .build();
    }


    public static void main(String[] args) {

        SpringApplication.run(Bootstrap.class, args);
    }

}
