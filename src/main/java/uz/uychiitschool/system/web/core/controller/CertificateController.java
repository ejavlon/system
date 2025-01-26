package uz.uychiitschool.system.web.core.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uychiitschool.system.web.base.dto.ResponseApi;
import uz.uychiitschool.system.web.core.dto.CertificateDto;
import uz.uychiitschool.system.web.core.entity.Certificate;
import uz.uychiitschool.system.web.core.service.CertificateService;

import java.util.UUID;

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

    @GetMapping(value = "/getCertificateWithPdf/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> getCertificateWithPdf(@PathVariable  UUID id) {
        try {
            byte[] resource = service.createPdfFromImageByIdOrCertificate(id, null);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "certificate.pdf"); // Yuklash uchun

            return new ResponseEntity<>(resource, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Xatolik: " + e.getMessage());
        }
    }



    @GetMapping
    public ResponseEntity<?> getAllCertificates(@RequestParam(required = false) Integer page,
                                           @RequestParam(required = false) Integer size) {
        ResponseApi<Page<Certificate>> responseApi = service.getAllCertificates(page == null ? 0 : (page - 1), size == null ? 10 : size);
        return ResponseEntity.ok(responseApi);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCertificateById(@PathVariable UUID id) {
        ResponseApi<Certificate> responseApi = service.getCertificateById(id);
        return ResponseEntity.status(responseApi.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(responseApi);
    }

    @PostMapping
    public ResponseEntity<?> addCertificate(@RequestBody CertificateDto certificateDto) {
        ResponseApi<Certificate> responseApi = service.create(certificateDto);
        return ResponseEntity.status(responseApi.isSuccess() ? HttpStatus.CREATED : HttpStatus.INTERNAL_SERVER_ERROR).body(responseApi);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCertificate(@PathVariable UUID id, @RequestBody CertificateDto certificateDto) {
        ResponseApi<Certificate> responseApi = service.update(id, certificateDto);
        return ResponseEntity.status(responseApi.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(responseApi);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCertificate(@PathVariable UUID id) {
        ResponseApi<Certificate> responseApi = service.deleteCertificateById(id);
        return ResponseEntity.status(responseApi.isSuccess() ? HttpStatus.NO_CONTENT : HttpStatus.NOT_FOUND).body(responseApi);
    }
}
