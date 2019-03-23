package com.android.bsl;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.DecimalFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BSLActivity extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
    /**
     * 返回按钮
     */
    private ImageView mBtnBack = null;
    public static  String dir;

    public void getDir(){
        this.dir = this.getFilesDir().toString();
    }
    //    控制按钮
    private Button btnControl = null;
    //    逐客令
    private Button btnContro2 = null;
    /**
     * 最右边的按钮
     */
    private Button mBtnRightText = null;
    private ImageButton mBtnRightImg = null;


    /**
     * 标题
     */
    private TextView mTvTitle = null;
    /**
     * 右边按钮分割线
     */
    private ImageView mImgRight = null;
    /**
     * 左边按钮分割线
     */
    private ImageView mImgLeft = null;
    private ImageView mImgicon = null;
    private ListView listview;
    private GalleryFlow galleryflow;
    //    列表
    public List<NodeInfo> nodelist;

    private MyAdapter myAdapter;
    public ClientThread rxListenerThread;
    public static Handler mainHandler;
    private String[] scensString = new String[]
            {
                    "狗窝", "温测系统", "湿测系统", "光测系统", "不明来客"
            };
    private int[] sensorTypes = {0x1b, 0x2b, 0x13, 0x15, 0x11, 0x14};

    private TextView sceneView;
    public static boolean isConnected;
    //定义 By HVT
    private DataService dataService;
    private int currentModule;
    private Map<Integer, List<NodeInfo>> nodeInfoList;

    int i = 0;
    private float tem;
    private float shidu;
    private float guang;
    private float juli;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        init();

    }

    private void init() {
        // TODO Auto-generated method stub
        mBtnBack = (ImageView) findViewById(R.id.imgActionBarBack);
        mBtnRightImg = (ImageButton) findViewById(R.id.btnActionBarRightImg);
        mBtnRightText = (Button) findViewById(R.id.btnActionBarRightText);
        mTvTitle = (TextView) findViewById(R.id.tvActionBarTitle);
        mImgRight = (ImageView) findViewById(R.id.imgActionBarRight);
        mImgLeft = (ImageView) findViewById(R.id.imgActionBarLeft);
        mImgicon = (ImageView) findViewById(R.id.titleImgSiftIcon);
        sceneView = (TextView) findViewById(R.id.sceneName);
        listview = (ListView) findViewById(R.id.mylist);
//        点击控制按钮
        btnControl = (Button) findViewById(R.id.btnControl);
        btnContro2 = (Button) findViewById(R.id.btnContro2);
        btnControl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ctrlIntent = new Intent(BSLActivity.this, ControlInterface.class);
                ctrlIntent.putExtra("type", currentModule);
                startActivity(ctrlIntent);
            }
        });
        btnContro2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ctrlIntent = new Intent(BSLActivity.this, ControlInterface.class);
                ctrlIntent.putExtra("type", currentModule);
                startActivity(ctrlIntent);
            }
        });

        myAdapter = new MyAdapter();
        listview.setAdapter(myAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int type, long arg3) {
                if (currentModule == 0) {
                    Intent intent = new Intent(BSLActivity.this, ChartActivity.class);
                    intent.putExtra("type", type);
                    startActivity(intent);
                }
            }
        });
        rxListenerThread = new ClientThread(this);//建立客户端线程
        nodelist = rxListenerThread.nodelist;
        rxListenerThread.start();

        initMainHandler();

        dataService = new DataService(this.getFilesDir().toString());
        dataService.initDB();
        nodeInfoList = new HashMap<Integer, List<NodeInfo>>();

        mBtnBack.setOnClickListener(this);
        mBtnRightImg.setOnClickListener(this);
        mBtnRightText.setOnClickListener(this);
        mTvTitle.setOnClickListener(this);

        setActionBarView(false, true, "主界面");
        //页面菜单图标
        Integer[] images = {
                R.drawable.room_gallery_0, R.drawable.room_gallery_1, R.drawable.room_gallery_2, R.drawable.room_gallery_3, R.drawable.room_gallery_5,
        };
        ImageAdapter adapter = new ImageAdapter(this, images);
        adapter.createReflectedImages();// 创建倒影效果
        GalleryFlow galleryFlow = (GalleryFlow) this.findViewById(R.id.gallery);
        galleryFlow.setFadingEdgeLength(0);
        galleryFlow.setSpacing(-30); // ͼƬ֮��ļ��
        galleryFlow.setAdapter(adapter);
        galleryFlow.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        currentModule = arg2;
                        setActionBarView(false, true, scensString[arg2]);
                        myAdapter.notifyDataSetChanged();
                        sceneView.setText(scensString[arg2]);
                        View btnControlView = (Button) findViewById(R.id.btnControl);
                        View btnControlView2 = (Button) findViewById(R.id.btnContro2);
                        if (currentModule == 0) {

                            if(i == 0){
                                msg("欢迎登陆看门狗系统","祝您生活愉快");
                                i = 1;
                            }
                            btnControlView.setVisibility(View.INVISIBLE);
                            btnControlView2.setVisibility(View.INVISIBLE);
                        }else if(currentModule == 1){
                             if(tem > 10.0) {
                           // if(true){
                                msg("温度提示", "气温适宜，出行建议穿搭外套加衬衣" );
                            }else if(tem <=10.0){
                                 msg("温度提示","气温较低请注意保暖");
                             }
                            btnControlView.setVisibility(View.VISIBLE);
                            btnControlView2.setVisibility(View.INVISIBLE);
                        }else if(currentModule == 2){
                             if(shidu > 20.0) {
                            //if(true){
                                 msg("湿度提示","空气潮湿，请注意");
                            }else if(shidu <=10.0){

                                 msg("湿度提示", "空气干燥，出行请注意补水" );
                             }
                            btnControlView.setVisibility(View.VISIBLE);
                            btnControlView2.setVisibility(View.INVISIBLE);
                        }else if(currentModule == 3){
                             if(guang > 10.0) {
                            //if(true){
                                msg("光照提示", "光照较强，请注意紫外线" );
                            }else if(guang <=10.0){
                                 msg("光照提示","光照不足，请打开前院灯");
                             }
                            btnControlView.setVisibility(View.VISIBLE);
                            btnControlView2.setVisibility(View.INVISIBLE);
                        }
                        else if (currentModule == 4){
                            if(juli > 10.0) {
                                //if(true){
                                msg("防盗提示", "一切正常" );
                            }else if(juli <=10.0){
                                msg("防盗提示","有访客前来，请注意");
                            }
                            btnControlView.setVisibility(View.INVISIBLE);
                            btnControlView2.setVisibility(View.VISIBLE);
                        }


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {

                        // TODO Auto-generated method stub
                    }
                }
        );
    }

    public void msg(String title, String message){
        AlertDialog alertDialog1 = new AlertDialog.Builder(BSLActivity.this,AlertDialog.THEME_HOLO_LIGHT)
                .setTitle(title)//标题
                .setMessage(message)//内容
                .setIcon(R.mipmap.ic_launcher)//图标
                .create();
        alertDialog1.show();
    }

    /**
     * @param backBtn      返回按钮是否可见
     * @param rightTextBtn 右边文本按钮时否可见
     * @param strTitle     设置中间的文本信息
     *                     <p>
     *                     设置右边文本信息
     */
    public void setActionBarView(boolean backBtn, boolean rightTextBtn, String strTitle) {
        mTvTitle.setText(strTitle);
        // mBtnRightText.setText(btnText);

        if (rightTextBtn) {
            mBtnRightImg.setVisibility(View.VISIBLE);
        } else {
            mBtnRightImg.setVisibility(View.INVISIBLE);
        }

        if (backBtn) {
            mBtnBack.setVisibility(View.VISIBLE);
            mImgLeft.setVisibility(View.VISIBLE);
        } else {
            mBtnBack.setVisibility(View.VISIBLE);
            mImgLeft.setVisibility(View.INVISIBLE);
        }

        if (rightTextBtn) {
            mBtnRightText.setVisibility(View.VISIBLE);
            mImgRight.setVisibility(View.VISIBLE);
        } else {
            mBtnRightText.setVisibility(View.VISIBLE);
            mImgRight.setVisibility(View.INVISIBLE);
        }

        if (false) {
            mImgicon.setVisibility(View.VISIBLE);
        } else {
            mImgicon.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btnActionBarRightImg:
            case R.id.btnActionBarRightText:
                startActivity(new Intent(this, ChartActivity.class));
                break;
            case R.id.imgActionBarBack:
                break;
        }
    }

    void initMainHandler() {

        mainHandler = new Handler() {
            @Override
//             主线程消息处理中心
            public void handleMessage(Message msg) {

                // 接收子线程的消息
                switch (msg.what) {
                    case 0x1111:
//                        接收到数据
                        myAdapter.notifyDataSetChanged();
//                        更新数据库
                        dataService.updateSensorData(nodelist);
                        break;
                    case 0x1112:
                        Bundle bundle = msg.getData();
                        byte[] buffer = bundle.getByteArray("sendData");
                        OutputStream outputStream = rxListenerThread.getOutPutStream();
                            if(outputStream==null){
                                try {
                                    outputStream.write(buffer);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        break;
                    case 0x5555:
                        isConnected = true;
                        Toast.makeText(BSLActivity.this, "连接成功！", Toast.LENGTH_LONG).show();
                        sendBroadcast(new Intent("com.android.action.nettestfinished"));
                        break;
                    case 0x5556:
                        isConnected = false;
                        Toast.makeText(BSLActivity.this, "连接失败！", Toast.LENGTH_LONG).show();
                        sendBroadcast(new Intent("com.android.action.nettestfinished"));
                        finish();
                        break;
                    default:
                        break;
                }
            }

        };
    }


    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
//            概览界面 返回主菜单条目数 否则返回nodelist大小
            if (currentModule == 0) {
                return sensorTypes.length;
            } else if (currentModule == 4) {
                return 0;
            } else {
                return dataService.getCurrentSensorCountByType(nodelist, currentModule);
            }
        }


        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }

        //        设置每个条目如何显示
        @Override
        public View getView(int index, View view, ViewGroup viewGroup) {
            String[] titleList = new String[]{"", "温度传感器","湿度传感器", "光照传感器", "外人来袭","振动传感器","声音传感器"};
            String[] unitList = new String[]{"", "℃", "%","lx", "","",""};
            ViewHolder viewholder;
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.listitem, null);
                viewholder = new ViewHolder(view);
                view.setTag(viewholder);
            } else {
                viewholder = (ViewHolder) view.getTag();
            }
//            概览界面
            if (currentModule == 0) {
//                获取平均值 如果是温湿度 分别获取
                Map<String, Object> result = dataService.getAverage(nodelist, sensorTypes[index]);
                String tip = "", data = "";
                float value = 0;
                if ((Integer) result.get("status") == 1) {
                    tip = "正常";
                    value = (Float) result.get("value");
                    data = String.valueOf(new DecimalFormat(".00").format(value)) + unitList[index + 1];
                    if(sensorTypes[index] == 0x1b){
                        tem = value;
                    }else if(sensorTypes[index] == 0x2b){
                        shidu = value;
                    }else if(sensorTypes[index] == 0x13){
                        guang = value;
                    }else if(sensorTypes[index] == 0x15){
                        juli = value;
                    }


                } else {
                    tip = "未检测到数据";
                    data = "未检测到传感器";
                }
                viewholder.setData(
                        sensorTypes[index],
                        new String[]{"室外温度", "室外湿度", "室外光照", "外人来袭","振动情况","声音有无"}[index],
                        data,
                        tip,
                        dataService.judgeStatus(sensorTypes[index], value)
                );
//                控制界面
            } else if (currentModule ==4) {
                //NodeInfo node=nodelist.get(arg);
             //   Toast.makeText(BSLActivity.this, "控制界面", Toast.LENGTH_LONG).show();
            } else{
                NodeInfo node = dataService.getCurrentSensorsByType(nodelist, currentModule, index);
                viewholder.setData(
                        node.getType(),
                        titleList[currentModule] + " #" + index,
                        new DecimalFormat(".00").format(node.getValue()) + unitList[currentModule],
                        node.getId() == null ? "" : node.getId(),
                        dataService.judgeStatus(sensorTypes[currentModule - 1], node.getValue())
                );
                //if (node.getValue() > 10.0) {
               // if(true){
                    /*AlertDialog alertDialog1 = new AlertDialog.Builder(BSLActivity.this)
                            .setTitle("温度提示")//标题
                            .setMessage("气温高于10摄氏度，请爸爸加衣服")//内容
                            .setIcon(R.mipmap.ic_launcher)//图标
                            .create();
                    alertDialog1.show();*/
               // Toast.makeText(BSLActivity.this, "气温高于10摄氏度，请爸爸加衣服!", Toast.LENGTH_SHORT).show();
                //}
            }
            return view;
        }

        private class ViewHolder {
            ImageView imageview;
            TextView sensorNameView;
            TextView infoView;


            public ViewHolder(View view) {
                this.imageview = (ImageView) view.findViewById(R.id.sensor_icon);
                this.sensorNameView = (TextView) view.findViewById(R.id.sensor_name);
                this.infoView = (TextView) view.findViewById(R.id.sensor_info);
               // this.addressView = (TextView) view.findViewById(R.id.sensor_address);
            }

            public void setData(int sensorType, String title, String info, String extra, int status) {
                this.imageview.setImageResource(getIcon(sensorType));
                this.sensorNameView.setText(title);
                this.infoView.setText(info);
             //   this.addressView.setText(extra);
                if (status == 1) {
                    this.infoView.setTextColor(Color.rgb(255, 0, 0));
                   /* Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vb.vibrate(1000);*/
                } else {
                    this.infoView.setTextColor(Color.parseColor("#f2f2f2"));
                }
            }
        }
    }


    private int getIcon(int arg0) {
        switch (arg0) {
            case 0x01:

                return R.drawable.wendu;
            case 0x02:
            case 0x03:
                return R.drawable.device_default_lightsensor;
            case 0x04:
                return R.drawable.jiujing;
            case 0x05:
                return R.drawable.kongqi;
            case 0x06:
                return R.drawable.device_normal_smoke;
            case 0x07:
                return R.drawable.device_normal_pir;
            case 0x08:
                return R.drawable.device_shutter_mid;
            case 0x09:
                return R.drawable.device_temp;
            case 0x0a:
                return R.drawable.jiasudu;
            case 0x0b:
            case 0x1b:
                return R.drawable.device_temp;
            case 0x2b:
                return R.drawable.device_hum;
            case 0x0c:
                return R.drawable.tuoluo;
            case 0x0d:

                return R.drawable.deng;
            case 0x0e:
                return R.drawable.zhinengdianbiao;
            case 0x0f:
                return R.drawable.yuanchengyaokong;
            case 0x11:
                return R.drawable.shake;
            case 0x13:
                return R.drawable.light;
            case 0x14:
                return R.drawable.voice;
            case 0x15:
                return R.drawable.range;
            case 0x17:
                return R.drawable.jidianqi;


        }
        return R.drawable.ic_launcher;
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(BSLActivity.this).setTitle("退出确定").setMessage("是否现在退出？").setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                rxListenerThread.interrupt();

                Socket socket = rxListenerThread.getSocket();
                try {
                    socket.close();
                    //rxListenerThread.childHandler.sendEmptyMessage(0x01);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                dialog.dismiss();
                BSLActivity.this.finish();

            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        }).create().show();
    }
}