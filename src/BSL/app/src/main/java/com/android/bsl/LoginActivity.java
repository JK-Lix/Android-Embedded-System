package com.android.bsl;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener {

    private Button mBtnExit;
    private Button mBtnLogin;

    private EditText mEtName = null;
    private EditText mEtPwd = null;
    private SharedPreferences preferences;
    private String defaultIp;
    private String defaultPort;
    private SharedPreferences.Editor editor;
    private AlertDialog dialog;
    BroadcastReceiver br;
    public static Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork() // 这里可以替换为detectAll()
                // 就包括了磁盘读写和网络I/O
                .penaltyLog() // 打印logcat，当然也可以定位到dropbox，通过文件保存相应的log
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects() // 探测SQLite数据库操作
                .penaltyLog() // 打印logcat
                .penaltyDeath().build());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //mBtnRegister = (Button)findViewById(R.id.title_right_button);
        // MapLocation ml = new MapLocation(mApp, this);

        // isContactNetWork();

        preferences = getSharedPreferences("ip&port", MODE_PRIVATE);
        editor = preferences.edit();
        defaultIp = preferences.getString("ip", "192.168.191.3");
        defaultPort = preferences.getString("port", "6789");

        init();
        br = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                mBtnLogin.setEnabled(true);
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.android.action.nettestfinished");
        registerReceiver(br, intentFilter);
    }

    private void init() {
        // TODO Auto-generated method stub

        mEtName = (EditText) findViewById(R.id.etUserName);
        mEtName.setText(defaultIp);
        mEtPwd = (EditText) findViewById(R.id.etUserPwd);
        mEtPwd.setText(defaultPort);
        mBtnLogin = (Button) findViewById(R.id.btnOk);
        mBtnLogin.setOnClickListener(this);

        initActionBar();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btnOk:
                if (!mEtName.getText().toString().equals("")) {
                    if (!mEtPwd.getText().toString().equals("")) {
                        editor.putString("ip", mEtName.getText().toString());
                        editor.putString("port", mEtPwd.getText().toString());
                        editor.commit();
                        boolean isConnected = NetworkDetector.detect(LoginActivity.this);
                        if (!isConnected) {
                            Toast.makeText(LoginActivity.this, "网络不可用!", Toast.LENGTH_LONG).show();
                        } else {
                            try {
                                InetSocketAddress socketAddress = new InetSocketAddress(mEtName.getText().toString(), Integer.parseInt(mEtPwd.getText().toString()));
                                Socket socket = new Socket();
                                socket.connect(socketAddress, 5000);
                                //TODO 修改登录逻辑
                                if (socket.isConnected()) {
                                 // if(true){
                                    socket.close();
                                    Toast.makeText(LoginActivity.this, "登录成功!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this, BSLActivity.class));
                                } else {
                                    Toast.makeText(LoginActivity.this, "登录失败!", Toast.LENGTH_SHORT).show();
                                    System.out.print("连接未建立，登陆失败！");
                                    //测试修改  登陆失败也能上去
                                    socket.close();
                                    startActivity(new Intent(LoginActivity.this, BSLActivity.class));
                                }

                            } catch (NumberFormatException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                Toast.makeText(LoginActivity.this, "登录失败!", Toast.LENGTH_SHORT).show();
                            } catch (SocketTimeoutException e) {
                                Toast.makeText(LoginActivity.this, "登录失败!", Toast.LENGTH_SHORT).show();
                            } catch (UnknownHostException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                Toast.makeText(LoginActivity.this, "登录失败!", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                Toast.makeText(LoginActivity.this, "登录失败!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
                break;
            case R.id.btnActionBarRightImg:
            case R.id.btnActionBarRightText:
                break;
            case R.id.imgActionBarBack:
                break;
        }
    }

    // ----------------------------------------------------------------------------------
    /** Called when the activity is first created. */
    /**
     * 返回按钮
     */
    private ImageView mBtnBack = null;

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

    private void initActionBar() {
        // TODO Auto-generated method stub
        mBtnBack = (ImageView) findViewById(R.id.imgActionBarBack);
        mBtnRightImg = (ImageButton) findViewById(R.id.btnActionBarRightImg);
        mBtnRightText = (Button) findViewById(R.id.btnActionBarRightText);
        mTvTitle = (TextView) findViewById(R.id.tvActionBarTitle);
        mImgRight = (ImageView) findViewById(R.id.imgActionBarRight);
        mImgLeft = (ImageView) findViewById(R.id.imgActionBarLeft);
        mImgicon = (ImageView) findViewById(R.id.titleImgSiftIcon);
        View v = findViewById(R.id.bkgdPic);//找到你要设透明背景的layout 的id
        v.getBackground().setAlpha(180);

    }


    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub

        switch (id) {
            case 0x0001:
                dialog = new AlertDialog.Builder(LoginActivity.this).setTitle("连接成功").setMessage("网络连接正常!").create();
                return dialog;
            case 0x0002:
                dialog = new AlertDialog.Builder(LoginActivity.this).setTitle("连接失败").setMessage("抱歉，网络出错了！").create();
                return dialog;

            case 0x0003:
                dialog = new AlertDialog.Builder(LoginActivity.this).setTitle("当前网络不可用，是否去设置网络").setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                    }
                }).create();

                return dialog;
        }
        return null;
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
//        unregisterReceiver(br);
    }

    // -------------------------------------------------------------------------

    public static class NetworkDetector {
        public final static int NONE = 0;
        // 无网络
        public final static int WIFI = 1;
        // Wi-Fi
        public final static int MOBILE = 2;

        public static boolean detect(Activity act) {

            ConnectivityManager manager = (ConnectivityManager) act.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

            if (manager == null) {
                return false;
            }

            NetworkInfo networkinfo = manager.getActiveNetworkInfo();

            if (networkinfo == null || !networkinfo.isAvailable()) {
                return false;
            }

            return true;
        }

        public static int getNetworkState(Context context) {
            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            // 手机网络判断
            State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
            if (state == State.CONNECTED || state == State.CONNECTING) {
                return MOBILE;
            }
            // Wifi网络判断
            state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
            if (state == State.CONNECTED || state == State.CONNECTING) {
                return WIFI;
            }

            return NONE;
        }
    }
}
