package com.gyhqq.upload.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.gyhqq.common.Exception.GyhException;
import com.gyhqq.common.enums.ExceptionEnum;
import com.gyhqq.upload.config.OSSProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class UploadService {
    // 支持的文件类型
    private static final List<String> suffixes = Arrays.asList("image/png", "image/jpeg", "image/bmp");

    /**
     * 本地上传图片
     * 返回图片地址
     *
     * @param file
     * @return
     */
    public String upload(MultipartFile file) {
        // 1、图片信息校验
        // 1)校验文件类型
        String type = file.getContentType();
        if (!suffixes.contains(type)) {
            throw new GyhException(ExceptionEnum.INVALID_FILE_TYPE);
        }

        // 2)校验图片内容
        try {
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if (bufferedImage == null) {
                throw new GyhException(ExceptionEnum.INVALID_FILE_TYPE);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new GyhException(ExceptionEnum.INVALID_FILE_TYPE);
        }

        //源文件名
        String originalFilename = file.getOriginalFilename();
        //后缀
        String suffix = originalFilename.substring(originalFilename.indexOf("."));
        //上传后的文件名
        String imageName = UUID.randomUUID().toString() + suffix;
        //上传的目录
        String dirPath = "C:\\App\\Nginx\\nginx-1.16.0\\html";
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdir();
        }
        try {
            file.transferTo(new File(dir, imageName));
        } catch (IOException e) {
            e.printStackTrace();
            throw new GyhException(ExceptionEnum.FILE_UPLOAD_ERROR);
        }
        //返回图片地址
        String imageUrl = "http://image.store.com/" + imageName;
        return imageUrl;
    }

    /**
     * 阿里云OSS上传图片
     *
     * @return
     */
    @Autowired
    private OSSProperties prop;

    @Autowired
    private OSS client;

    public Map<String, Object> getSignature() {
        try {
            long expireTime = prop.getExpireTime();
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, prop.getMaxFileSize());
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, prop.getDir());

            String postPolicy = client.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = client.calculatePostSignature(postPolicy);

            Map<String, Object> respMap = new LinkedHashMap<>();
            respMap.put("accessId", prop.getAccessKeyId());
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", prop.getDir());
            respMap.put("host", prop.getHost());
            respMap.put("expire", expireEndTime);
            return respMap;
        } catch (Exception e) {
            throw new GyhException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
    }
}
