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
import uz.uychiitschool.system.bot.Bot2;

@RestController
@RequestMapping("/api/v1/webhook")
@RequiredArgsConstructor
public class WebhookController {
    private final Bot2 bot;


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
        bot.sendMessage(message.getChatId().toString(), "Salom " + from.getFirstName() + " " + from.getLastName());
    }

    public void handleCallbackQuery(Update update){

    }
}
