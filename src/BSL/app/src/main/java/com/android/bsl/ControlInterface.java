package com.android.bsl;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.bsl.CommonUtil.Utils;

public class ControlInterface extends Activity implements SeekBar.OnSeekBarChangeListener {
    public static NodeInfo nodeinfo;
    private int sensorType;
    private boolean isbtn1Checked, isbtn2Checked, isbtn3Checked, isbtn4Checked;
    private int currentProcess;
    private TextView titleView;
    private Timer timer;
    public static Handler handler;
    private View view1, view2;
    RadioGroup mada_radioGroup;
    RadioButton mada_rb1;
    RadioButton mada_bb2;
    SeekBar mada_seekBar;
    Button mada_button;
    private ImageView io_deng1;

    private ToggleButton io_btn1;

    private Button io_sureBtn;
    private Button io_cancelBtn;
    private static final String TAG = "ControlInterface";
    private boolean[] btnCheckedState = new boolean[4];
    private boolean[] btnRecordState = new boolean[4];
    private byte lastDengState = -1;
    private boolean initFinished;
    private int mada_sudu;
    private byte dengState = 0;
    public static String currentUiName = "";
    private Timer timer2;

    private DataService dataService;
    private ToggleButton btnSwitchFan;
    private ToggleButton btnSwitchTiao;
    private SeekBar seekBarWindow;
    private SeekBar seekBarWatering;
    private ImageView imgActionBarBack;

    private List<NodeInfo> node;
    public ClientThread rxListener;
    public float tem;
    //public static Handler mainHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.control_panel);
        dataService = new DataService(this.getFilesDir().toString());
        dataService.sendControlMessage((byte) 0, (byte) 0, (short) 1000);
        findViewById(R.id.imgActionBarBack).setVisibility(View.INVISIBLE);
        imgActionBarBack = (ImageView) findViewById(R.id.imgActionBarBack);
        imgActionBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        int type = getIntent().getIntExtra("type", 0);
//        title设置
        String[] title = {"", "温度控制面板", "湿度控制面板", "光照控制", "逐客令"};
        ((TextView) findViewById(R.id.tvActionBarTitle)).setText(title[type]);
//        根据类型初始化控制器
        LinearLayout cp_container = (LinearLayout) findViewById(R.id.cp_container);
        if (type == 1) {
            tempure(cp_container, type);
        } else if (type == 2) {
            addFan(cp_container);
        } else if (type == 3) {
            shidu(cp_container);
        } else if (type == 4) {
            fuckOff(cp_container);
            open(cp_container, type);
        }
    }
    public class PlaySound  {
    }
    private  PlaySound instance = null;  //单例

    public  PlaySound getInstance(){
        if (instance == null) {
            instance = new PlaySound();
        }
        return instance;
    }

    public void PlayClickSound01(Context context) {
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.music);
        mediaPlayer.start();
    }
    //  添加逐客令
    private void fuckOff(LinearLayout cp_container) {
        PlayClickSound01(this);

    }
    //    添加温度提示系统
    /**
     * 参数说明：
     * 1 ： ca_containter: 页面
     * 2 :  type 当前显示的页面序号
     */
    private int[] sensorTypes = {0x1b, 0x2b, 0x13, 0x15};
    //开门
    public void open(LinearLayout cp_container, int type){
        View watering = getLayoutInflater().inflate(R.layout.ctrl_watering, null);
        seekBarWatering = (SeekBar) watering.findViewById(R.id.seekBarWatering);
        seekBarWatering.setOnSeekBarChangeListener(this);
        cp_container.addView(watering);
    }

//温度提示信息 空调
    private void tempure(LinearLayout cp_container, int type) {

        View tiao = getLayoutInflater().inflate(R.layout.ctrl_tiao, null);
        btnSwitchTiao = (ToggleButton) (tiao.findViewById(R.id.btnSwitchTiao));
        btnSwitchTiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!btnSwitchTiao.isChecked()) {
                    dataService.sendControlMessage((byte) 0, (byte) 0, (short) 0);
                } else {
                    dataService.sendControlMessage((byte) 0, (byte) 1, (short) 0);
                }
            }
        });
        cp_container.addView(tiao);
        //得到对应的传感器的数据
        /**
         * 参数说明： value 传感器预警阈值
         *返回值： 1：异常
         *         0:正常
         * */
        //float value = 0;
        //异常
       // if (dataService.judgeStatus(0x1b, value) == 1) {
           // Utils utis = new Utils();
           /* rxListenerThread = new ClientThread(this);//建立客户端线程
            nodelist = rxListenerThread.nodelist;
            rxListenerThread.start();
            BSLActivity bsl = new BSLActivity();
            bsl.initMainHandler();

            dataService = new DataService(this.getFilesDir().toString());
            dataService.initDB();*/
           // nodeInfoList = new HashMap<Integer, List<NodeInfo>>();
            /*BSLActivity bsl = new BSLActivity();
            node = bsl.nodelist;
            rxListener = bsl.rxListenerThread;
            node = rxListener.nodelist;
            rxListener.start();
            bsl.initMainHandler();
            dataService = new DataService(this.getFilesDir().toString());
            dataService.initDB();
            Map<String, Object> result = dataService.getAverage(node, sensorTypes[1]);
            value = (Float) result.get("value");
            NodeInfo nodel = dataService.getCurrentSensorsByType(node, 1, 0x1b);
            value = nodel.getValue();*/
           // data = String.valueOf(new DecimalFormat(".00").format(value)) + unitList[index + 1];
            //小于 10 C
           /* Iterator it = res.entrySet().iterator() ;
            float temperature = 0;
            while (it.hasNext())
            {
                Map.Entry entry = (Map.Entry) it.next() ;
                if(entry.getKey().equals("sensorValues")){
                    temperature = Float.parseFloat(entry.getValue().toString());
                }

            }

            System.out.print("99999999999999");*/
            //int temperature = res.get("sensorValues");
           /* if (value > 10.0) {
              //  msgDailog("气温小于10 C 请爸爸加衣服");
                AlertDialog alertDialog1 = new AlertDialog.Builder(this)
                        .setTitle("温度提示")//标题
                        .setMessage("气温高于10摄氏度，请爸爸加衣服")//内容
                        .setIcon(R.mipmap.ic_launcher)//图标
                        .create();
                alertDialog1.show();
                }*/
       // }
        //正常
       AlertDialog alertDialog1 = new AlertDialog.Builder(this)
                .setTitle("提示")//标题
                .setMessage("气温适宜，不建议打开空调")//内容
                .setIcon(R.mipmap.ic_launcher)//图标
                .create();
        alertDialog1.show();
    }

    //   灯
    private void shidu(LinearLayout cp_container) {
        View window = getLayoutInflater().inflate(R.layout.ctrl_window, null);
        seekBarWindow = (SeekBar) window.findViewById(R.id.seekBarWindow);
        seekBarWindow.setOnSeekBarChangeListener(this);
        cp_container.addView(window);
        AlertDialog alertDialog1 = new AlertDialog.Builder(this)
                .setTitle("光照提示")//标题
                .setMessage("光照充分，不必开灯")//内容
                .setIcon(R.mipmap.ic_launcher)//图标
                .create();
        alertDialog1.show();
    }
    //添加湿度提示信息 加湿器
    private void addFan(LinearLayout cp_container) {
        View fan = getLayoutInflater().inflate(R.layout.ctrl_fan, null);
        btnSwitchFan = (ToggleButton) (fan.findViewById(R.id.btnSwitchFan));
        btnSwitchFan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!btnSwitchFan.isChecked()) {
                    dataService.sendControlMessage((byte) 0, (byte) 0, (short) 0);
                } else {
                    dataService.sendControlMessage((byte) 0, (byte) 1, (short) 0);
                }
            }
        });
        cp_container.addView(fan);
        AlertDialog alertDialog1 = new AlertDialog.Builder(this)
                .setTitle("湿度提示")//标题
                .setMessage("空气干燥，建议打开加湿器")//内容
                .setIcon(R.mipmap.ic_launcher)//图标
                .create();
        alertDialog1.show();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int progress = seekBar.getProgress();
        if (progress != 0) {
            switch (seekBar.getId()) {
                case R.id.seekBarWatering: {
                    int value = getValue(progress);
                    dataService.sendControlMessage((byte) 1, (byte) 1, (short) value);
                    break;
                }
                case R.id.seekBarWindow: {
                    int value = getValue(progress);
                    dataService.sendControlMessage((byte) 2, (byte) 1, (short) value);
                    break;
                }
                default:
                    break;
            }
        }
    }

    private int getValue(int raw) {
        int result = raw < 8 ? 8 : raw;
        result = result * 100 / 2;
        return result;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }
}
