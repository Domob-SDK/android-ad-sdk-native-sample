package cn.domob.nativeaddemo.domain;

import java.util.ArrayList;
import java.util.List;

import cn.domob.android.nativeads.NativeAd;

/**
 * @author Alan
 */
public class NewsInfo {

    private static final int TYPE_NUM = 2;
    private String title;
    private List<NewsItem> items = new ArrayList<NewsItem>();

    /**
     * 清空items
     */
    public void clear() {
        items.clear();
    }

    /**
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the items
     */
    public List<NewsItem> getItems() {
        return items;
    }

    /**
     * @param if item is native ad, insert item.
     */
    public void addItem(NewsItem newsItem) {
        if (newsItem == null) {
            return;
        }
        if (items.size() - 1 > newsItem.getPosition()) {
            items.add(newsItem.getPosition(), newsItem);
        } else {
            items.add(newsItem);
        }
    }

    /**
     * @param newsItem
     */
    public void addAllItems(NewsInfo newsInfo) {
        if (newsInfo == null) {
            return;
        }
        title = newsInfo.getTitle();
        if (newsInfo.getItems().size() > 0) {
            items.addAll(items.size(), newsInfo.getItems());
        }
    }

    public int getTypeCount() {
        return TYPE_NUM;
    }

    @Override
    public String toString() {
        return "NewsInfo [title=" + title + ", items=" + items + "]";
    }

    public static class NewsItem {

        private String title;
        private String desc;
        private String link;
        private String putData;
        private NativeAd nativeAd;
        private int position;
        private int type;

        /**
         * @return position
         */
        public int getPosition() {
            return position;
        }

        /**
         * @param position
         */
        public void setPosition(int position) {
            this.position = position;
        }

        /**
         * @return type
         */
        public int getType() {
            return type;
        }

        /**
         * @param type
         */
        public void setType(int type) {
            this.type = type;
        }

        /**
         * @return putData
         */
        public String getPutData() {
            return putData;
        }

        /**
         * @param putData
         */
        public void setPutData(String putData) {

            this.putData = putData;
        }

        /**
         * @return the title
         */
        public String getTitle() {
            return title;
        }

        /**
         * @param title the title to set
         */
        public void setTitle(String title) {
            this.title = title;
        }

        /**
         * @return the description
         */
        public String getDesc() {
            return desc;
        }

        /**
         * @param desc the description to set
         */
        public void setDesc(String desc) {
            this.desc = desc;
        }

        /**
         * @return the link
         */
        public String getLink() {
            return link;
        }

        /**
         * @param link the link to set
         */
        public void setLink(String link) {
            this.link = link;
        }

        @Override
        public String toString() {
            return "NewsItem [title=" + title + ", desc=" + desc + ", link=" + link + ", putData=" + putData + "]";
        }

        /**
         * @param nativeAd
         */
        public void setNativeAd(NativeAd nativeAd) {
            this.nativeAd = nativeAd;
        }

        /**
         * @return nativeAd
         */
        public NativeAd getNativeAd() {
            return nativeAd;
        }
    }
}
