package com.j2ee.MovieManageSystem.controller;

import com.j2ee.MovieManageSystem.common.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @PostMapping("/poster")
    public Result<String> uploadPoster(@RequestParam("file") MultipartFile file) {
        try {
            String originalName = file.getOriginalFilename();
            String ext = "";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID() + ext;
            Path uploadDir = Path.of("./uploads/posters");
            if (!Files.exists(uploadDir)) Files.createDirectories(uploadDir);
            file.transferTo(uploadDir.resolve(fileName).toFile());
            return Result.ok("上传成功", "/uploads/posters/" + fileName);
        } catch (Exception e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }
}
