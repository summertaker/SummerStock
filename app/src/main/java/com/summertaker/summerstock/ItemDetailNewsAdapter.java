package com.summertaker.summerstock;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.summertaker.summerstock.common.BaseDataAdapter;
import com.summertaker.summerstock.data.NewsData;

import java.util.ArrayList;

public class ItemDetailNewsAdapter extends BaseDataAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<NewsData> mDataList = null;

    public ItemDetailNewsAdapter(Context context, ArrayList<NewsData> dataList) {
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
        final ItemDetailNewsAdapter.ViewHolder holder;
        final NewsData data = mDataList.get(position);

        if (convertView == null) {
            holder = new ItemDetailNewsAdapter.ViewHolder();

            convertView = mLayoutInflater.inflate(R.layout.item_detail_news_row, null);

            holder.tvTitle = convertView.findViewById(R.id.tvTitle);
            holder.tvElapsed = convertView.findViewById(R.id.tvElapsed);
            holder.tvPublished = convertView.findViewById(R.id.tvPublished);
            holder.tvSummary = convertView.findViewById(R.id.tvSummary);

            convertView.setTag(holder);
        } else {
            holder = (ItemDetailNewsAdapter.ViewHolder) convertView.getTag();
        }

        String title = data.getTitle();
        //title = title.replace("텍스트", "<font color='red'>텍스트</font>");
        holder.tvTitle.setText(Html.fromHtml(title), TextView.BufferType.SPANNABLE);

        String elapsed = "";
        if (data.getElapsed() == 0) {
            elapsed = "오늘";
        } else {
            elapsed = data.getElapsed() + "일 전";
        }
        holder.tvElapsed.setText(elapsed);

        String published = "(" + data.getPublishedText() + ")";
        holder.tvPublished.setText(published);

        holder.tvSummary.setText(data.getSummary());

        return convertView;
    }

    static class ViewHolder {
        TextView tvTitle;
        TextView tvElapsed;
        TextView tvPublished;
        TextView tvSummary;
    }
}
