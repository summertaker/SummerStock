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

public class ReturnAdapter extends BaseDataAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<Item> mDataList;

    public ReturnAdapter(Context context, ArrayList<Item> dataList) {
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

            convertView = mLayoutInflater.inflate(R.layout.return_row, null);

            holder.tvNo = convertView.findViewById(R.id.tvNo);
            holder.tvName = convertView.findViewById(R.id.tvName);

            holder.tvRor = convertView.findViewById(R.id.tvRor);
            holder.tvPdt = convertView.findViewById(R.id.tvPdt);
            holder.tvNdp = convertView.findViewById(R.id.tvNdp);

            holder.ivChart = convertView.findViewById(R.id.ivChart);

            //holder.tvFavorite = convertView.findViewById(R.id.tvFavorite);
            //holder.tvPossession = convertView.findViewById(R.id.tvPossession);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
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
        holder.tvName.setText(name);

        // 수익률
        String ror = Config.FLOAT_FORMAT.format(data.getRor()) + "%";
        if (data.getRor() > 0.0) {
            ror = "+" + ror;
            holder.tvRor.setTextColor(Config.COLOR_PRICE_RISE);
        } else {
            holder.tvRor.setTextColor(Config.COLOR_PRICE_FALL);
        }
        holder.tvRor.setText(ror);

        // 예측일
        String pdt = data.getPdt();
        holder.tvPdt.setText(pdt);

        // 경과일
        String ndp = "(" + data.getNdp() + "일 경과" + ")";
        holder.tvNdp.setText(ndp);

        // 차트
        String chartUrl = "https://ssl.pstatic.net/imgfinance/chart/mobile/candle/day/" + data.getCode() + "_end.png";
        Glide.with(mContext).load(chartUrl).apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(holder.ivChart);

        return convertView;
    }

    static class ViewHolder {
        TextView tvNo;
        TextView tvName;

        TextView tvRor;
        TextView tvPdt;
        TextView tvNdp;

        ImageView ivChart;

        //TextView tvFavorite;
        //TextView tvPossession;
    }
}
