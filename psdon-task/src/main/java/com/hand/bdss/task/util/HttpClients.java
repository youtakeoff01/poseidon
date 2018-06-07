package com.hand.bdss.task.util;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

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
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by hand on 2017/8/24.
 */
public class HttpClients {
    private static HttpClient client = null;

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
            client = new DefaultHttpClient();
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new TrustManager[]{xtm}, null);
            SSLSocketFactory socketFactory = new SSLSocketFactory(ctx);
            socketFactory.setHostnameVerifier(hostnameVerifier);
            client.getConnectionManager().getSchemeRegistry().register(new Scheme("https", socketFactory, 443));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HttpClient getHttpClient() {
        return client;
    }

    /**
     * 模拟GET请求
     *
     * @param url
     * @return
     */
	public static JSONObject sentGet(String url) throws IOException {
        try {
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = client.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                String result = EntityUtils.toString(response.getEntity(), "UTF-8");
                return JSONObject.parseObject(result);
            }
        } catch (IOException e) {
            throw new IOException(e);
        }
        return null;
    }

    /**
     * 模拟GET请求
     *
     * @param url
     * @return
     */
    public static JSONObject sendPost(String url, List<NameValuePair> formParams, MultipartEntityBuilder builder) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        try {
            if (null != formParams) {
                httpPost.setEntity(new UrlEncodedFormEntity(formParams, "UTF-8"));
            }
            if (null != builder) {
                HttpEntity entity = builder.build();
                httpPost.setEntity(entity);
            }
            HttpResponse response = client.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == 200) {
                String result = EntityUtils.toString(response.getEntity(), "UTF-8");
                return JSONObject.parseObject(result);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException(e);
        } finally {
            httpPost.releaseConnection();
        }
        return null;
    }
}
