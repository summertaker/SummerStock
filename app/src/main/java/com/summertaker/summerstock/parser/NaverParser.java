package com.summertaker.summerstock.parser;

import com.summertaker.summerstock.common.BaseParser;
import com.summertaker.summerstock.common.Config;
import com.summertaker.summerstock.data.ItemData;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class NaverParser extends BaseParser {

    public void parseRise(String response, ArrayList<ItemData> dataList) {
        if (response == null || response.isEmpty()) {
            return;
        }

        Document doc = Jsoup.parse(response);

        Elements tables = doc.getElementsByTag("table");

        for (Element table : tables) {
            String className = table.attr("class");
            if (!"type_2".equals(className)) {
                continue;
            }

            int no = 1;

            Elements trs = table.getElementsByTag("tr");
            for (Element tr : trs) {
                Elements tds = tr.getElementsByTag("td");
                if (tds.size() != 12) {
                    continue;
                }

                String code = "";
                String name = "";
                int price = 0;
                int adp = 0;
                float adr = 0;
                int volume = 0;
                float per = 0;
                float roe = 0;

                String temp = "";

                Elements a = tds.get(1).getElementsByTag("a");
                String href = a.attr("href");
                String[] array = href.split("=");
                code = array[1];
                name = a.text();

                temp = tds.get(2).text();
                temp = temp.replace(",", "");
                price = Integer.parseInt(temp);
                if (price < Config.PRICE_MIN) {
                    continue;
                }

                temp = tds.get(3).text();
                temp = temp.replace(",", "");
                adp = Integer.valueOf(temp);

                temp = tds.get(4).text();
                temp = temp.replace(",", "");
                temp = temp.replace("%", "");
                adr = Float.valueOf(temp);
                if (adr < Config.ADR_MIN) {
                    continue;
                }

                temp = tds.get(5).text();
                temp = temp.replace(",", "");
                volume = Integer.valueOf(temp);

                temp = tds.get(10).text();
                temp = temp.replace(",", "");
                temp = temp.replace("%", "");
                temp = temp.replace("N/A", "0");
                per = Float.valueOf(temp);

                temp = tds.get(11).text();
                temp = temp.replace(",", "");
                temp = temp.replace("%", "");
                temp = temp.replace("N/A", "0");
                roe = Float.valueOf(temp);

                //title = title.replaceAll("\\[.*\\]", "");

                //Log.e(mTag, "itemCd: " + code + ", " + name + ", " + price + ", " + adp + ", " + adr + ", " + volume + ", " + per + ", " + roe);

                ItemData data = new ItemData();
                data.setNo(no);
                data.setCode(code);
                data.setName(name);
                data.setPrice(price);
                data.setAdp(adp);
                data.setAdr(adr);
                data.setVolume(volume);
                data.setPer(per);
                data.setRoe(roe);
                dataList.add(data);

                no++;
            }
        }
    }
}
