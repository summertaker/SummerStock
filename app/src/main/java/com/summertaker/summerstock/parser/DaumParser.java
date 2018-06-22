package com.summertaker.summerstock.parser;

import android.util.Log;

import com.summertaker.summerstock.common.BaseParser;
import com.summertaker.summerstock.data.Item;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class DaumParser extends BaseParser {

    public Item parseItemDetail(String response) {
        Item item = new Item();

        if (response == null || response.isEmpty()) {
            return item;
        }

        Document doc = Jsoup.parse(response);

        // 제목
        Elements h2s = doc.getElementsByTag("h2");
        for (Element h2 : h2s) {
            String attr = h2.attr("onclick");
            //Log.e(mTag, "attr: " + attr);

            if (attr.contains("GoPage(")) {
                String name = h2.text();
                item.setName(name);
                break;
            }
        }

        // 상세
        Elements uls = doc.getElementsByTag("ul");
        for (Element ul : uls) {
            String attr = ul.attr("class");
            //Log.e(mTag, "attr: " + attr);

            if ("list_stockrate".equals(attr)) {

                int price = 0;
                int adp = 0;
                float adr = 0;
                String temp = "";

                Elements lis = ul.getElementsByTag("li");

                temp = lis.get(0).text();
                temp = temp.replace(",", "");
                price = Integer.valueOf(temp);


                temp = lis.get(1).text();
                temp = temp.replace(",", "");
                adp = Integer.valueOf(temp);

                temp = lis.get(2).text();
                temp = temp.replace(",", "");
                temp = temp.replace("%", "");
                temp = temp.replace("％", "");
                adr = Float.valueOf(temp);

                item.setPrice(price);
                item.setAdp(adp);
                item.setAdr(adr);
            }
        }

        return item;
    }

    public void parseAllItemPrice(String response, ArrayList<Item> itemList) {
        if (response == null || response.isEmpty()) {
            return;
        }

        Document doc = Jsoup.parse(response);

        Elements tables = doc.getElementsByTag("table");

        for (Element table : tables) {

            String className = table.attr("class");
            if (!"gTable clr".equals(className)) {
                continue;
            }

            int no = 1;

            Elements trs = table.getElementsByTag("tr");
            for (Element tr : trs) {

                Elements tds = tr.getElementsByTag("td");
                if (tds.size() != 6) {
                    continue;
                }

                String code = "";
                String name = "";
                int price = 0;
                float adr = 0;

                String temp = "";

                Element el = tds.get(0).getElementsByTag("a").get(0);
                String href = el.attr("href");
                String[] array = href.split("=");
                code = array[1];
                name = el.text();

                temp = tds.get(1).text();
                temp = temp.replace(",", "");
                price = Integer.parseInt(temp);

                temp = tds.get(2).text();
                temp = temp.replace(",", "");
                temp = temp.replace("%", "");
                adr = Float.valueOf(temp);

                Item item = new Item();
                item.setNo(no);
                item.setCode(code);
                item.setName(name);
                item.setPrice(price);
                item.setAdr(adr);
                itemList.add(item);

                no++;

                el = tds.get(3).getElementsByTag("a").get(0);
                href = el.attr("href");
                array = href.split("=");
                code = array[1];
                name = el.text();

                temp = tds.get(4).text();
                temp = temp.replace(",", "");
                price = Integer.parseInt(temp);

                temp = tds.get(5).text();
                temp = temp.replace(",", "");
                temp = temp.replace("%", "");
                adr = Float.valueOf(temp);

                //if (price < 200000) {
                //    //if (price < Config.PRICE_MIN || price > Config.PRICE_MAX) {
                //    continue;
                //}

                //Log.e(mTag, code + " " + name + " " + price + " " + adr);

                item = new Item();
                item.setNo(no);
                item.setCode(code);
                item.setName(name);
                item.setPrice(price);
                item.setAdr(adr);
                itemList.add(item);

                no++;
            }
        }
    }
}
