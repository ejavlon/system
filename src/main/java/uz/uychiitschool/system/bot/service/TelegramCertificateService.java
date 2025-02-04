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
import uz.uychiitschool.system.web.core.entity.Certificate;
import uz.uychiitschool.system.web.core.entity.Student;
import uz.uychiitschool.system.web.core.service.CertificateService;
import uz.uychiitschool.system.web.core.service.StudentService;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TelegramCertificateService {
    private final ItSchoolBot itSchoolBot;
    private final CertificateService certificateService;
    private final StudentService studentService;

    public void sendCertificate(UUID id, String chatId) {
        Certificate certificate = certificateService.findCertificateByIdOrThrow(id);
        Student student = certificate.getStudent();
        byte[] bytes = certificateService.createPdfFromImageByIdOrCertificate(certificate.getId(), certificate);
        InputFile inputFile = new InputFile(new ByteArrayInputStream(bytes), String.format("%s%s.pdf", certificate.getSerial(),certificate.getNumber()));
        itSchoolBot.sendDocument(chatId, String.format("%S %S", student.getLastName(), student.getFirstName()), inputFile);
    }

    public void sendCertificateFromList(List<Certificate> certificates, String chatId, boolean isWeekLy) {

        for (int i = 0; i < certificates.size(); i++) {
            final int k = i;
            new Thread(() -> {
                Certificate certificate = certificates.get(k);
                Student student = certificate.getStudent();
                byte[] bytes;
                if (isWeekLy){
                    bytes = certificateService.createPdfFromImageByIdOrCertificateWeekly(certificate.getId(), certificate);
                }else{
                    bytes = certificateService.createPdfFromImageByIdOrCertificate(certificate.getId(), certificate);
                }
                InputFile inputFile = new InputFile(new ByteArrayInputStream(bytes), String.format("%s%s.pdf", certificate.getSerial(),certificate.getNumber()));
                itSchoolBot.sendDocument(chatId, String.format("%S %S", student.getLastName(), student.getFirstName()), inputFile);
            }).start();
        }
    }

    public void sendExcelFile(String chatId){
        try {
            InputStream inputStream = new ClassPathResource("xls/EXAMPLE.xlsx").getInputStream();
            InputFile inputFile = new InputFile(inputStream, "example.xlsx");
            itSchoolBot.sendDocument(chatId, "Quyidagi shaklda namunani to'ldirib yuboring", inputFile);

        }catch (Exception e){
            e.printStackTrace();
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
                if (firstRow){
                    firstRow = false;
                    continue;
                }

                boolean firstCell = true;
                Student student = new Student();
                Certificate certificate = new Certificate();

                for (Cell cell : row) {
                    if (firstCell){
                        String fullName = cell.getStringCellValue();
                        String[] fullNameArr = fullName.split(" ", 2);
                        student.setFirstName(fullNameArr[1]);
                        student.setLastName(fullNameArr[0]);
                        students.add(student);
                        firstCell = false;
                    }else {
                        if (cell.getCellType() == CellType.NUMERIC){
//                            LocalDate localDate = LocalDate.parse(cell.getDateCellValue(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                            Date date = cell.getDateCellValue();
                            LocalDateTime localDateTime = LocalDateTime.of(date.getYear(), date.getMonth(), date.getDay(), 0, 0, 0);
                            certificate.setStudent(student);
                            certificate.setDate(localDateTime);
                            certificates.add(certificate);
                        }else if (cell.getCellType() == CellType.STRING){
                            LocalDate localDate = LocalDate.parse(cell.getStringCellValue(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                            LocalDateTime localDateTime = localDate.atStartOfDay();
                            certificate.setStudent(student);
                            certificate.setDate(localDateTime);
                            certificates.add(certificate);
                        }

                    }
                }
            }
            studentService.saveStudentFromList(students);
            List<Certificate> savedCertificates = certificateService.saveCertificateFromList(certificates);
            sendCertificateFromList(savedCertificates, message.getChatId().toString(), isWeekly);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void uploadExcelFile(String fileId) {
        try {
            org.telegram.telegrambots.meta.api.objects.File file = itSchoolBot.execute(new GetFile(fileId));
            String fileUrl = "https://api.telegram.org/file/bot" + itSchoolBot.getBotToken() + "/" + file.getFilePath();
            Path filePath = Path.of("downloads/data.xlsx");
            Files.createDirectories(filePath.getParent());
            FileUtils.copyURLToFile(new URL(fileUrl), filePath.toFile());

            System.out.println("Excel fayl yuklab olindi: " + filePath.toAbsolutePath());

        } catch (TelegramApiException | IOException e) {
            e.printStackTrace();
        }
    }


}
