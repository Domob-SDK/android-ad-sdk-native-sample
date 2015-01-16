package cn.domob.nativeaddemo.service;

import android.os.Handler;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;

import cn.domob.nativeaddemo.domain.NewsInfo;

public class ItemsControl {

    public static final String NEWS_URL = "http://news.qq.com/newsgn/rss_newsgn.xml";
    public static final String INPUTENCODING = "gb2312";
    public static final int HTTPSTATUS_OK = 200;
    public static final int AD_TYPE = 1;
    public static final int NEWS_TYPE = 0;

    public void getItems(Handler handler, int messageWhat) {
        NewsInfo combo = new NewsEngine().getNewsInfo();
        if (combo == null) {
            Log.e("DomobSDK", "Request rss may be return null.");
            return;
        }
        handler.obtainMessage(messageWhat, combo).sendToTarget();
    }

    public static NewsInfo parse(InputStream is) {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(is, INPUTENCODING);
            int type = parser.getEventType();
            NewsInfo newsInfos = null;
            NewsInfo.NewsItem newsInfo = null;
            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    case XmlPullParser.START_TAG:
                        if ("channel".equals(parser.getName())) {
                            newsInfos = new NewsInfo();
                            parser.next();
                            parser.next();
                            if ("title".equals(parser.getName())) {
                                newsInfos.setTitle(parser.nextText());
                            }
                        } else if ("item".equals(parser.getName())) {
                            newsInfo = new NewsInfo.NewsItem();
                        } else if ("link".equals(parser.getName())) {
                            if (newsInfo != null)
                                newsInfo.setLink(parser.nextText());
                        } else if ("title".equals(parser.getName())) {
                            if (newsInfo != null)
                                newsInfo.setTitle(parser.nextText());
                        } else if ("description".equals(parser.getName())) {
                            if (newsInfo != null)
                                newsInfo.setDesc(parser.nextText());
                        } else if ("pubDate".equals(parser.getName())) {
                            if (newsInfo != null)
                                newsInfo.setPutData(parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("item".equals(parser.getName())) {
                            newsInfo.setType(NEWS_TYPE);
                            newsInfos.addItem(newsInfo);
                            newsInfo = null;
                        }
                        break;
                }
                type = parser.next();
            }
            is.close();
            return newsInfos;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
