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
        return saveFile(file, "uploads"); // 기본 uploads 폴더로 리다이렉트
    }

    public String saveFile(MultipartFile file, String folder) {
        try {
            String uploadDir = fileConfig.getUploadDir() + "/" + folder;
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File dest = new File(dir, fileName);
            file.transferTo(dest);

            // 브라우저에서 접근 가능한 경로 반환
            return "/img/" + folder + "/" + fileName;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("파일 업로드 실패", e);
        }
    }
}
