package com.summertaker.summerstock;

import android.content.Context;
import android.content.res.Resources;
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

public class TopAdapter extends BaseDataAdapter {

    private Context mContext;
    private Resources mResources;
    private LayoutInflater mLayoutInflater;
    private ArrayList<Item> mDataList;

    public TopAdapter(Context context, ArrayList<Item> dataList) {
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
        final ViewHolder holder;
        final Item data = mDataList.get(position);

        if (convertView == null) {
            holder = new ViewHolder();

            convertView = mLayoutInflater.inflate(R.layout.top_row, null);

            holder.tvNo = convertView.findViewById(R.id.tvNo);
            holder.tvName = convertView.findViewById(R.id.tvName);

            holder.tvPsp = convertView.findViewById(R.id.tvPsp);
            holder.tvPdt = convertView.findViewById(R.id.tvPdt);
            holder.tvNdp = convertView.findViewById(R.id.tvNdp);

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
        String name = data.getName() + " (" + data.getNor() + ")";
        holder.tvName.setText(name);

        // 가격
        String psp = Config.NUMBER_FORMAT.format(data.getPsp());
        holder.tvPsp.setText(psp);

        // 추천일
        String pdt = data.getPdt();
        holder.tvPdt.setText(pdt);

        // 경과일
        String ndp = "(" + data.getNdp() + "일 경과)";
        holder.tvNdp.setText(ndp);

        // 차트
        String chartUrl = "https://ssl.pstatic.net/imgfinance/chart/mobile/candle/day/" + data.getCode() + "_end.png";
        Glide.with(mContext).load(chartUrl).apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(holder.ivChart);

        return convertView;
    }

    static class ViewHolder {
        TextView tvNo;
        TextView tvName;

        TextView tvPsp;
        TextView tvPdt;
        TextView tvNdp;

        ImageView ivChart;

        //TextView tvFavorite;
        //TextView tvPossession;
    }
}
