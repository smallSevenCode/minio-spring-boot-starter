package org.example.minio.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 工具类
 *
 * @author 苦瓜不苦
 * @date 2022/9/7 21:19
 **/
public class MinioUtil {


    /**
     * 转换成文件名称
     *
     * @param objectName 对象名称
     * @return 文明名称
     */
    public static String fileName(String objectName) {
        return FileUtil.getName(objectName);
    }


    /**
     * 格式化文件名
     *
     * @param fileName 文件名(包括后缀)
     * @return 对象名
     */
    public static String objectName(String fileName) {
        return StrUtil.strBuilder()
                .append(DateUtil.format(DateUtil.date(), "yyyyMM"))
                .append("/")
                .append(FileUtil.mainName(fileName))
                .append("-")
                .append(IdUtil.objectId())
                .append(".")
                .append(FileUtil.extName(fileName))
                .toString();
    }

    /**
     * 永久访问链接
     *
     * @param bucketName 桶
     * @param objectName 对象名称
     * @return 访问链接
     */
    public static String objectUrl(String bucketName, String objectName) {
        return StrUtil.strBuilder()
                .append(bucketName)
                .append("/")
                .append(objectName)
                .toString();
    }


    /**
     * 生成文件路径
     *
     * @param fileDirectory 文件目录
     * @param objectName    对象名称
     * @return 文件路径
     */
    public static String filePath(String fileDirectory, String objectName) {
        String filePath = StrUtil.strBuilder()
                .append(fileDirectory)
                .append("/")
                .append(FileUtil.getName(objectName))
                .toString();
        // 修复文件路径
        return FileUtil.normalize(filePath);
    }


}
