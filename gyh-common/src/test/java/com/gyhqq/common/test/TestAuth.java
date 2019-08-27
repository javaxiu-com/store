package com.gyhqq.common.test;

import com.gyhqq.common.auth.entity.Payload;
import com.gyhqq.common.auth.entity.UserInfo;
import com.gyhqq.common.auth.utils.JwtUtils;
import com.gyhqq.common.auth.utils.RsaUtils;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

public class TestAuth {
    //私钥的存放目录
    private String privateFilePath = "C:\\App\\Nginx\\nginx-1.16.0\\html\\rsa\\id_rsa";
    //公钥的存放目录
    private String publicFilePath = "C:\\App\\Nginx\\nginx-1.16.0\\html\\rsa\\id_rsa.pub";

    @Test
    public void testRSA() throws Exception {
        // 生成密钥对
        RsaUtils.generateKey(publicFilePath, privateFilePath, "hello", 2048);
        // 获取私钥
        PrivateKey privateKey = RsaUtils.getPrivateKey(privateFilePath);
        System.out.println("privateKey = " + privateKey);
        // 获取公钥
        PublicKey publicKey = RsaUtils.getPublicKey(publicFilePath);
        System.out.println("publicKey = " + publicKey);
    }

    /**
     * 测试代码一:
     * @throws Exception
     */
//    @Test
//    public void testJWT() throws Exception {
//        // 获取私钥
//        PrivateKey privateKey = RsaUtils.getPrivateKey(privateFilePath);
//        // 生成token
//        String token = JwtUtils.generateTokenExpireInMinutes(new UserInfo(1L, "Jack", "guest"), privateKey, 5);
//        System.out.println("token = " + token);
//
//        // 获取公钥
//        PublicKey publicKey = RsaUtils.getPublicKey(publicFilePath);
//        // 解析token
//        Payload<UserInfo> info = JwtUtils.getInfoFromToken(token, publicKey, UserInfo.class);
//
//        System.out.println("info.getExpiration() = " + info.getExpiration());
//        System.out.println("info.getUserInfo() = " + info.getUserInfo());
//        System.out.println("info.getId() = " + info.getId());
//    }
//}

    /**
     * 测试代码二:
     * @throws Exception
     */
    @Test
    public void testJWT() throws Exception {
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername("jack");
        userInfo.setId(1L);
        userInfo.setRole("admin");
        PrivateKey privateKey = RsaUtils.getPrivateKey(privateFilePath);
        //创建token
        String token = JwtUtils.generateTokenExpireInMinutes(userInfo, privateKey, 3);
        System.out.println(token);

        //测试恶意更改token
/*        String newToken = "eyJhbGciOiJSUzI1NiJ9.eyJ1c2VyIjoie1wiaWRcIjoxLFwidXNlcm5hbWVcIjpcImphY2tcIixcInJvbGVcIjpcImFkbWluXCJ9IiwianRpIjoiWlRFME4ySTBNV1l0TURKalppMDBOVFEzTFdFd00ySXRZell4TnpreFpHUmtZVEptIiwiZXhwIjoxNTY2OTAwMzE2fQ.yX0PFUzEJlI7ro3OPskoT_7S6K53w52lQfLoQT2OEWjkRZ1b8JGRzTgmR6MvfuNz2y6sr6aagWRROmE9_vx3GGbAzBGJwJn5m76debWJvXjs9ESYOu5GGmGVbBP-6dNz_kvRrcjSjfZl35nTrMLVlX_WFTRCoSOCco5o8mkPLAKMyUeVFNoFkLOKGHNUwdIiIn_odK1hTU80M3X7A_G0vsolGj9uyW4cyOKiejonnym4uZG8Cie_ijQxdwq7Fvr6ufgQub6uNyhDJgBDYbDbVW_HlLXyIyxgXiOyCAQDOo7bxvF2jQwvilY3x7nuXeH8wkM291g2ohIccQ39WJl7DQ\n" +
                "ZTE0N2I0MWYtMDJjZi00NTQ3LWEwM2ItYzYxNzkxZGRkYTJm";*/

        //解密
        PublicKey publicKey = RsaUtils.getPublicKey(publicFilePath);
        Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token, publicKey, UserInfo.class);
        System.out.println(payload.getId());
        System.out.println(payload.getExpiration());
        System.out.println(payload.getUserInfo());
    }
}
