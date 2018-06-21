package com.summertaker.summerstock.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.summertaker.summerstock.R;
import com.summertaker.summerstock.common.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Util {

    private static String mTag = "== Util";

    public static void alert(final Context context, String title, String message, final Activity activity) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        if (title != null) {
            alert.setTitle(title);
        }
        alert.setMessage(message);
        if (activity == null) {
            alert.setCancelable(true);
        } else {
            alert.setPositiveButton(context.getString(R.string.finish), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    activity.finish();
                }
            });
        }
        alert.setNegativeButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        alert.show();
    }

    public static String getString(JSONObject json, String key) {
        try {
            String str = json.getString(key);
            if ("null".equals(str)) {
                str = "";
            }
            return str;
        } catch (JSONException e) {
            return "";
        }
    }

    public static int getInt(JSONObject json, String key) {
        try {
            return json.getInt(key);
        } catch (JSONException e) {
            return 0;
        }
    }

    public static double getDouble(JSONObject json, String key) {
        try {
            return json.getDouble(key);
        } catch (JSONException e) {
            return 0.0;
        }
    }

    public static String readFile(String fileName) {

        String result = "";

        File file = new File(Config.DATA_PATH, fileName);
        if (file.exists()) {
            StringBuilder builder = new StringBuilder();
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                    //builder.append('\n');
                }
                reader.close();
            } catch (IOException e) {
                Log.e(mTag, "FILE: " + fileName);
                Log.e(mTag, "ERROR: " + e.getLocalizedMessage());
            }
            //Log.d(mTag, builder.toString());
            result = builder.toString();
        }

        return result;
    }

    public static void writeToFile(String fileName, String data) {
        final File file = new File(Config.DATA_PATH, fileName);

        try {
            boolean isSuccess = file.createNewFile();
            if (isSuccess) {
                FileOutputStream fOut = new FileOutputStream(file);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

                myOutWriter.append(data);
                myOutWriter.close();

                fOut.flush();
                fOut.close();
            }
        } catch (IOException e) {
            Log.e(mTag, "FILE: " + fileName);
            Log.e(mTag, "ERROR: " + e.getLocalizedMessage());
        }
    }

    public static String getToday(String format) {
        // 2016년 3월 22일 (금)
        //Date now = new Date();
        //DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
        //String today = df.format(now);

        // 2016년 3월 22일 화요일 오후 10:45
        //String dateTime = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT).format(now);

        Calendar calendar = Calendar.getInstance();
        if (format == null) {
            format = "yyyy-MM-dd";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    /*
    public static boolean isSameDate(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The dates must not be null...");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameDate(cal1, cal2);
    }

    public static boolean isSameDate(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }
    */
}

