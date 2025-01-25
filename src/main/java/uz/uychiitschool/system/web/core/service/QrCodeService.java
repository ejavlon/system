package uz.uychiitschool.system.web.core.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

@Service
public class QrCodeService {

    public BufferedImage addQrCodeToImage(String text, BufferedImage image) throws Exception {
        // QR kodni yaratish
        final int qrSize = 200;  // QR kodning o'lchami
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
        int x = 850;  // QR kodni joylashtirish uchun x koordinatasi
        int y = 1000;  // QR kodni joylashtirish uchun y koordinatasi
        graphics.drawImage(qrImage, x, y, null);
        graphics.dispose();

        return image;
    }
}
