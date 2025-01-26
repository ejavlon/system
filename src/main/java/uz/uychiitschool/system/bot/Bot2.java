package uz.uychiitschool.system.bot;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.uychiitschool.system.bot.config.BotConfig;

@Component
public class Bot2 extends TelegramWebhookBot {
    private final BotConfig botConfig;

    public Bot2(BotConfig botConfig) {
        super(botConfig.getToken());
        this.botConfig = botConfig;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChatId().toString());
        message.setText("SAlom");
        return message;

    }

    @Override
    public String getBotPath() {
        return this.botConfig.getWebhookPath();
    }

    @Override
    public String getBotUsername() {
        return this.botConfig.getUsername();
    }

    @Override
    public void setWebhook(SetWebhook setWebhook) throws TelegramApiException {
        setWebhook.setUrl(botConfig.getWebhookPath());
        super.setWebhook(setWebhook);

    }

    @SneakyThrows
    public void sendMessage(String chatId, String message, InlineKeyboardMarkup markup){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        sendMessage.setReplyMarkup(markup);
        execute(sendMessage);
    }

    @SneakyThrows
    public void sendMessage(String chatId, String message){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        execute(sendMessage);
    }
}
