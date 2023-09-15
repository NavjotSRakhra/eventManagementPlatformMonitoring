package io.github.navjotsrakhra.internalsecureproxy;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
public class keepAlive {
    @Scheduled(timeUnit = TimeUnit.MINUTES, fixedDelay = 10)
    public void sendRequest() {
        var url = System.getenv("url_to_keep_alive");

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(RequestConfig.DEFAULT).build()) {
            HttpGet get = new HttpGet(url);
            var response = httpClient.execute(get);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
