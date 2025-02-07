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
import uz.uychiitschool.system.bot.service.TelegramCertificateService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/webhook")
@RequiredArgsConstructor
public class WebhookController {
    private final ItSchoolBot bot;
    private final TelegramCertificateService telegramCertificateService;
    private final InlineKeyboardMarkup certificateTypeMarkup;

    private boolean isWeekly;

    @PostMapping
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        if (update.hasMessage()
                && update.getMessage().hasDocument()
                && List.of("ejavlon", "ahrorbek_mamadaliyev", "iTmaktabUychi", "NazarovSherzod")
                .contains(update.getMessage().getFrom().getUserName())
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

        if (message.hasText() &&
                message.getText().length() > 10 &&
                message.getText().startsWith("/start")) {
            String text = message.getText();
            String id = text.substring(message.getText().indexOf("start") + 6).trim();
            UUID uuidFromString;
            try {
                uuidFromString = UUID.fromString(id);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Doesn't match UUID");
            }
            telegramCertificateService.sendCertificate(uuidFromString, message.getChatId().toString());
        } else if ("/start".equals(message.getText()) && List.of("ejavlon", "ahrorbek_mamadaliyev", "iTmaktabUychi", "NazarovSherzod")
                .contains(from.getUserName())) {
            bot.sendMessage(
                    message.getChatId().toString(),
                    "Iltimos kerakli certificate turidan birini tanlang!",
                    certificateTypeMarkup
            );
            isWeekly = false;
        }
    }

    public void handleCallbackQuery(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();

        bot.deleteMessage(
                callbackQuery.getMessage().getChatId(),
                callbackQuery.getMessage().getMessageId()
        );
        if ("generateCertificate".equals(callbackQuery.getData())) {
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
            telegramCertificateService.generateCertificate(message, false);
        } else if ((document.getMimeType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
                document.getMimeType().equals("application/vnd.ms-excel")) && isWeekly) {
            telegramCertificateService.generateCertificate(message, true);
        }
    }
}
