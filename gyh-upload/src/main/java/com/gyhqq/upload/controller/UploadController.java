package com.gyhqq.upload.controller;

import com.gyhqq.upload.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
public class UploadController {

    @Autowired
    private UploadService uploadService;

    /**
     * 本地上传图片功能
     * @param file
     * @return
     */
    @PostMapping("/image")
    public ResponseEntity<String> image(@RequestParam(name = "file") MultipartFile file) {
        //返回201，并且携带url路径
        return ResponseEntity.status(HttpStatus.CREATED).body(uploadService.upload(file));
    }

    /**
     * 阿里云OSS上传图片:获取阿里云签名
     * @return
     */
    @GetMapping("/signature")
    public ResponseEntity<Map<String,Object>> signature(){
        return ResponseEntity.status(HttpStatus.CREATED).body(uploadService.getSignature());
    }

}
