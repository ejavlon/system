package uz.uychiitschool.system.bot.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;

@Configuration
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class BotConfig {

    @Value("${bot.username}")
    String username;

    @Value("${bot.webhook-path}")
    String webhookPath;

    @Value("${bot.set-webhook-url}")
    String setWebhookUrl;

    @Value("${bot.token}")
    String token;

    @Value("${zonaid}")
    String zonaId;

    private final Environment environment;

    @Bean
    public SetWebhook setWebhookInstance() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.exchange(
                String.format(
                        environment.getProperty("bot.set-webhook-url"),
                        environment.getProperty("bot.token"),
                        environment.getProperty("bot.webhook-path")
                ),
                HttpMethod.GET, HttpEntity.EMPTY, Object.class
        );

        return SetWebhook.builder()
                .url(this.getWebhookPath())
                .build();
    }

//    @Bean
//    public SetWebhook setWebhook() {
//        SetWebhook setWebhook = new SetWebhook();
//        setWebhook.setUrl(this.webhookPath);
//        return setWebhook;
//    }


}
