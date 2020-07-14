package online.sl.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "wx.api")
public class WeChatConfig {

    private String appId, secret;
}
