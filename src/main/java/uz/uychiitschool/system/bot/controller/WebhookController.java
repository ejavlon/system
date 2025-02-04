package uz.uychiitschool.system.bot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import uz.uychiitschool.system.bot.ItSchoolBot;
import uz.uychiitschool.system.bot.service.ComponentService;
import uz.uychiitschool.system.bot.service.TelegramCertificateService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/webhook")
@RequiredArgsConstructor
public class WebhookController {
    private final ItSchoolBot bot;
    private final TelegramCertificateService telegramCertificateService;
    private final ComponentService componentService;

    private boolean isWeekly;

    @PostMapping
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {

        if (update.hasMessage()
                && update.getMessage().hasDocument()
                && List.of(
                        "ejavlon",
                        "iTmaktabUychi",
                        "ahrorbek_mamadaliyev12",
                        "NazarovSherzod"
                ).contains(update.getMessage().getFrom().getUserName())
        ) {

            handleDocument(update);
        } else if (update.hasMessage()) {
            handleMessage(update);
        } else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update);
        }

        return null;
    }


    public void handleMessage(Update update) {
        Message message = update.getMessage();
        User from = message.getFrom();

        if ("/start".equals(message.getText()) && List.of("ejavlon", "iTmaktabUychi").contains(from.getUserName())) {
            InlineKeyboardMarkup inlineKeyboardMarkup = componentService.generateCertificateMarkup();
            bot.sendMessage(
                    message.getChatId().toString(),
                    String.format("Certificate yaratishni boshlang %s %s !", from.getFirstName(), from.getLastName()),
                    inlineKeyboardMarkup
            );
            isWeekly = false;
        } else if (message.hasText() && message.getText().startsWith("/start")) {
            String text = message.getText();
            String id = text.substring(message.getText().indexOf("start") + 6);
            telegramCertificateService.sendCertificate(UUID.fromString(id), message.getChatId().toString());
        }
    }

    public void handleCallbackQuery(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();


        if ("generateCertificate".equals(callbackQuery.getData())) {
            bot.deleteMessage(
                    callbackQuery.getMessage().getChatId(),
                    callbackQuery.getMessage().getMessageId()
            );
            telegramCertificateService.sendExcelFile(callbackQuery.getMessage().getChatId().toString());
        } else if ("generateCertificateWeekly".equals(callbackQuery.getData())) {
            telegramCertificateService.sendExcelFile(callbackQuery.getMessage().getChatId().toString());
            isWeekly = true;
        }
    }

    public void handleDocument(Update update) {
        Message message = update.getMessage();
        Document document = update.getMessage().getDocument();
        if ((document.getMimeType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
                document.getMimeType().equals("application/vnd.ms-excel")) && !isWeekly) {
            telegramCertificateService.generateCertificate(message,false);
        }else if ((document.getMimeType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
                document.getMimeType().equals("application/vnd.ms-excel")) && isWeekly){
            telegramCertificateService.generateCertificate(message,true);
        }
    }
}
