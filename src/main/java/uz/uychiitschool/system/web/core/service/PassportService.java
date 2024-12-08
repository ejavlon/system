package uz.uychiitschool.system.web.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.uychiitschool.system.web.core.repository.PassportRepository;

@Service
@RequiredArgsConstructor
public class PassportService {
    private final PassportRepository repository;

}
