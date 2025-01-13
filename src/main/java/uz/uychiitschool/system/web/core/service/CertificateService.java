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
import uz.uychiitschool.system.web.base.dto.ResponseApi;
import uz.uychiitschool.system.web.base.entity.User;
import uz.uychiitschool.system.web.base.exception.DataNotFoundException;
import uz.uychiitschool.system.web.base.service.UserService;
import uz.uychiitschool.system.web.core.dto.CertificateDto;
import uz.uychiitschool.system.web.core.entity.Certificate;
import uz.uychiitschool.system.web.core.entity.Course;
import uz.uychiitschool.system.web.core.repository.CertificateRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CertificateRepository repository;
    private final CourseService courseService;
    private final UserService userService;
    private final QrCodeService qrCodeService;

    public Certificate createCertificate() {
        Certificate certificate = new Certificate();
        certificate.setId(UUID.randomUUID()); // Noyob UUID yaratish
        certificate.setSerial("SER-" + UUID.randomUUID().toString().substring(0, 2)); // Seriya yaratish
        certificate.setNumber("NUM-" + UUID.randomUUID().toString().substring(0, 8)); // Raqam yaratish

        return repository.save(certificate);
    }

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

    public ResponseApi<Certificate> create(CertificateDto certificateDto){
        return null;
    }

    public Certificate createCertificate(CertificateDto certificateDto){
        if (certificateDto.getDate() != null && certificateDto.getDate().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Certificate date is after now");
        }

        LocalDateTime date = certificateDto.getDate() != null ? certificateDto.getDate() : LocalDateTime.now();

        Course course = courseService.findCourseByIdOrThrow(certificateDto.getCourseId());
        User teacher = userService.findUserByIdOrUsernameOrThrow(certificateDto.getTeacherId(), null);


        return null;
    }

    public Certificate createOrUpdateCertificate(CertificateDto certificateDto, Certificate exsistingCertificate) {
        if (exsistingCertificate == null){
            return createCertificate(certificateDto);
        }

        return null;
    }

    public Certificate findCertificateByIdOrThrow(UUID id) {
        return repository.findById(id).orElseThrow(() -> new DataNotFoundException("Certificate not found"));
    }

    public byte [] createPdfFromImage(String text) throws Exception {
        BufferedImage image = addTextToImage(text);
        image = qrCodeService.addQrCodeToImage("https://ejavlon.uz",image);

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
    }

    public BufferedImage addTextToImage(String text) throws IOException {
        // Rasmdan BufferedImage yaratish
        String pdfPath = "/home/javlon/IdeaProjects/system/data/images/c1.png";
        BufferedImage image = ImageIO.read(new File(pdfPath));

        // Graphics2D yordamida yozuv qo'shish
        Graphics2D graphics = image.createGraphics();
        graphics.setFont(new Font("Arial", Font.BOLD, 50));
        graphics.setColor(Color.BLACK);
        graphics.drawString(text, 50, 50); // X va Y koordinatalari

        graphics.dispose();
        return image;
    }
}
