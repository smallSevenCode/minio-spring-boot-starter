package org.example.minio.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

/**
 * Minio客户端配置
 *
 * @author zp
 * @date 2022/9/7 14:15
 */
@Data
@ConfigurationProperties(
        prefix = "minio"
)
public class MinioProperties implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 是否开启
     */
    private Boolean enable;
    /**
     * URL地址
     */
    private String endpoint;
    /**
     * 用户名
     */
    private String accessKey;
    /**
     * 密码
     */
    private String secretKey;
    /**
     * 桶对象
     */
    private String bucketName;

}
