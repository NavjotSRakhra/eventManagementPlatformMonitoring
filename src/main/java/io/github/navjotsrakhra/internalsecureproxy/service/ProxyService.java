package io.github.navjotsrakhra.internalsecureproxy.service;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProxyService {

    private static String cookie = null;
    private static String username = null, password = null;
    @Value("${url}")
    private String url;
    @Value("${prometheus.path}")
    private String prometheusPath;
    @Value("${login.processing.path}")
    private String loginProcessingPath;

    public String getPrometheusFromEnvVarWithUsernameAndPassword() {

        if (username == null || password == null) fetchUsernamePassword();
        if (cookie == null) refreshCookie();


        try (CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(RequestConfig.DEFAULT).build()) {
            HttpGet get = new HttpGet(url + prometheusPath);
            get.addHeader("Cookie", cookie);
            var response = httpClient.execute(get);

            String responseString = new String(response.getEntity().getContent().readAllBytes());

            if (response.getStatusLine().getStatusCode() != 200 || responseString.contains("<!DOCTYPE html>")) {
                refreshCookie();
                return getPrometheusFromEnvVarWithUsernameAndPassword();
            }

            return responseString;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void fetchUsernamePassword() {
        username = System.getenv("username");
        password = System.getenv("password");
    }

    private void refreshCookie() {
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(RequestConfig.DEFAULT).build()) {
            HttpPost post = new HttpPost(url + loginProcessingPath);

            List<NameValuePair> formParameters = new ArrayList<>();
            formParameters.add(new BasicNameValuePair("username", username));
            formParameters.add(new BasicNameValuePair("password", password));

            post.setEntity(new UrlEncodedFormEntity(formParameters));

            HttpResponse response = httpClient.execute(post);

            var temp = response.getFirstHeader("Set-cookie").getElements()[0];
            cookie = temp.getName() + "=" + temp.getValue();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
