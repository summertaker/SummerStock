package com.summertaker.summerstock.parser;

import android.content.Context;

import com.summertaker.summerstock.common.BaseParser;
import com.summertaker.summerstock.data.News;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DaumNewsParser extends BaseParser {

    public void parseList(Context context, String itemNm, String response, ArrayList<News> dataList) {
        if (response == null || response.isEmpty()) {
            return;
        }

        Document doc = Jsoup.parse(response);

        Element root = doc.getElementById("itemNewsList");
        if (root == null) {
            return;
        }

        Element ul = root.getElementsByTag("ul").first();
        if (ul == null) {
            return;
        }

        DateFormat sdf = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());

        Calendar cal = Calendar.getInstance();
        Date curDate = cal.getTime();

        int year = cal.get(Calendar.YEAR);
        String century = (year + "").substring(0, 2);

        for (Element li : ul.select("li")) {
            String title = "";
            String url = "";
            String summary = "";
            String date_str = "";
            Date published = null;
            String publishedText = "";
            long elapsed = 0;

            Element el;

            el = li.select("strong > a").first();
            title = el.text();
            if (itemNm != null && !itemNm.isEmpty()) {
                if (!title.contains(itemNm)) {
                    continue;
                }
            }

            if (title.contains("신고가") || title.contains("신저가") || title.contains("상담") || title.contains("주주")
                    || title.contains("반등") || title.contains("증자") || title.contains("배당")) {
                continue;
            }

            title = title.replaceAll("\\[.*\\]", "");
            title = title.replaceAll("\\.\\.", "");
            title = title.trim();

            if (itemNm != null && !itemNm.isEmpty()) {
                title = title.replace(itemNm, "<font color='#1565C0'>" + itemNm + "</font>");
            }

            url = el.attr("href");
            url = "http://m.finance.daum.net/" + url;

            el = li.select("span.datetime").first();
            publishedText = el.text();
            publishedText = century + publishedText; //.replace(".", "-");

            try {
                published = sdf.parse(publishedText);
                long diff = curDate.getTime() - published.getTime();
                elapsed = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                //Log.e(mTag, "days: " + days);

            } catch (ParseException e) {
                e.printStackTrace();
            }
            //published = published + ":00";

            el = li.select("p > a").first();
            summary = el.text();

            //Log.e(mTag, title + " " + published + " " + url);

            News news = new News();
            news.setTitle(title);
            news.setUrl(url);
            news.setSummary(summary);
            news.setPublished(published);
            news.setPublishedText(publishedText);
            news.setElapsed(elapsed);

            dataList.add(news);
        }
    }
}

