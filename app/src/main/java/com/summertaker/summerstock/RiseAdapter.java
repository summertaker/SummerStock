package com.summertaker.summerstock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.summertaker.summerstock.common.BaseDataAdapter;
import com.summertaker.summerstock.common.Config;
import com.summertaker.summerstock.data.Item;

import java.util.ArrayList;

public class RiseAdapter extends BaseDataAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<Item> mDataList;

    public RiseAdapter(Context context, ArrayList<Item> dataList) {
        this.mContext = context;
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
        final ViewHolder holder;
        final Item data = mDataList.get(position);

        if (convertView == null) {
            holder = new ViewHolder();

            convertView = mLayoutInflater.inflate(R.layout.rise_row, null);

            holder.tvNo = convertView.findViewById(R.id.tvNo);
            holder.tvName = convertView.findViewById(R.id.tvName);

            holder.tvPrice = convertView.findViewById(R.id.tvPrice);
            holder.tvAdp = convertView.findViewById(R.id.tvAdp);
            holder.tvAdr = convertView.findViewById(R.id.tvAdr);

            holder.ivChart = convertView.findViewById(R.id.ivChart);

            //holder.tvFavorite = convertView.findViewById(R.id.tvFavorite);
            //holder.tvPossession = convertView.findViewById(R.id.tvPossession);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 순차 번호
        String no = data.getNo() + ".";
        holder.tvNo.setText(no);

        // 종목 이름
        String name = data.getName();
        if (data.getNor() > 0) {
            name = name + " (" + data.getNor() + ")";
        }
        holder.tvName.setText(name);

        // 가격
        String price = Config.NUMBER_FORMAT.format(data.getPrice());
        holder.tvPrice.setText(price);
        if (data.getAdr() > 0) {
            holder.tvPrice.setTextColor(Config.COLOR_PRICE_RISE);
        } else {
            holder.tvPrice.setTextColor(Config.COLOR_PRICE_FALL);
        }

        // 등락률
        String adr = Config.FLOAT_FORMAT.format(data.getAdr()) + "%";
        if (data.getAdr() > 0) {
            adr = "+" + adr;
            holder.tvAdr.setTextColor(Config.COLOR_PRICE_RISE);
        } else {
            holder.tvAdr.setTextColor(Config.COLOR_PRICE_FALL);
        }
        holder.tvAdr.setText(adr);

        // 전일비
        String adp = Config.NUMBER_FORMAT.format(data.getAdp());
        if (data.getAdp() > 0) {
            adp = "+" + adp;
        }
        holder.tvAdp.setText(adp);

        // 차트
        String chartUrl = "https://ssl.pstatic.net/imgfinance/chart/mobile/candle/day/" + data.getCode() + "_end.png";
        Glide.with(mContext).load(chartUrl).apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(holder.ivChart);

        return convertView;
    }

    static class ViewHolder {
        TextView tvNo;
        TextView tvName;

        TextView tvPrice;
        TextView tvAdp;
        TextView tvAdr;

        ImageView ivChart;

        //TextView tvFavorite;
        //TextView tvPossession;
    }
}
