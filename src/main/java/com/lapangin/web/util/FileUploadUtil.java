package com.lapangin.web.util;

import java.io.File;
import java.io.IOException;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public class FileUploadUtil {

    public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File uploadFile = new File(dir, StringUtils.cleanPath(fileName));
        multipartFile.transferTo(uploadFile);
    }
}