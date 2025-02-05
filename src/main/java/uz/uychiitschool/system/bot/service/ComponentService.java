package uz.uychiitschool.system.bot.service;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class ComponentService {

    @Bean
    public InlineKeyboardMarkup generateCertificateMarkup() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();

        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setCallbackData("generateCertificate");
        button1.setText("Certificate savodxonlik \uD83D\uDE80");

        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setCallbackData("generateCertificateWeekly");
        button2.setText("Certificate malaka oshirish \uD83D\uDE80");

        row1.add(button1);
        row2.add(button2);
        rows.add(row1);
        rows.add(row2);
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }
}
