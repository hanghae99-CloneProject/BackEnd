package com.move.Bunjang.service;

import com.move.Bunjang.domain.Media;
import com.move.Bunjang.domain.Post;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageUploadInter {

    Media fileUpload(MultipartFile multipartFile);
    void deleteFile(String fileName);
    String createFileName(String fileName);
    String getFileExtension(String fileName);

}
