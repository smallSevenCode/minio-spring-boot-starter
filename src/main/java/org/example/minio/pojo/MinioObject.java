package org.example.minio.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.InputStream;
import java.io.Serializable;

/**
 * @author 苦瓜不苦
 * @date 2022/9/7 21:06
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MinioObject implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 类型 0.上传 1.下载
     */
    private Integer fileType;
    /**
     * 标签
     */
    private String etag;
    /**
     * 桶
     */
    private String bucketName;
    /**
     * 对象名称
     */
    private String objectName;
    /**
     * 文件类型
     */
    private String contentType = "application/octet-stream";
    /**
     * 访问链接
     */
    private String objectUrl;
    /**
     * 文件名称
     */
    private String fileName;
    /**
     * 文件大小
     */
    private String fileSize;
    /**
     * 文件路径
     */
    private String filePath;
    /**
     * 输入流
     */
    private InputStream inputStream;
    /**
     * 耗时
     */
    private String timeInterval;

}
