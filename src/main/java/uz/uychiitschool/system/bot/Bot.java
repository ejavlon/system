//package uz.uychiitschool.system.bot;
//
//import org.springframework.stereotype.Component;
//import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//import org.telegram.telegrambots.meta.api.objects.Update;
//import org.telegram.telegrambots.starter.SpringWebhookBot;
//import uz.uychiitschool.system.bot.config.BotConfig;
//
//@Component
//public class Bot extends SpringWebhookBot {
//    private final BotConfig botConfig;
//
//    public Bot(BotConfig botConfig) {
//        super(botConfig.setWebhookInstance(), botConfig.getToken());
//        this.botConfig = botConfig;
//    }
//
//    @Override
//    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
//        SendMessage message = new SendMessage();
//        message.setChatId(update.getMessage().getChatId().toString());
//        message.setText("SAlom");
//        return message;
//
//    }
//
//    @Override
//    public String getBotPath() {
//        return this.botConfig.getWebhookPath();
//    }
//
//    @Override
//    public String getBotUsername() {
//        return this.botConfig.getUsername();
//    }
//
//}
