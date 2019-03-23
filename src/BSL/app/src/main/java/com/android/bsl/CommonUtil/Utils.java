package com.android.bsl.CommonUtil;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.bsl.BSLActivity;
import com.android.bsl.ClientThread;
import com.android.bsl.DataService;
import com.android.bsl.NodeInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.PointValue;

public class Utils {
    public DataService dataService;
    private List<NodeInfo> nodelist;
    public ClientThread rxListenerThread;
    SQLiteDatabase db;
    String dir;

     public Utils() {
        this.dir = BSLActivity.dir;
        db = SQLiteDatabase.openOrCreateDatabase(dir + "/value.db", null);
    }


    public  Map<String, Object>  getHistoryAverageSeries(int type) {
        String sql = "SELECT * FROM sensordata WHERE value>0 AND type=" + type + " GROUP BY time ORDER BY time DESC LIMIT 150";
        Cursor cursor = db.rawQuery(sql, null);
        List<PointValue> sensorValues = new ArrayList<PointValue>();
        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        int index = 0;
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                float value = cursor.getFloat(1);
                axisValues.add(0, new AxisValue(cursor.getCount() - index - 1).setLabel(
                        new SimpleDateFormat("dd HH:mm:ss", Locale.getDefault()).format(new Date(cursor.getInt(3)))
                ));
                sensorValues.add(0, new PointValue(cursor.getCount() - index - 1, value));
                index++;
                cursor.moveToNext();
            }
        }
        cursor.close();
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("sensorValues", sensorValues);
        result.put("axisValues", axisValues);
        return result;
    }

    public float getSensorValue(){
       // nodelist = rxListenerThread.init();
       Map<String, Object> result = dataService.getAverage(nodelist, 0x1b);
        String tip = "", data = "";
        float value = 0;
        value = (Float) result.get("value");
        return value;
    }
}
