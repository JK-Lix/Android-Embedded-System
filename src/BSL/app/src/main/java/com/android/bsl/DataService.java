package com.android.bsl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import org.w3c.dom.Node;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.PointValue;


public class DataService {
    private int[] sensorTypes = {0x1b, 0x2b, 0x13, 0x15, 0x11, 0x14};

    //    数据库
    SQLiteDatabase db;
    String dir;

    DataService(String _dir) {
//        数据库
        this.dir = _dir;
        //创建数据库连接
        db = SQLiteDatabase.openOrCreateDatabase(dir + "/value.db", null);
    }

    public void initDB() {
//        如果表不存在就创建
        String drop_value_table = "drop table if exists sensordata";
        db.execSQL(drop_value_table);
        String value_table = "create table if not exists sensordata" +
                "(" +
                "_id integer primary key autoincrement," +
                "value float, " +
                "type int, " +
                "time bigint" +
                ")";
        db.execSQL(value_table);
    }

    //    获取某类型传感器的平均值
    public Map<String, Object> getAverage(List<NodeInfo> nodelist, int type) {
        float sum = 0;
        int num = 0;
        for (NodeInfo node : nodelist) {
            if (node.getType() == type) {
                sum += node.getValue();
                ++num;
            }
        }
        Map<String, Object> result = new HashMap<String, Object>();
        if (num == 0) {
            result.put("status", 0);
        } else {
            result.put("status", 1);
            result.put("value", sum / num);
        }
        return result;
    }


    //    获取节点数量
    public int getCurrentSensorCountByType(List<NodeInfo> nodelist, int type) {
        int[] typelist = {0x00, 0x1b, 0x2b, 0x13};
        int count = 0;
        for (NodeInfo node : nodelist) {
            if (node.getType() == typelist[type]) {
                count++;
            }
        }
        return count;
    }

    //获取当前节点
    public NodeInfo getCurrentSensorsByType(List<NodeInfo> nodelist, int type, int index) {
        int[] typelist = {0x00,0x1b, 0x2b, 0x13, 0x15, 0x11,0x14};
        int count = 0;
        for (NodeInfo node : nodelist) {
            if (node.getType() == typelist[type]) {
                if (count == index) {
                    return node;
                }
                count++;
            }
        }
        return null;
    }


    private void insert(ContentValues value) {
        db.insert("sensordata", null, value);
    }

    //更新传感器数据
    public void updateSensorData(List<NodeInfo> nodeList) {
        ContentValues cv = new ContentValues();
        for (int type = 0; type < sensorTypes.length; ++type) {
            cv.put("value", (Float) this.getAverage(nodeList, sensorTypes[type]).get("value"));
            cv.put("type", type);
            cv.put("time", System.currentTimeMillis());
            this.insert(cv);
        }
    }

    //返回传感器的历史平均数据
    public Map<String, Object> getHistoryAverageSeries(int type) {
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

    public void sendControlMessage(byte deviceType, byte status, short value) {
        byte[] buffer = new byte[6];
//        协议头
        buffer[0] = buffer[1] = (byte) 0xff;
//        控制的设备类型
        buffer[2] = deviceType;
//        状态参数
        buffer[3] = status;
//        数值参数
        buffer[4] = (byte) ((value & 0xFF00) / 256);
        buffer[5] = (byte) (value & 0x00FF);
        Message message = new Message();
//        发消息
        Bundle bundle = new Bundle();
        bundle.putByteArray("sendData", buffer);
        message.what = 0x1112;
        message.setData(bundle);
        BSLActivity.mainHandler.sendMessage(message);
    }

    //    判断传感器状态
    public int judgeStatus(int sensorType, float value) {
        if (sensorType == 0x1b) {
            if (value > 10.5) {
                return 1;
            } else {
                return 0;
            }
        } else if (sensorType == 0x2b) {
            if (value > 80 || value < 20) {
                return 1;
            } else {
                return 0;
            }

        } else if (sensorType == 0x13) {
            if (value > 1500) {
                return 1;
            } else {
                return 0;
            }
        }else if (sensorType == 0x15) {
            if (value > 0) {
                return 1;
            } else {
                return 0;
            }
        } else if (sensorType == 0x11) {
            if (value > 0) {
                return 1;
            } else {
                return 0;
            }
        } else if (sensorType == 0x14) {
            if (value > 20) {
                return 1;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }
}
