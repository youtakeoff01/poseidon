package com.hand.bdss.dsmp.component.azkaban;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;
import com.hand.bdss.dsmp.config.SystemConfig;
import com.hand.bdss.dsmp.model.ETLJobConfig;

/**
 * Created by xc on 2017/4/28.
 */

@SuppressWarnings("deprecation")
public class AzkabanAPI {

    private static HttpClient httpClient = new DefaultHttpClient();
    private static String responseContent = null;
    private static String sessionId = null;


    private static String baseUrl = SystemConfig.AZKABAN_URL;
    private static String url = baseUrl+"/manager";
    private static String createAction = "/manager?action=create";
    private static String loginAction = "/?action=login";
    private static String deleteAction = "/manager?delete=true";
    private static String uploadAction = "/manager?ajax=upload";


    static {
        X509TrustManager xtm = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        X509HostnameVerifier hostnameVerifier = new AllowAllHostnameVerifier();
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new TrustManager[]{xtm}, null);
            SSLSocketFactory socketFactory = new SSLSocketFactory(ctx);
            socketFactory.setHostnameVerifier(hostnameVerifier);
            httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", socketFactory, 443));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 登录验证
     *
     * @param params POST访问的参数Map对象
     * @return 返回响应值
     */

    public static String loginAndGetSessionId(Map<String, String> params) throws Exception {

        HttpPost httpPost = new HttpPost(baseUrl + loginAction);
        List<NameValuePair> formParams = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        httpPost.setEntity(new UrlEncodedFormEntity(formParams, "UTF-8"));
        httpClient.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS,true);
        HttpResponse response = httpClient.execute(httpPost);
        
        HttpEntity entity = response.getEntity();
        if (response.getStatusLine().getStatusCode() == 200) {
            responseContent = EntityUtils.toString(entity, "UTF-8");
            JSONObject jsonObject = JSONObject.parseObject(responseContent);
            if (jsonObject.getString("status").equals("success")) {
                sessionId = jsonObject.getString("session.id");
            }
        }
        return sessionId;
    }

    /**
     * 创建一个调度项目
     *
     * @param etlJobConfig
     * @throws Exception
     */
    public static void createProject(ETLJobConfig etlJobConfig) throws Exception {

        HttpPost httpPost = new HttpPost(baseUrl + createAction);
        List<NameValuePair> formParams = new ArrayList<>();
        String projectName = etlJobConfig.getJobName()+"_0_sqoop";
        String projectDesc = etlJobConfig.getJobName() + etlJobConfig.getJobType();
        formParams.add(new BasicNameValuePair("name", projectName));
        formParams.add(new BasicNameValuePair("description", projectDesc));

        httpPost.setEntity(new UrlEncodedFormEntity(formParams, "UTF-8"));
        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        if (response.getStatusLine().getStatusCode() == 200) {
            responseContent = EntityUtils.toString(entity);
        }
        System.out.println(responseContent);
    }

    /**
     * get请求删除一个project
     *
     * @return
     * @throws Exception
     */
    public static void deleteProject(String projectName) throws Exception {
        String url = baseUrl + deleteAction + "&session.id=" + sessionId + "&project=" + projectName;
        HttpGet httpGet = new HttpGet(url);
        HttpResponse response = httpClient.execute(httpGet);
    }

    /**
     * 将存放在sourceFilePath目录下的源文件,打包成fileName名称的ZIP文件,并存放到zipFilePath。
     * <p>
     * //     * @param sourceFilePath 待压缩的文件路径
     * //     * @param zipFilePath    压缩后存放路径
     * //     * @param fileName       压缩后文件的名称
     *
     * @return flag
     */
    public static boolean fileToZip(ETLJobConfig etlJobConfig,String sqoopPath,String zipPath) {
        String sourceFilePath = sqoopPath+etlJobConfig.getSyncSource()+".sh";
//        String jobName = etlJobConfig.getSyncSource();
        String path = sqoopPath + etlJobConfig.getSyncSource() + ".job";

        String zipFilePath = zipPath;
        String fileName = etlJobConfig.getJobName();
        boolean flag = false;
        File sourceFile = new File(sourceFilePath);
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        ZipOutputStream zos = null;

        if (sourceFile.exists() == false) {
            System.out.println(">>>>>> 待压缩的文件目录：" + sourceFilePath + " 不存在. <<<<<<");
        } else {
            try {
                File zipFile = new File(zipFilePath + "/" + fileName + ".zip");
                if (zipFile.exists()) {
                    System.out.println(">>>>>> " + zipFilePath + " 目录下存在名字为：" + fileName + ".zip" + " 打包文件. <<<<<<");
                } else {
//                    File[] sourceFiles = sourceFile.listFiles();
//                    sourceFiles[0].getName()
                    File file1 =  new File(sourceFilePath);
                    File file2  = new File(path);
                    File[] sourceFiles = new File[]{file1,file2};
                    if (null == sourceFiles || sourceFiles.length < 1) {
                        System.out.println(">>>>>> 待压缩的文件目录：" + sourceFilePath + " 里面不存在文件,无需压缩. <<<<<<");
                    } else {
                        fos = new FileOutputStream(zipFile);
                        zos = new ZipOutputStream(new BufferedOutputStream(fos));
                        byte[] bufs = new byte[1024 * 10];
                        for (int i = 0; i < sourceFiles.length; i++) {
                            // 创建ZIP实体,并添加进压缩包
                            ZipEntry zipEntry = new ZipEntry(sourceFiles[i].getName());
                            zos.putNextEntry(zipEntry);
                            // 读取待压缩的文件并写进压缩包里
                            fis = new FileInputStream(sourceFiles[i]);
                            bis = new BufferedInputStream(fis, 1024 * 10);
                            int read = 0;
                            while ((read = bis.read(bufs, 0, 1024 * 10)) != -1) {
                                zos.write(bufs, 0, read);
                            }
                        }
                        flag = true;
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } finally {
                // 关闭流
                try {
                    if (null != bis) bis.close();
                    if (null != zos) zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
        return flag;
    }

    /**
     * 上传zip包到项目中
     *
     * @param etlJobConfig
     * @throws Exception
     */
    public static void uploadJar(ETLJobConfig etlJobConfig,String zipPath) throws Exception {
        String projectName = etlJobConfig.getJobName();
        zipPath = zipPath + projectName + ".zip";

        HttpPost httpPost = new HttpPost(url);
        InputStream inputStream = new FileInputStream(zipPath);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addBinaryBody("file", inputStream, ContentType.create("application/zip"), projectName);
        builder.addTextBody("project", projectName, ContentType.create("type/plain"));
        builder.addTextBody("session.id", sessionId, ContentType.create("type/plain"));
        builder.addTextBody("ajax", "upload", ContentType.create("type/plain"));
        HttpEntity entity = builder.build();
        httpPost.setEntity(entity);
        HttpResponse response = httpClient.execute(httpPost);
        int statusCode = response.getStatusLine().getStatusCode();
        System.out.println(statusCode+"----------------------");
        if (statusCode == 200) {
            responseContent = EntityUtils.toString(response.getEntity());
        }
        System.out.println(responseContent);
    }

    /**
     * 创建一个作业
     *
     * @param etlJobConfig
     * @param absuPath 路径
     * @throws Exception
     */
    public static void createJob(ETLJobConfig etlJobConfig,String absuPath) throws Exception {
        String jobName = etlJobConfig.getSyncSource();
        String path = absuPath + jobName + ".job";
        FileOutputStream fileInputStream = new FileOutputStream(new File(path));
        String job = "type=command" + "\n" + "command=sh " + jobName + ".sh";
        fileInputStream.write(job.getBytes());
    }

    /**
     * 执行工作流
     *
     * @throws Exception
     */
    public static void executeFlow(ETLJobConfig etlJobConfig) throws Exception {
        String projectName = etlJobConfig.getJobName();
        String flowName = etlJobConfig.getSyncSource();
        String url = baseUrl + "/executor?ajax=executeFlow" + "&session.id=" + sessionId + "&project=" + projectName + "&flow=" + flowName;
        HttpGet httpGet = new HttpGet(url);
        HttpResponse response = httpClient.execute(httpGet);
        if (response.getStatusLine().getStatusCode() == 200) {
            responseContent = EntityUtils.toString(response.getEntity());
        }
        System.out.println(responseContent);
    }
}

