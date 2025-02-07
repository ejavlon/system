package uz.uychiitschool.system.bot.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Document;

import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.uychiitschool.system.bot.ItSchoolBot;
import uz.uychiitschool.system.web.base.exception.DataNotFoundException;
import uz.uychiitschool.system.web.core.entity.Certificate;
import uz.uychiitschool.system.web.core.entity.Student;
import uz.uychiitschool.system.web.core.service.CertificateService;
import uz.uychiitschool.system.web.core.service.StudentService;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TelegramCertificateService {
    private final ItSchoolBot itSchoolBot;
    private final CertificateService certificateService;
    private final StudentService studentService;

    public void sendCertificate(UUID id, String chatId) {
        Certificate certificate;
        try {
            certificate = certificateService.findCertificateByIdOrThrow(id);
        } catch (DataNotFoundException e) {
            itSchoolBot.sendMessage(chatId, "Bunday sertificate mavjud emas!");
            return;
        }
        Student student = certificate.getStudent();
        byte[] bytes;
        if (certificate.getWeekly()) {
            bytes = certificateService.createPdfFromImageByIdOrCertificateWeekly(certificate.getId(), certificate);
        } else {
            bytes = certificateService.createPdfFromImageByIdOrCertificate(certificate.getId(), certificate);
        }
        InputFile inputFile = new InputFile(new ByteArrayInputStream(bytes), String.format("%s%s.pdf", certificate.getSerial(), certificate.getNumber()));
        itSchoolBot.sendDocument(chatId, String.format("%S %S", student.getLastName(), student.getFirstName()), inputFile);
    }

    public void sendCertificateFromList(List<Certificate> certificates, String chatId, boolean isWeekLy) {
        for (int i = 0; i < certificates.size(); i++) {
            final int k = i;
            new Thread(() -> {
                Certificate certificate = certificates.get(k);
                Student student = certificate.getStudent();
                byte[] bytes;
                if (isWeekLy) {
                    bytes = certificateService.createPdfFromImageByIdOrCertificateWeekly(certificate.getId(), certificate);
                } else {
                    bytes = certificateService.createPdfFromImageByIdOrCertificate(certificate.getId(), certificate);
                }
                InputFile inputFile = new InputFile(new ByteArrayInputStream(bytes), String.format("%s%s.pdf", certificate.getSerial(), certificate.getNumber()));
                itSchoolBot.sendDocument(chatId, String.format("%S %S", student.getLastName(), student.getFirstName()), inputFile);
            }).start();
        }
    }

    public void sendExcelFile(String chatId) {
        try {
            InputStream inputStream = new ClassPathResource("xls/EXAMPLE.xlsx").getInputStream();
            InputFile inputFile = new InputFile(inputStream, "example.xlsx");
            itSchoolBot.sendDocument(chatId, "Quyidagi shaklda namunani to'ldirib yuboring", inputFile);

        } catch (Exception e) {
            throw new RuntimeException("something went wrong", e);
        }
    }

    public void generateCertificate(Message message, boolean isWeekly) {
        Document document = message.getDocument();
        uploadExcelFile(document.getFileId());
        String path = new ClassPathResource("downloads/data.xlsx").getPath();

        try (FileInputStream fis = new FileInputStream(new File(path));
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // Birinchi sahifa (sheet)
            boolean firstRow = true;

            List<Student> students = new ArrayList<>();
            List<Certificate> certificates = new ArrayList<>();

            for (Row row : sheet) {
                if (firstRow) {
                    firstRow = false;
                    continue;
                }

                boolean firstCell = true;
                Student student = new Student();
                Certificate certificate = new Certificate();

                for (Cell cell : row) {
                    if (firstCell) {
                        String fullName = cell.getStringCellValue();
                        System.out.println("fullName = " + fullName);
                        String[] fullNameArr = fullName.split(" ", 2);
                        student.setFirstName(fullNameArr[1]);
                        student.setLastName(fullNameArr[0]);
                        students.add(student);
                        firstCell = false;
                    } else {
                        if (cell.getCellType() == CellType.NUMERIC) {
                            Date date = cell.getDateCellValue();
                            Instant instant = date.toInstant();
                            LocalDateTime localDateTime = instant.atZone(ZoneId.of("Asia/Tashkent")).toLocalDateTime();
                            certificate.setStudent(student);
                            certificate.setDate(localDateTime);
                        } else if (cell.getCellType() == CellType.STRING) {
                            try {
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                                LocalDate localDate = LocalDate.parse(cell.getStringCellValue().trim(), formatter);
                                LocalDateTime localDateTime = localDate.atStartOfDay();
                                certificate.setStudent(student);
                                certificate.setDate(localDateTime);
                            } catch (DateTimeParseException e) {
                                System.err.println("Xatolik: Noto‘g‘ri sana formati - " + cell.getStringCellValue());
                            }
                        }
                        certificate.setWeekly(isWeekly);
                        certificates.add(certificate);
                    }
                }
            }
            studentService.saveStudentFromList(students);
            List<Certificate> savedCertificates = certificateService.saveCertificateFromList(certificates);
            sendCertificateFromList(savedCertificates, message.getChatId().toString(), isWeekly);
        } catch (IOException e) {
            throw new RuntimeException("Something went wrong when reading excel file", e);
        }
    }

    private void uploadExcelFile(String fileId) {
        try {
            org.telegram.telegrambots.meta.api.objects.File file = itSchoolBot.execute(new GetFile(fileId));
            String fileUrl = "https://api.telegram.org/file/bot" + itSchoolBot.getBotToken() + "/" + file.getFilePath();
            Path filePath = Path.of("downloads/data.xlsx");
            Files.createDirectories(filePath.getParent());
            FileUtils.copyURLToFile(new URL(fileUrl), filePath.toFile());
        } catch (TelegramApiException | IOException e) {
            e.printStackTrace();
        }
    }


}
