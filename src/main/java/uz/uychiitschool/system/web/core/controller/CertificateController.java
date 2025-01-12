package uz.uychiitschool.system.web.core.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.uychiitschool.system.web.core.service.CertificateService;

@RestController
@RequestMapping("/api/v1/certificate")
@RequiredArgsConstructor
public class CertificateController {
    private final CertificateService service;

    @Operation(
            description = "PDF faylni yaratish va qaytarish",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "PDF fayl muvaffaqiyatli yaratildi",
                            content = @Content(mediaType = "application/pdf")
                    )
            }
    )
    @PostMapping(value = "/addTextAndConvertToPdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> addTextAndConvertToPdf(@RequestParam String text) {
        try {
            byte[] resource = service.createPdfFromImage(text);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "certificate.pdf"); // Yuklash uchun

            return new ResponseEntity<>(resource, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Xatolik: " + e.getMessage());
        }
    }

}
