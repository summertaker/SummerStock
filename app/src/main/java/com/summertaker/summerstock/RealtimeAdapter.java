package com.summertaker.summerstock;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.summertaker.summerstock.common.BaseDataAdapter;
import com.summertaker.summerstock.common.Config;
import com.summertaker.summerstock.data.Item;

import java.util.ArrayList;

public class RealtimeAdapter extends BaseDataAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<Item> mDataList;

    public RealtimeAdapter(Context context, ArrayList<Item> dataList) {
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

            convertView = mLayoutInflater.inflate(R.layout.realtime_row, null);

            holder.tvNo = convertView.findViewById(R.id.tvNo);
            holder.tvName = convertView.findViewById(R.id.tvName);
            holder.tvPrice = convertView.findViewById(R.id.tvPrice);
            holder.tvAdr = convertView.findViewById(R.id.tvAdr);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 순차 번호
        String no = data.getNo() + "";
        holder.tvNo.setText(no);

        // 종목 이름
        String name = data.getName();
        holder.tvName.setText(name);

        // 현재가
        String price = Config.NUMBER_FORMAT.format(data.getPrice());
        holder.tvPrice.setText(price);

        // 등락률
        String adr = Config.FLOAT_FORMAT.format(data.getAdr()) + "%";
        holder.tvAdr.setText(adr);

        // 등락 색상
        if (data.getAdr() > 0) {
            //holder.tvNo.setTextColor(Config.COLOR_PRICE_RISE);
            //holder.tvName.setTextColor(Config.COLOR_PRICE_RISE);

            holder.tvPrice.setTextColor(Config.COLOR_PRICE_RISE);
            holder.tvAdr.setTextColor(Config.COLOR_PRICE_RISE);
        } else {
            //holder.tvNo.setTextColor(Config.COLOR_PRICE_FALL);
            //holder.tvName.setTextColor(Config.COLOR_PRICE_FALL);

            holder.tvPrice.setTextColor(Config.COLOR_PRICE_FALL);
            holder.tvAdr.setTextColor(Config.COLOR_PRICE_FALL);
        }

        return convertView;
    }

    static class ViewHolder {
        TextView tvNo;
        TextView tvName;
        TextView tvPrice;
        TextView tvAdr;
    }
}
