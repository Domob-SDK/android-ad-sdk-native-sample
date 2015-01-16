package cn.domob.nativeaddemo.service;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;

import cn.domob.nativeaddemo.domain.NewsInfo;

public class NewsEngine {

    public NewsInfo getNewsInfo() {
        InputStream in = getFromRSSFeed(ItemsControl.NEWS_URL);
        return ItemsControl.parse(in);
    }

    public InputStream getFromRSSFeed(String url) {
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);

            HttpResponse httpResponse = httpClient.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == ItemsControl.HTTPSTATUS_OK) {
                return httpResponse.getEntity().getContent();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
