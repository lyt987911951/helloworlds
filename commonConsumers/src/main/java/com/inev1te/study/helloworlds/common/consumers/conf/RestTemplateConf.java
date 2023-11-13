package com.inev1te.study.helloworlds.common.consumers.conf;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
public class RestTemplateConf {

    @Bean
    public RestTemplate restTemplate() throws FileNotFoundException {
        //设置连接超时时间
        RequestConfig.Builder builder = RequestConfig.custom().setConnectTimeout(5 * 1000)
                .setConnectionRequestTimeout(5 * 1000)
                .setSocketTimeout(10 * 1000);
        CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(builder.build()).setRedirectStrategy(new LaxRedirectStrategy()).build();
        ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
        interceptors.add(new ClientHttpRequestInterceptor() {
            @Override
            public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
                ClientHttpResponse execute;
                try {
                    execute = execution.execute(request, body);
                } catch (IOException e) {
                    // 特殊处理超时异常
                    final Exception ex = e;
                    if (e instanceof SocketTimeoutException) {
                        // 返回自定义异常
                        // throw new AppException(AppErrorEnum.REQUEST_TIMEOUT);
                        execute = new AbstractClientHttpResponse() {
                            @Override
                            public int getRawStatusCode() throws IOException {
                                return HttpStatus.SC_REQUEST_TIMEOUT;
                            }

                            @Override
                            public String getStatusText() throws IOException {
                                return "request time out";
                            }

                            @Override
                            public void close() {

                            }

                            @Override
                            public InputStream getBody() throws IOException {
                                return new ByteArrayInputStream(ex.getMessage().getBytes());
                            }

                            @Override
                            public HttpHeaders getHeaders() {
                                return new HttpHeaders();
                            }
                        };
                    }else {
                        execute = new AbstractClientHttpResponse() {
                            @Override
                            public int getRawStatusCode() throws IOException {
                                return 500;
                            }

                            @Override
                            public String getStatusText() throws IOException {
                                return "server internal error";
                            }

                            @Override
                            public void close() {

                            }

                            @Override
                            public InputStream getBody() throws IOException {
                                return new ByteArrayInputStream(ex.getMessage().getBytes() );
                            }

                            @Override
                            public HttpHeaders getHeaders() {
                                return new HttpHeaders();
                            }
                        };
                    }
                }
                return execute;
            }
        });
        restTemplate.setInterceptors(interceptors);
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return false;
            }

            @Override
            public void handleError(ClientHttpResponse response) throws IOException {

            }
        });
        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        for (HttpMessageConverter<?> messageConverter : messageConverters) {
            if (messageConverter instanceof MappingJackson2HttpMessageConverter) {
                MappingJackson2HttpMessageConverter converter = (MappingJackson2HttpMessageConverter) messageConverter;
                converter.setDefaultCharset(StandardCharsets.UTF_8);
            }
            if (messageConverter instanceof StringHttpMessageConverter) {
                StringHttpMessageConverter converter = (StringHttpMessageConverter) messageConverter;
                converter.setDefaultCharset(StandardCharsets.UTF_8);
            }
        }
        return restTemplate;
    }
}
