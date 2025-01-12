package uz.uychiitschool.system.web.core.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.uychiitschool.system.web.core.entity.Certificate;
import uz.uychiitschool.system.web.core.repository.CertificateRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CertificateRepository repository;

    public Certificate createCertificate() {
        Certificate certificate = new Certificate();
        certificate.setId(UUID.randomUUID()); // Noyob UUID yaratish
        certificate.setSerial("SER-" + UUID.randomUUID().toString().substring(0, 8)); // Seriya yaratish
        certificate.setNumber("NUM-" + UUID.randomUUID().toString().substring(0, 8)); // Raqam yaratish

        return repository.save(certificate);
    }

    public Certificate getCertificate(UUID id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Certificate not found"));
    }

    public byte [] createPdfFromImage(String text) throws Exception {
        BufferedImage image = addTextToImage(text);
        image = addQrCodeToImage("https://ejavlon.uz",image);

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

    public BufferedImage addQrCodeToImage(String text, BufferedImage image) throws Exception {
        // QR kodni yaratish
        int qrSize = 150;  // QR kodning o'lchami
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, 1);  // QR kod chetini sozlash
        BitMatrix matrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, qrSize, qrSize, hints);

        // QR kodni BufferedImage formatida yaratish
        BufferedImage qrImage = new BufferedImage(qrSize, qrSize, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < qrSize; i++) {
            for (int j = 0; j < qrSize; j++) {
                qrImage.setRGB(i, j, matrix.get(i, j) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
            }
        }

        // QR kodni original rasmga joylashtirish
        Graphics2D graphics = image.createGraphics();
        int x = 250;  // QR kodni joylashtirish uchun x koordinatasi
        int y = 250;  // QR kodni joylashtirish uchun y koordinatasi
        graphics.drawImage(qrImage, x, y, null);
        graphics.dispose();

        return image;
    }
}
