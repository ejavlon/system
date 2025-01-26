package uz.uychiitschool.system.bot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import uz.uychiitschool.system.bot.ItSchoolBot;
import uz.uychiitschool.system.bot.service.TelegramCertificateService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/webhook")
@RequiredArgsConstructor
public class WebhookController {
    private final ItSchoolBot bot;
    private final TelegramCertificateService telegramCertificateService;

    @PostMapping
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        if (update.hasMessage()){
            handleMessage(update);
        }else if(update.hasCallbackQuery()){
            handleCallbackQuery(update);
        }
        return null;
    }

    public void handleMessage(Update update){
        Message message = update.getMessage();
        User from = message.getFrom();
        System.out.println("message.getText() = " + message.getText());

        if (message.hasText() && message.getText().startsWith("/start")){
            String text = message.getText();
            String id = text.substring(message.getText().indexOf("start") + 6);

            telegramCertificateService.sendCertificate(UUID.fromString(id), message.getChatId().toString());
        }
//        this.bot.sendMessage(message.getChatId().toString(), "Salom " + from.getFirstName() + " " + from.getLastName());
    }

    public void handleCallbackQuery(Update update){

    }
}
