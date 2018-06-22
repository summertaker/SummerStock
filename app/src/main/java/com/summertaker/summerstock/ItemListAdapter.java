package com.summertaker.summerstock;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.summertaker.summerstock.common.BaseDataAdapter;
import com.summertaker.summerstock.common.Config;
import com.summertaker.summerstock.data.Item;

import java.util.ArrayList;

public class ItemListAdapter extends BaseDataAdapter {

    private Context mContext;
    private Resources mResources;
    private LayoutInflater mLayoutInflater;
    private ArrayList<Item> mDataList;

    public ItemListAdapter(Context context, String fragmentId, ArrayList<Item> dataList) {
        this.mContext = context;
        this.mResources = context.getResources();
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mDataList = dataList;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ItemListAdapter.ViewHolder holder;
        final Item data = mDataList.get(position);

        if (convertView == null) {
            holder = new ItemListAdapter.ViewHolder();

            convertView = mLayoutInflater.inflate(R.layout.item_list_row, null);

            holder.tvNo = convertView.findViewById(R.id.tvNo);
            //holder.tvCode = convertView.findViewById(R.id.tvCode);
            holder.tvName = convertView.findViewById(R.id.tvName);

            holder.loPrice = convertView.findViewById(R.id.loPrice);
            holder.tvPriceTitle = convertView.findViewById(R.id.tvPriceTitle);
            holder.tvPrice = convertView.findViewById(R.id.tvPrice);
            holder.tvAdp = convertView.findViewById(R.id.tvAdp);
            holder.tvAdr = convertView.findViewById(R.id.tvAdr);
            holder.tvIndt = convertView.findViewById(R.id.tvIndt);

            holder.loReturn = convertView.findViewById(R.id.loReturn);
            holder.tvRor = convertView.findViewById(R.id.tvRor);
            holder.tvNdr = convertView.findViewById(R.id.tvNdr);

            holder.ivChart = convertView.findViewById(R.id.ivChart);

            holder.tvFavorite = convertView.findViewById(R.id.tvFavorite);
            holder.tvPossession = convertView.findViewById(R.id.tvPossession);

            convertView.setTag(holder);
        } else {
            holder = (ItemListAdapter.ViewHolder) convertView.getTag();
        }

        /*
        // 보유
        if (data.getPossession().isEmpty()) {
            holder.tvPossession.setVisibility(View.GONE);
        } else {
            holder.tvPossession.setVisibility(View.VISIBLE);
        }

        // 관심
        if (data.getFavorite().isEmpty()) {
            holder.tvFavorite.setVisibility(View.GONE);
            holder.tvFavorite.setText(mResources.getString(R.string.favorite_off));
            holder.tvFavorite.setTextColor(mResources.getColor(R.color.favorite_off));
        } else {
            holder.tvFavorite.setVisibility(View.VISIBLE);
            holder.tvFavorite.setText(mResources.getString(R.string.favorite_on));
            holder.tvFavorite.setTextColor(mResources.getColor(R.color.favorite_on));
        }
        */

        // 순차 번호
        String no = data.getNo() + ".";
        holder.tvNo.setText(no);

        // 종목 이름
        String name = data.getName();
        if (data.getNor() > 0) {
            name = name + " (" + data.getNor() + ")";
        }
        holder.tvName.setText(name);

        // 종목 코드
        //String code = data.getCode();
        //holder.tvCode.setText(code);

        // 가격
        if (data.getPrice() > 0 || data.getPsp() > 0) {
            holder.loPrice.setVisibility(View.VISIBLE);

            String price = "";
            if (data.getPrice() > 0) {
                price = Config.NUMBER_FORMAT.format(data.getPrice());
                holder.tvPriceTitle.setText(mResources.getString(R.string.current_price)); // 현재가
            } else if (data.getPsp() > 0) {
                price = Config.NUMBER_FORMAT.format(data.getPsp());
                holder.tvPriceTitle.setText(mResources.getString(R.string.prediction_stock_price)); // 예측가
            }
            holder.tvPrice.setText(price);
        }

        //----------------
        // 등락률
        //----------------
        if (data.getAdr() == 0) {
            holder.tvAdr.setVisibility(View.GONE);
        } else {
            holder.tvAdr.setVisibility(View.VISIBLE);

            String adr = Config.FLOAT_FORMAT.format(data.getAdr()) + "%";
            if (data.getAdr() > 0) {
                adr = "+" + adr;
                holder.tvAdr.setTextColor(Config.COLOR_PRICE_RISE);
            } else {
                //adr = "▽" + adr;
                holder.tvAdr.setTextColor(Config.COLOR_PRICE_FALL);
            }
            //adr = "(" + adr + ")";
            holder.tvAdr.setText(adr);
        }

        // 전일비
        if (data.getAdp() == 0) {
            holder.tvAdp.setVisibility(View.GONE);
        } else {
            holder.tvAdp.setVisibility(View.VISIBLE);

            String adp = Config.NUMBER_FORMAT.format(data.getAdp());
            if (data.getAdp() > 0) {
                adp = "+" + adp;
                //holder.tvAdp.setTextColor(Config.COLOR_PRICE_RISE);
            } else {
                //adp = "▼" + adp;
                //holder.tvAdp.setTextColor(Config.COLOR_PRICE_FALL);
            }
            adp = "(" + adp + ")";
            holder.tvAdp.setText(adp);
        }

        /*
        // 추천일
        if (data.getIndt() == null || data.getIndt().isEmpty()) {
            holder.tvIndt.setVisibility(View.GONE);
        } else {
            holder.tvIndt.setVisibility(View.VISIBLE);

            String indt = mResources.getString(R.string.recommendation_date) + " " + data.getIndt();
            if (data.getNdr() > 0) {
                indt = indt + " (" + data.getNdr() + "일 전" + ")";
            }
            holder.tvIndt.setText(indt);
        }

        // 추천일 후 누적 수익률
        if (data.getRor() > 0) {
            holder.loReturn.setVisibility(View.VISIBLE);

            String ror = Config.FLOAT_FORMAT.format(data.getRor()) + "%";
            if (data.getRor() > 0.0) {
                ror = "+" + ror;
                holder.tvRor.setTextColor(Config.COLOR_PRICE_RISE);
            } else {
                //ror = "▽" + ror;
                holder.tvRor.setTextColor(Config.COLOR_PRICE_FALL);
            }
            holder.tvRor.setText(ror);

            // 경과일
            if (data.getNdr() == 0) {
                holder.tvNdr.setVisibility(View.GONE);
            } else {
                holder.tvNdr.setVisibility(View.VISIBLE);
                String ndr = "(" + data.getNdr() + "일 경과)";
                holder.tvNdr.setText(ndr);
            }
        }
        */

        /*
        // 수정가
        int adjPrc = data.getAdjPrc();
        if (adjPrc == 0) {
            adjPrc = data.getPrePrc();
        }
        if (adjPrc == 0) {
            holder.loAdjPrc.setVisibility(View.GONE);
        } else {
            holder.loAdjPrc.setVisibility(View.VISIBLE);

            // 수정가
            String adjPrcText = Config.NUMBER_FORMAT.format(adjPrc) + "원";
            if (adjPrc > data.getPrc()) {
                holder.tvAdjPrc.setTextColor(Config.COLOR_PRICE_RISE);
            } else {
                holder.tvAdjPrc.setTextColor(Config.COLOR_PRICE_FALL);
            }
            holder.tvAdjPrc.setText(adjPrcText);

            // 가격 차이
            int diff = data.getAdjPrc() - data.getPrc();
            String diffPrcText = Config.NUMBER_FORMAT.format(diff) + "원";
            if (diff > 0) {
                diffPrcText = "+" + diffPrcText;
                holder.tvDiffPrc.setTextColor(Config.COLOR_PRICE_RISE);
            } else {
                holder.tvDiffPrc.setTextColor(Config.COLOR_PRICE_FALL);
            }
            diffPrcText = "(" + diffPrcText + ")";
            holder.tvDiffPrc.setText(diffPrcText);

            // 추천일
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", mLocale);
            Calendar cal = Calendar.getInstance();
            Date d = cal.getTime();
            long curTime = d.getTime();

            try {
                Date date = sdf.parse(data.getInDt());
                String inDt = data.getInDt(); //new SimpleDateFormat("yy-MM-dd", mLocale).format(date);
                holder.tvInDt.setText(inDt);

                // 추천 경과일
                long oldTime = date.getTime();
                long diffTime = curTime - oldTime;
                int days = (int) ((((diffTime / 1000) / 60) / 60) / 24);

                String dtPassedText = (days == 0) ? mResources.getString(R.string.today) : "+" + days + "일";
                dtPassedText = "(" + dtPassedText + ")";
                holder.tvInDtPassed.setText(dtPassedText);
            } catch (ParseException e) {
                Log.e(mTag, "ERROR: " + e.getMessage());
            }
        }
        */

        /*
        // 수익률
        if (data.getWkRtn() == 0.0 && data.getMnRtn() == 0.0 && data.getMn3Rtn() == 0.0) {
            holder.loRtn.setVisibility(View.GONE);
        } else {
            holder.loRtn.setVisibility(View.VISIBLE);

            // 수익률: 1주일
            String wkRtnText = Config.FLOAT_FORMAT.format(data.getWkRtn()) + "%";
            holder.tvWkRtn.setText(wkRtnText);
            if (data.getWkRtn() > 0.0) {
                holder.tvWkRtn.setTextColor(Config.COLOR_PRICE_RISE);
            } else {
                holder.tvWkRtn.setTextColor(Config.COLOR_PRICE_FALL);
            }

            // 수익률: 1개월
            String mnRtnText = Config.FLOAT_FORMAT.format(data.getMnRtn()) + "%";
            holder.tvMnRtn.setText(mnRtnText);
            if (data.getMnRtn() > 0.0) {
                holder.tvMnRtn.setTextColor(Config.COLOR_PRICE_RISE);
            } else {
                holder.tvMnRtn.setTextColor(Config.COLOR_PRICE_FALL);
            }

            // 수익률: 3개월
            String mn3RtnText = Config.FLOAT_FORMAT.format(data.getMn3Rtn()) + "%";
            holder.tvMn3Rtn.setText(mn3RtnText);
            if (data.getMn3Rtn() > 0.0) {
                holder.tvMn3Rtn.setTextColor(Config.COLOR_PRICE_RISE);
            } else {
                holder.tvMn3Rtn.setTextColor(Config.COLOR_PRICE_FALL);
            }
        }
        */

        /*
        if (data.getInReason() == null || data.getInReason().isEmpty()) {
            holder.tvInReason.setVisibility(View.GONE);
        } else {
            holder.tvInReason.setVisibility(View.VISIBLE);
            holder.tvInReason.setText(data.getInReason());
        }
        */

        //String weekChartUrl = "https://ssl.pstatic.net/imgfinance/chart/item/area/week/" + data.getItemCd() + ".png";
        //Glide.with(mContext).load(weekChartUrl).apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(holder.ivWeekChart);

        //String month3ChartUrl = "https://chart-finance.daumcdn.net/time3/3month/" + data.getItemCd() + "-290157.png";
        //Glide.with(mContext).load(month3ChartUrl).apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(holder.ivMonth3Chart);

        //String chartUrl = "https://chart-finance.daumcdn.net/time3/3month/" + data.getItemCd() + "-290157.png"; // 다음
        String chartUrl = "https://ssl.pstatic.net/imgfinance/chart/mobile/candle/day/" + data.getCode() + "_end.png";
        Glide.with(mContext).load(chartUrl).apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(holder.ivChart);

        //String chart2 = "https://chart-finance.daumcdn.net/time3/year/" + data.getItemCd() + "-290157.png";
        //Glide.with(mContext).load(chart2).apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(holder.ivChart2);

        return convertView;
    }

    static class ViewHolder {
        TextView tvNo;
        //TextView tvCode;
        TextView tvName;
        //TextView tvPfNm;

        LinearLayout loPrice;
        TextView tvPriceTitle;
        TextView tvPrice;
        TextView tvAdp;
        TextView tvAdr;
        TextView tvIndt;

        LinearLayout loReturn;
        TextView tvRor;
        TextView tvNdr;

        //TextView tvInReason;

        ImageView ivChart;

        TextView tvFavorite;
        TextView tvPossession;
    }
}
