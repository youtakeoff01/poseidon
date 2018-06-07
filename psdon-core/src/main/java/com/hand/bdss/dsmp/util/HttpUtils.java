package com.hand.bdss.dsmp.util;

import java.io.IOException;

import com.hand.bdss.dsmp.config.SystemConfig;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class HttpUtils {

    private HttpClientBuilder httpClientBuilder;
    private CloseableHttpClient closeableHttpClient;

    private static String username;
    private static String password;

    static {
        username = SystemConfig.ATLAS_USERNAME;
        password = SystemConfig.ATLAS_PASSWORD;
    }

    public String doGet(String URI){
        if( closeableHttpClient == null ){
            //createCloseableHttpClient();
            createCloseableHttpClientWithBasicAuth();
        }
        String result = "";
        HttpResponse httpResponse;
        HttpEntity entity;
        HttpGet httpGet = new HttpGet(URI);
        try {
            httpResponse = closeableHttpClient.execute(httpGet);
            entity = httpResponse.getEntity();
            if( entity != null ){
                result = EntityUtils.toString(entity);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        finally {
//            closeHttpClient();
//        }

        return result;
    }


    public void closeHttpClient(){
        if( closeableHttpClient != null ){
            try {
                closeableHttpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void createCloseableHttpClient(){
        if( closeableHttpClient == null ){
            // 创建HttpClientBuilder
            httpClientBuilder = HttpClientBuilder.create();
            // HttpClient
            closeableHttpClient = httpClientBuilder.build();
        }
    }

    public void createCloseableHttpClientWithBasicAuth(){
        if( closeableHttpClient == null ){
            // 创建HttpClientBuilder
            httpClientBuilder = HttpClientBuilder.create();
            // 设置BasicAuth
            CredentialsProvider provider = new BasicCredentialsProvider();
            // Create the authentication scope
            AuthScope scope = new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM);
            // Create credential pair
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
            // Inject the credentials
            provider.setCredentials(scope, credentials);
            // Set the default credentials provider
            httpClientBuilder.setDefaultCredentialsProvider(provider);
            // HttpClient
            closeableHttpClient = httpClientBuilder.build();
        }
    }

    public static void main(String args[])  {
        String URI = "http://dasrv03.novalocal:21000/api/atlas/entities/d4e31f51-0304-487c-ab9e-bcc96dbd924d";

        HttpUtils httpClient = new HttpUtils();
        String result = httpClient.doGet(URI);
        System.out.println(result);
    }
}
