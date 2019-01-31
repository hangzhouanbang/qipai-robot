package com.anbang.fake.utils;

/**
 * @Author: 吴硕涵
 * @Date: 2019/1/29 5:10 PM
 * @Version 1.0
 */

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;

public class QiniuUtil {
    private static String accessKey = "qQj7mRKyvE7dOOjObMC8W58i6Yn3penfr7-_fg4d";
    private static String secretKey = "9f70kmAddF1maP1U0jy0vRNAhwWNv_huR1xDSH_s";
    private static String bucket = "anbang";
    private static Auth auth = Auth.create(accessKey, secretKey);
    private static String uptoken = auth.uploadToken(bucket);
    // 构造一个带指定Zone对象的配置类
    private static Configuration config = new Configuration(Zone.zone0());
    private static UploadManager uploadManager = new UploadManager(config);
    private static BucketManager bucketManager = new BucketManager(auth, config);
    ////默认不指定key的情况下，以文件内容的hash值作为文件名
    private static String key = null;

    public static void deleteImage(String fileName) throws QiniuException {
        bucketManager.delete(bucket, fileName);
    }

    public static void upLoadImage(String localFilePath) throws QiniuException {
        try {
            Response response = uploadManager.put(localFilePath, key, uptoken);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            System.out.println(putRet.key);
            System.out.println(putRet.hash);
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
            }
        }
    }
}
