package com.livo.project.admin.service;

import com.livo.project.config.FileConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class FileService {
    private final FileConfig fileConfig;

    public String saveFile(MultipartFile file) {
        try {
            String uploadDir = fileConfig.getUploadDir();

            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            // 고유 파일명
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File dest = new File(uploadDir, fileName);

            file.transferTo(dest);

            // static 기준으로 접근 가능한 URL 반환
            return "/img/uploads/" + fileName;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("파일 업로드 실패", e);
        }
    }
}
