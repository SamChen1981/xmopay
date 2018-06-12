package com.xmopay.common.utils.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.nio.conn.NoopIOSessionStrategy;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HttpClientUtils {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);

    private static PoolingHttpClientConnectionManager clientConnectionManager = null;
    private static CloseableHttpClient httpClient = null;
    private static RequestConfig config = RequestConfig.custom()
            .setConnectTimeout(20000) // 设置连接超时
            .setSocketTimeout(20000) // 设置读取超时
            .setConnectionRequestTimeout(1000) // 设置从连接池获取连接实例的超时
            .build();

    private final static Object syncLock = new Object();

    /**
     * 创建httpclient连接池并初始化
     */
    static {
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", SSLConnectionSocketFactory.getSocketFactory())
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .build();
        clientConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

        // 设置整个连接池最大连接数 根据自己的场景决定
        // 是路由的默认最大连接（该值默认为2），限制数量实际使用DefaultMaxPerRoute并非MaxTotal。
        clientConnectionManager.setMaxTotal(1000);

        // 设置过小无法支持大并发(ConnectionPoolTimeoutException: Timeout waiting for connection from pool)，路由是对maxTotal的细分。
        //（目前只有一个路由，因此让他等于最大值）

        // 此处解释下MaxtTotal和DefaultMaxPerRoute的区别：
        // 1、MaxtTotal是整个池子的大小；
        // 2、DefaultMaxPerRoute是根据连接到的主机对MaxTotal的一个细分；比如：
        // MaxtTotal=400 DefaultMaxPerRoute=200
        // 而我只连接到http://sishuok.com时，到这个主机的并发最多只有200；而不是400；
        // 而我连接到http://sishuok.com 和 http://qq.com时，到每个主机的并发最多只有200；即加起来是400（但不能超过400）；所以起作用的设置是DefaultMaxPerRoute。
        clientConnectionManager.setDefaultMaxPerRoute(500);
    }

    public static CloseableHttpClient getHttpClient() {
        if (httpClient == null) {
            synchronized (syncLock) {
                if (httpClient == null) {
                    httpClient = HttpClients.custom().setConnectionManager(clientConnectionManager).setDefaultRequestConfig(config).build();
                }
            }
        }
        return httpClient;
    }

    /**
     * 同步HTTP POST 请求
     * @param url
     * @param params
     * @return
     */
    public static String rsyncPost(String url, Map<String, String> params) {
        HttpPost httpPost = new HttpPost(url);

        CloseableHttpResponse response = null;
        String respResult = null;

        synchronized (syncLock) {
            try {
                List<NameValuePair> pairList = new ArrayList<NameValuePair>(params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue().toString());
                    pairList.add(pair);
                }

                httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("utf-8")));
                response = getHttpClient().execute(httpPost);

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.SC_OK) {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        respResult = EntityUtils.toString(entity, "utf-8");
                        response.close();
                    }
                } else {
                    httpPost.abort();
                    logger.error("[HttpClient]: Unexpected response statusCode=" + statusCode);
                    throw new ClientProtocolException("Unexpected response statusCode=" + statusCode);
                }
            } catch (IOException e) {
                logger.error("[HttpClient]: params={" + params + "}, printStackTrace=" + e);
            } finally {
                httpPost.releaseConnection();
                if (response != null) {
                    try {
                        EntityUtils.consume(response.getEntity());
                        response.close();
                    } catch (IOException e) {
                        logger.error("[HttpClient]: params={" + params + "}, printStackTrace=" + e);
                    }
                }
            }
        }
        return respResult;
    }

    /**
     * 异步HTTP POST 请求
     * @param url
     * @param map
     * @param listener
     */
    public static void asyncPost(String url, Map<String, String> map, final com.paycloud.utils.http.HttpListener listener) {
        try {
            // 设置协议http和https对应的处理socket链接工厂的对象
            SSLContext sslcontext = SSLContexts.custom().useTLS().loadTrustMaterial(null, new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();

            SSLIOSessionStrategy sslSessionStrategy = new SSLIOSessionStrategy(sslcontext, new AllowAllHostnameVerifier());

            Registry<SchemeIOSessionStrategy> sessionStrategyRegistry = RegistryBuilder.<SchemeIOSessionStrategy>create()
                    .register("http", NoopIOSessionStrategy.INSTANCE)
                    .register("https", sslSessionStrategy)
                    .build();

            //配置io线程
            IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
                    .setIoThreadCount(Runtime.getRuntime().availableProcessors())
                    .setConnectTimeout(20000) //毫秒
                    .setSoTimeout(20000) //毫秒
                    .build();

            //设置连接池大小
            ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);
            PoolingNHttpClientConnectionManager connManager = new PoolingNHttpClientConnectionManager(ioReactor, sessionStrategyRegistry);
            connManager.setMaxTotal(1024 * 1024 * 2);
            connManager.setDefaultMaxPerRoute(1024 * 1024 * 1);

            //创建自定义的httpclient对象
            final CloseableHttpAsyncClient client = HttpAsyncClients.custom().setConnectionManager(connManager).build();

            //创建post方式请求对象
            HttpPost httpPost = new HttpPost(url);

            //装填参数
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            if (map != null) {
                for (Entry<String, String> entry : map.entrySet()) {
                    nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
            }

            //设置参数到请求对象中
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));

            //设置header信息
            //指定报文头【Content-type】、【User-Agent】
            httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
            httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

            // Start the client
            client.start();

            //执行请求操作，并拿到结果（异步）
            client.execute(httpPost, new FutureCallback<HttpResponse>() {
                @Override
                public void completed(HttpResponse resp) {
                    String body = "";
                    //这里使用EntityUtils.toString()方式时会大概率报错，原因：未接受完毕，链接已关
                    try {
                        HttpEntity entity = resp.getEntity();
                        if (entity != null) {
                            final InputStream instream = entity.getContent();
                            try {
                                final StringBuilder sb = new StringBuilder();
                                final char[] tmp = new char[1024];
                                final Reader reader = new InputStreamReader(instream, "UTF-8");
                                int l;
                                while ((l = reader.read(tmp)) != -1) {
                                    sb.append(tmp, 0, l);
                                }
                                body = sb.toString();
                                //限制返回长度
                                if (body.length() > 10) {
//                                    logger.error("[HttpClient]: HttpResponse respBody=" + body);
                                    body = "RESPONSE_TOO_LONG";
                                }
                            } finally {
                                instream.close();
                                EntityUtils.consume(entity);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        //logger.error("[HttpClient]: HttpResponse Exception A1=" + e.getMessage());
                    }
                    listener.onCompleted(body);
                    close(client);
                }

                @Override
                public void cancelled() {
                    listener.onCanceled();
                    close(client);
                }

                @Override
                public void failed(Exception ex) {
                    listener.onFailed(ex);
                    close(client);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            //logger.error("[HttpClient]: HttpResponse Exception A2=" + e.getMessage());
        }
    }

    /**
     * 通过get请求url地址,获取返回结果
     *
     * @param url 发送请求的url
     * @return
     * @throws Exception
     */
    public static String rsyncGet(String url) throws Exception {
        HttpGet httpPost = new HttpGet(url);

        CloseableHttpResponse response = null;
        String respResult = null;
        synchronized (syncLock) {
            try {

                response = getHttpClient().execute(httpPost);

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.SC_OK) {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        respResult = EntityUtils.toString(entity, "utf-8");
                        response.close();
                    }
                } else {
                    httpPost.abort();
                    logger.error("[HttpClient]: Unexpected response statusCode=" + statusCode);
                    throw new ClientProtocolException("Unexpected response statusCode=" + statusCode);
                }
            } catch (IOException e) {
                logger.error("[HttpClient]: printStackTrace=" + e);
            } finally {
                httpPost.releaseConnection();
                if (response != null) {
                    try {
                        EntityUtils.consume(response.getEntity());
                        response.close();
                    } catch (IOException e) {
                        logger.error("[HttpClient]: printStackTrace=" + e);
                    }
                }
            }
        }
        return respResult;
    }

    /**
     * 关闭client对象
     *
     * @param client
     */
    private static void close(CloseableHttpAsyncClient client) {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}