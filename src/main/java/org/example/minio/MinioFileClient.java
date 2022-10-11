package org.example.minio;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.io.FileUtil;
import io.minio.*;
import org.example.minio.pojo.MinioObject;
import org.example.minio.properties.MinioProperties;
import org.example.minio.utils.MinioUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;

/**
 * @author 苦瓜不苦
 * @date 2022/9/7 21:10
 **/
@Configuration
@EnableConfigurationProperties(MinioProperties.class)
@ConditionalOnProperty(prefix = "minio", name = "enable", havingValue = "true")
public class MinioFileClient {

    private final MinioProperties minioProperties;
    private final MinioClient minioClient;

    public MinioFileClient(MinioProperties minioProperties) {
        this.minioProperties = minioProperties;
        this.minioClient = MinioClient.builder()
                .endpoint(minioProperties.getEndpoint())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();
    }

    /**
     * 判断桶是否存在,否则创建桶
     *
     * @param bucketName 桶
     */
    public void makeBucket(String bucketName) {
        try {
            if (!minioClient.bucketExists(BucketExistsArgs
                    .builder()
                    .bucket(bucketName)
                    .build())) {
                minioClient.makeBucket(MakeBucketArgs
                        .builder()
                        .bucket(bucketName)
                        .build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 上传文件
     *
     * @param bucketName  桶
     * @param inputStream 输入流
     * @param fileName    文件名(包括后缀)
     * @return
     */
    public MinioObject upload(String bucketName, InputStream inputStream, String fileName) {
        try {
            TimeInterval timer = DateUtil.timer();
            // 创建桶
            makeBucket(bucketName);
            // 上传
            ObjectWriteResponse response = minioClient.putObject(PutObjectArgs
                    .builder()
                    .bucket(bucketName)
                    .stream(inputStream, inputStream.available(), -1)
                    .object(MinioUtil.objectName(fileName))
                    .build());
            return MinioObject.builder()
                    .fileType(0)
                    .etag(response.etag())
                    .bucketName(response.bucket())
                    .objectName(response.object())
                    .objectUrl(MinioUtil.objectUrl(bucketName, response.object()))
                    .fileName(MinioUtil.fileName(response.object()))
                    .fileSize(FileUtil.readableFileSize(inputStream.available()))
                    .timeInterval(timer.intervalPretty())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new RuntimeException("上传文件失败");
    }

    /**
     * 上传文件
     *
     * @param inputStream 输入流
     * @param fileName    文件名(包括后缀)
     * @return
     */
    public MinioObject upload(InputStream inputStream, String fileName) {
        return upload(minioProperties.getBucketName(), inputStream, fileName);
    }

    /**
     * 上传文件
     *
     * @param bucketName 桶
     * @param file       文件对象
     * @return
     */
    public MinioObject upload(String bucketName, File file) {
        try {
            TimeInterval timer = DateUtil.timer();
            // 文件路径
            String filePath = file.getPath();
            // 创建桶
            makeBucket(bucketName);
            // 文件类型
            String contentType = FileUtil.getMimeType(filePath);
            // 上传
            ObjectWriteResponse response = minioClient.uploadObject(UploadObjectArgs
                    .builder()
                    .bucket(bucketName)
                    .filename(filePath)
                    .object(MinioUtil.objectName(filePath))
                    .contentType(contentType)
                    .build());
            return MinioObject.builder()
                    .fileType(0)
                    .etag(response.etag())
                    .bucketName(response.bucket())
                    .objectName(response.object())
                    .contentType(contentType)
                    .objectUrl(MinioUtil.objectUrl(bucketName, response.object()))
                    .fileName(MinioUtil.fileName(response.object()))
                    .fileSize(FileUtil.readableFileSize(file))
                    .timeInterval(timer.intervalPretty())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new RuntimeException("上传文件失败");
    }


    /**
     * 上传文件
     *
     * @param file 文件对象
     * @return
     */
    public MinioObject upload(File file) {
        return upload(minioProperties.getBucketName(), file);
    }


    /**
     * 上传文件
     *
     * @param filePath 文件路径
     * @return
     */
    public MinioObject upload(String filePath) {
        return upload(minioProperties.getBucketName(), FileUtil.file(filePath));
    }


    /**
     * 上传文件
     *
     * @param bucketName    桶
     * @param multipartFile 文件解析器
     * @return
     */
    public MinioObject upload(String bucketName, MultipartFile multipartFile) {
        try {
            TimeInterval timer = DateUtil.timer();
            // 创建桶
            makeBucket(bucketName);
            ObjectWriteResponse response = minioClient.putObject(PutObjectArgs
                    .builder()
                    .bucket(bucketName)
                    .stream(multipartFile.getInputStream(), multipartFile.getSize(), -1)
                    .object(MinioUtil.objectName(multipartFile.getOriginalFilename()))
                    .contentType(multipartFile.getContentType())
                    .build());
            return MinioObject.builder()
                    .fileType(0)
                    .etag(response.etag())
                    .bucketName(response.bucket())
                    .objectName(response.object())
                    .contentType(multipartFile.getContentType())
                    .objectUrl(MinioUtil.objectUrl(bucketName, response.object()))
                    .fileName(MinioUtil.fileName(response.object()))
                    .fileSize(FileUtil.readableFileSize(multipartFile.getSize()))
                    .timeInterval(timer.intervalPretty())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new RuntimeException("上传文件失败");
    }


    /**
     * 上传文件
     *
     * @param multipartFile 文件解析器
     * @return
     */
    public MinioObject upload(MultipartFile multipartFile) {
        return upload(minioProperties.getBucketName(), multipartFile);
    }

    /**
     * 文件信息
     *
     * @param bucketName 桶
     * @param objectName 对象名称
     * @return
     */
    public StatObjectResponse statObject(String bucketName, String objectName) {
        try {
            return minioClient.statObject(StatObjectArgs
                    .builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new RuntimeException("获取文件信息失败");
    }


    /**
     * 下载文件流
     *
     * @param bucketName 桶
     * @param objectName 对象名称
     * @return
     */
    public MinioObject downloadStream(String bucketName, String objectName) {
        try {
            TimeInterval timer = DateUtil.timer();
            // 文件信息
            StatObjectResponse response = statObject(bucketName, objectName);
            // 文件流
            InputStream inputStream = minioClient.getObject(GetObjectArgs
                    .builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
            return MinioObject
                    .builder()
                    .fileType(1)
                    .etag(response.etag())
                    .bucketName(bucketName)
                    .objectName(objectName)
                    .contentType(response.contentType())
                    .objectUrl(MinioUtil.objectUrl(bucketName, objectName))
                    .fileName(MinioUtil.fileName(objectName))
                    .fileSize(FileUtil.readableFileSize(response.size()))
                    .inputStream(inputStream)
                    .timeInterval(timer.intervalPretty())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new RuntimeException("下载文件流失败");
    }


    /**
     * 下载文件流
     *
     * @param objectName 对象名称
     * @return
     */
    public MinioObject downloadStream(String objectName) {
        return downloadStream(minioProperties.getBucketName(), objectName);
    }


    /**
     * 下载文件
     *
     * @param bucketName    桶
     * @param objectName    对象名称
     * @param fileDirectory 文件目录
     * @return
     */
    public MinioObject download(String bucketName, String objectName, String fileDirectory) {
        try {
            TimeInterval timer = DateUtil.timer();
            String filePath = MinioUtil.filePath(fileDirectory, objectName);
            // 文件信息
            StatObjectResponse response = statObject(bucketName, objectName);
            minioClient.downloadObject(DownloadObjectArgs
                    .builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .overwrite(true)
                    .filename(filePath)
                    .build());
            return MinioObject
                    .builder()
                    .fileType(1)
                    .etag(response.etag())
                    .bucketName(bucketName)
                    .objectName(objectName)
                    .contentType(response.contentType())
                    .objectUrl(MinioUtil.objectUrl(bucketName, objectName))
                    .fileName(MinioUtil.fileName(objectName))
                    .fileSize(FileUtil.readableFileSize(response.size()))
                    .filePath(filePath)
                    .timeInterval(timer.intervalPretty())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new RuntimeException("下载文件失败");
    }


    /**
     * 下载文件
     *
     * @param objectName    对象名称
     * @param fileDirectory 文件目录
     * @return
     */
    public MinioObject download(String objectName, String fileDirectory) {
        return download(minioProperties.getBucketName(), objectName, fileDirectory);
    }


}
