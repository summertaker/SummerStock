package com.summertaker.summerstock.common;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.summertaker.summerstock.util.OkHttpSingleton;

import okhttp3.OkHttpClient;

public class BaseFragment extends Fragment {

    protected Context mContext;

    protected String mTag = "== " + this.getClass().getSimpleName();

    public BaseFragment() {

    }

    public void goTop() {

    }

    public boolean goBack() {
        return false;
    }

    public void openInNew() {

    }

    public void refreshFragment() {

    }

    protected void updateFavorite(String itemCd, final TextView tvFavorite) {
        /*
        //startAnimateRefresh();

        String url = Config.URL_FAVORITE_UPDATE + itemCd;
        //Log.e(mTag, "url: " + url);

        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.e(mTag, "response:\n" + response);
                if (response.isEmpty()) {
                    tvFavorite.setText(getString(R.string.favorite_off));
                    tvFavorite.setTextColor(getResources().getColor(R.color.favorite_off));
                } else {
                    tvFavorite.setText(getString(R.string.favorite_on));
                    tvFavorite.setTextColor(getResources().getColor(R.color.favorite_on));
                }
                //stopAnimateRefresh();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(mTag, ">> VOLLEY ERROR: " + error.getMessage());
            }
        });

        BaseApplication.getInstance().addToRequestQueue(strReq, mVolleyTag);
        */
    }
}
