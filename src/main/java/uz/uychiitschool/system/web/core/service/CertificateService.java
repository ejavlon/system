package uz.uychiitschool.system.web.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.uychiitschool.system.web.core.entity.Certificate;
import uz.uychiitschool.system.web.core.repository.CertificateRepository;

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
}
