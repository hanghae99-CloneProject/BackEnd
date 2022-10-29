package com.move.review.service;

import com.move.review.domain.Media;
import com.move.review.domain.Post;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageUploadInter {



    List<Media> fileUpload(List<MultipartFile> multipartFile, Post post);
    void deleteFile(String fileName);
    String createFileName(String fileName);
    String getFileExtension(String fileName);

}
