package uz.uychiitschool.system.web.core.service;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uychiitschool.system.web.base.dto.ResponseApi;
import uz.uychiitschool.system.web.base.entity.User;
import uz.uychiitschool.system.web.base.exception.DataNotFoundException;
import uz.uychiitschool.system.web.base.service.UserService;
import uz.uychiitschool.system.web.core.dto.CertificateDto;
import uz.uychiitschool.system.web.core.entity.Certificate;
import uz.uychiitschool.system.web.core.entity.Course;
import uz.uychiitschool.system.web.core.entity.Student;
import uz.uychiitschool.system.web.core.repository.CertificateRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CertificateRepository repository;
    private final CourseService courseService;
    private final UserService userService;
    private final StudentService studentService;
    private final QrCodeService qrCodeService;
//    private final CertificateService selfService;

    public ResponseApi<Page<Certificate>> getAllCertificates(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.sort(Certificate.class)
                .by(Certificate::getDate).ascending());

        Page<Certificate> certificates = repository.findAll(pageable);

        return ResponseApi.createResponse(certificates, "All certificates", true);
    }

    public ResponseApi<Certificate> getCertificateById(UUID id) {
        Certificate certificate = findCertificateByIdOrThrow(id);
        return ResponseApi.createResponse(certificate, "Certificate by id", true);
    }

    @Transactional
    public ResponseApi<Certificate> create(CertificateDto certificateDto){
        Certificate certificate = createOrUpdateCertificate(certificateDto, null);
        certificate = repository.save(certificate);
        return ResponseApi.createResponse(certificate, "Certificate successfully created", true);
    }


    @Transactional
    public ResponseApi<Certificate> update(UUID id, CertificateDto certificateDto) {
        Certificate existingCertificate = findCertificateByIdOrThrow(id);
        existingCertificate = createOrUpdateCertificate(certificateDto, existingCertificate);
        return ResponseApi.createResponse(existingCertificate, "Certificate successfully updated", true);
    }

    public ResponseApi<Certificate> deleteCertificateById(UUID id) {
        Certificate certificate = findCertificateByIdOrThrow(id);
        repository.delete(certificate);
        return ResponseApi.createResponse(certificate, "Certificate successfully deleted", true);
    }

    @Transactional
    public Certificate createCertificate(CertificateDto certificateDto){
        if (certificateDto.getDate() != null && certificateDto.getDate().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Certificate date is after now");
        }

        LocalDateTime date = certificateDto.getDate() != null ? certificateDto.getDate() : LocalDateTime.now();
        Course course = courseService.findCourseByIdOrThrow(certificateDto.getCourseId());

        Student student = studentService.findStudentByIdOrThrow(certificateDto.getStudentId());

        boolean anyMatch = student.getCertificates().stream().anyMatch(certificate -> certificate.getCourse().equals(course));
        if (anyMatch) {
            throw new RuntimeException("Certificate already exists");
        }

        User teacher = userService.findUserByIdOrUsernameOrThrow(certificateDto.getTeacherId(), null);
        String uuid = UUID.randomUUID().toString();
        return Certificate.builder()
                .date(date)
                .course(course)
                .teacher(teacher)
                .student(student)
                .serial(uuid.substring(0, uuid.indexOf("-")).replaceAll("[^a-zA-Z]", "").substring(0,2).toUpperCase())
                .number(String.valueOf(repository.count() + 10000))
                .build();
    }

    @Transactional
    public Certificate createOrUpdateCertificate(CertificateDto certificateDto, Certificate exsistingCertificate) {
        if (exsistingCertificate == null){
            return createCertificate(certificateDto);
        }

        if (certificateDto.getDate() != null && certificateDto.getDate().isBefore(LocalDateTime.now())) {
            exsistingCertificate.setDate(certificateDto.getDate());
        }

        if (certificateDto.getStudentId() != null) {
            Student student = studentService.findStudentByIdOrThrow(certificateDto.getStudentId());
            exsistingCertificate.setStudent(student);
        }

        if (certificateDto.getTeacherId() != null) {
            User teacher = userService.findUserByIdOrUsernameOrThrow(certificateDto.getTeacherId(), null);
            exsistingCertificate.setTeacher(teacher);
        }

        if (certificateDto.getCourseId() != null) {
            Course course = courseService.findCourseByIdOrThrow(certificateDto.getCourseId());
            exsistingCertificate.setCourse(course);
        }

        return exsistingCertificate;
    }

    public Certificate findCertificateByIdOrThrow(UUID id) {
        return repository.findById(id).orElseThrow(() -> new DataNotFoundException("Certificate not found"));
    }

    public byte [] createPdfFromImage(UUID id)  {
        try {
            Certificate certificate = findCertificateByIdOrThrow(id);
            BufferedImage image = addTextToImage(certificate);
            image = qrCodeService.addQrCodeToImage(String.format("https://t.me/uychi_it_school_bot?start=%s", certificate.getId()),image);

            // Rasmdan byte massivini olish
            ByteArrayOutputStream imageOutputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", imageOutputStream);
            byte[] imageBytes = imageOutputStream.toByteArray();

            // PDF faylni vaqtinchalik oqimda yaratish
            ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(pdfOutputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);

            // Sahifa formatini o'rnatish (A4, kerakli formatda)
            pdfDoc.setDefaultPageSize(PageSize.A4.rotate()); // Yoki `PageSize.A4` agar portret bo'lsa

            // Dokument yaratish
            Document document = new Document(pdfDoc);

            // PDF sahifasining o'lchamlarini olish
            float pdfWidth = pdfDoc.getDefaultPageSize().getWidth();
            float pdfHeight = pdfDoc.getDefaultPageSize().getHeight();

            // Rasmni PDF sahifasiga qo'shish
            ImageData imageData = ImageDataFactory.create(imageBytes);
            Image pdfImage = new Image(imageData);

            // Rasmning o'lchamlarini moslashtirish
            float aspectRatio = image.getWidth() / (float) image.getHeight(); // Rasmning nisbatini hisoblash
            float scaledWidth = pdfWidth;
            float scaledHeight = scaledWidth / aspectRatio;

            // Agar balandlik sahifaning balandligidan katta bo'lsa, balandlikka moslashtirish
            if (scaledHeight > pdfHeight) {
                scaledHeight = pdfHeight;
                scaledWidth = scaledHeight * aspectRatio;
            }

            // Rasmni moslashtirish
            pdfImage.scaleToFit(scaledWidth, scaledHeight);

            // Rasmni hujjatga qo'shish
            document.add(pdfImage);

            // Hujjatni yopish
            document.close();

            // PDF ma'lumotini byte[] formatida olish
            return pdfOutputStream.toByteArray();
        }catch (Exception e){
            throw new DataNotFoundException("Certificate not found or pdf not created");
        }
    }

    public byte [] createPdfFromImageByIdOrCertificate(UUID id, Certificate certificate)  {
        try {
            if (certificate == null){
                certificate = findCertificateByIdOrThrow(id);
            }

            BufferedImage image = addTextToImage(certificate);
            image = qrCodeService.addQrCodeToImage(String.format("https://t.me/uychi_it_school_bot?start=%s", certificate.getId()),image);

            // Rasmdan byte massivini olish
            ByteArrayOutputStream imageOutputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", imageOutputStream);
            byte[] imageBytes = imageOutputStream.toByteArray();

            // PDF faylni vaqtinchalik oqimda yaratish
            ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(pdfOutputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);

            // Sahifa formatini o'rnatish (A4, kerakli formatda)
            pdfDoc.setDefaultPageSize(PageSize.A4.rotate()); // Yoki `PageSize.A4` agar portret bo'lsa

            // Dokument yaratish
            Document document = new Document(pdfDoc);

            // PDF sahifasining o'lchamlarini olish
            float pdfWidth = pdfDoc.getDefaultPageSize().getWidth();
            float pdfHeight = pdfDoc.getDefaultPageSize().getHeight();

            // Rasmni PDF sahifasiga qo'shish
            ImageData imageData = ImageDataFactory.create(imageBytes);
            Image pdfImage = new Image(imageData);

            // Rasmning o'lchamlarini moslashtirish
            float aspectRatio = image.getWidth() / (float) image.getHeight(); // Rasmning nisbatini hisoblash
            float scaledWidth = pdfWidth;
            float scaledHeight = scaledWidth / aspectRatio;

            // Agar balandlik sahifaning balandligidan katta bo'lsa, balandlikka moslashtirish
            if (scaledHeight > pdfHeight) {
                scaledHeight = pdfHeight;
                scaledWidth = scaledHeight * aspectRatio;
            }

            // Rasmni moslashtirish
            pdfImage.scaleToFit(scaledWidth, scaledHeight);

            // Rasmni hujjatga qo'shish
            document.add(pdfImage);

            // Hujjatni yopish
            document.close();

            // PDF ma'lumotini byte[] formatida olish
            return pdfOutputStream.toByteArray();
        }catch (Exception e){
            throw new DataNotFoundException("Certificate not found or pdf not created");
        }
    }

    public BufferedImage addTextToImage(Certificate certificate) throws IOException {
        // Rasmdan BufferedImage yaratish
        Student student = certificate.getStudent();
        String os = System.getProperty("os.name").toLowerCase();
        String imagePath = "/home/javlon/IdeaProjects/system/data/images/c1.png";

        if (os.contains("win")){
            imagePath = "D:\\system\\data\\images\\c1.png";
        }

        BufferedImage image = ImageIO.read(new File(imagePath));

        // Graphics2D yordamida yozuv qo'shish
        String fontPath = "/home/javlon/IdeaProjects/system/data/fonts/Montserrat-Medium.ttf";
        if (os.contains("win")) {
            fontPath = "D:\\system\\data\\fonts\\Montserrat-Medium.ttf";
            System.out.println("windows");
        }

        Font customFont = new Font("Arial", Font.BOLD, 50);
        Font customFont2 = new Font("Arial", Font.BOLD, 50);

        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath));
            customFont = customFont.deriveFont(60f); // Font o'lchamini belgilash

            customFont2 = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath));
            customFont2 = customFont.deriveFont(30f); // Font o'lchamini belgilash
        }catch (Exception e){
            e.printStackTrace();
        }

        Graphics2D graphics = image.createGraphics();
//        graphics.setFont(new Font("Arial", Font.BOLD, 50));
        graphics.setFont(customFont);
        graphics.setColor(Color.BLACK);
        graphics.drawString(String.format("%s %s", student.getLastName().toUpperCase(), student.getFirstName().toUpperCase()), 100, 750); // X va Y koordinatalari

        String date = certificate.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        String serialAndNumber = String.format("%s %s", certificate.getSerial(),certificate.getNumber());
        Graphics2D graphics2 = image.createGraphics();
        graphics2.setFont(customFont2);
        graphics2.setColor(Color.BLACK);
        graphics2.drawString(String.format("%s", date), 1430, 1175);
        graphics2.drawString(String.format("%s", serialAndNumber), 1430, 1065);

        graphics.dispose();
        return image;
    }
}
