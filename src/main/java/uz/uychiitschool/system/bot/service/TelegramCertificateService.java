package uz.uychiitschool.system.bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import uz.uychiitschool.system.bot.ItSchoolBot;
import uz.uychiitschool.system.web.core.entity.Certificate;
import uz.uychiitschool.system.web.core.entity.Student;
import uz.uychiitschool.system.web.core.service.CertificateService;

import java.io.ByteArrayInputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TelegramCertificateService {
    private final ItSchoolBot itSchoolBot;
    private final CertificateService certificateService;

    public void sendCertificate(UUID id, String chatId) {
        Certificate certificate = certificateService.findCertificateByIdOrThrow(id);
        Student student = certificate.getStudent();
        byte[] bytes = certificateService.createPdfFromImageByIdOrCertificate(certificate.getId(), certificate);
        InputFile inputFile = new InputFile(new ByteArrayInputStream(bytes), String.format("%s%s.pdf", certificate.getSerial(),certificate.getNumber()));
        itSchoolBot.sendDocument(chatId, String.format("%S %S", student.getLastName(), student.getFirstName()), inputFile);
    }

}
