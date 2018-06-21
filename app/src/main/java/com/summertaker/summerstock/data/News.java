package com.summertaker.summerstock.data;

import android.support.annotation.NonNull;

import java.util.Comparator;
import java.util.Date;

public class News implements Comparable<News> {
    private String itemCd;
    private String itemNm;
    private String title;
    private String url;
    private String summary;
    private Date published;
    private String publishedText;
    private long elapsed;

    @Override
    public int compareTo(@NonNull News o) {
        return 0;
    }

    public static Comparator<News> compareToPublished = new Comparator<News>() {

        public int compare(News d1, News d2) {

            Date v1 = d1.getPublished();
            Date v2 = d2.getPublished();

            //return value1 - value2; //ascending order
            return (int) v2.getTime() - (int) v1.getTime(); //descending order
        }

    };

    public String getItemCd() {
        return itemCd;
    }

    public void setItemCd(String itemCd) {
        this.itemCd = itemCd;
    }

    public String getItemNm() {
        return itemNm;
    }

    public void setItemNm(String itemNm) {
        this.itemNm = itemNm;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Date getPublished() {
        return published;
    }

    public void setPublished(Date published) {
        this.published = published;
    }

    public long getElapsed() {
        return elapsed;
    }

    public void setElapsed(long elapsed) {
        this.elapsed = elapsed;
    }

    public String getPublishedText() {
        return publishedText;
    }

    public void setPublishedText(String publishedText) {
        this.publishedText = publishedText;
    }
}
