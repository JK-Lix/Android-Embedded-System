package com.android.bsl;


import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.model.*;

public class ChartActivity extends Activity implements OnClickListener {
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

    private DataService dataService;
    //    图表
    int type;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        dataService = new DataService(this.getFilesDir().toString());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.charts);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // task to run goes here
                renderChart((LineChartView) findViewById(R.id.line_chart));
            }
        };
        Timer timer = new Timer();
        long delay = 0;
        long intevalPeriod = 500;
        // schedules the task to be run in an interval
        timer.scheduleAtFixedRate(task, delay, intevalPeriod);

    }

    private void renderChart(LineChartView lineChart) {
        Map<String, Object> chartData = dataService.getHistoryAverageSeries(type);
//        设置X 轴的显示
        List<PointValue> mPointValues = (ArrayList<PointValue>) chartData.get("sensorValues");
        //    图表的每个点的显示
        List<AxisValue> mAxisXValues = (ArrayList<AxisValue>) chartData.get("axisValues");
//        折线数据
        Line line = new Line(mPointValues).setColor(Color.parseColor("#FFCD41"));  //折线的颜色（橙色）
        line.setHasPoints(false);
        line.setCubic(true);//曲线是否平滑，即是曲线还是折线
        List<Line> lines = new ArrayList<Line>();
        line.setHasLabelsOnlyForSelected(true);
        lines.add(line);
//        数据
        LineChartData data = new LineChartData();
        data.setLines(lines);

        //坐标轴
        Axis axisX = new Axis(); //X轴
        axisX.setHasTiltedLabels(true);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
        axisX.setTextColor(Color.WHITE);  //设置字体颜色
        axisX.setValues(mAxisXValues);//填充X轴的坐标名称
        data.setAxisXBottom(axisX); //x 轴在底部
        axisX.setHasLines(true); //x 轴分割线

        // Y轴是根据数据的大小自动设置Y轴上限(在下面我会给出固定Y轴数据个数的解决方案)
        Axis axisY = new Axis();  //Y轴
        String[] units = {"℃", "%", "lx","人"};
        axisY.setName(units[type]);//y轴标注
        axisY.setTextSize(15);//设置字体大小
        data.setAxisYLeft(axisY);  //Y轴设置在左边
        //Chart的效果

        lineChart.setContainerScrollEnabled(false, ContainerScrollType.HORIZONTAL);
        lineChart.setInteractive(true);
        lineChart.setLineChartData(data);

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


        mBtnBack.setOnClickListener(this);
        mBtnRightImg.setOnClickListener(this);
        mBtnRightText.setOnClickListener(this);
        mTvTitle.setOnClickListener(this);

        String[] title = {"温度实时曲线", "湿度实时曲线", "光照强度实时曲线","人数"};
        type = getIntent().getIntExtra("type", 0);
        setActionBarView(true, false, title[type]);

    }

    /**
     * @param backBtn      返回按钮是否可见
     * @param rightTextBtn 右边文本按钮时否可见
     * @param strTitle     设置中间的文本信息
     *                     //     * @param btnText      设置右边文本信息
     */
    public void setActionBarView(boolean backBtn, boolean rightTextBtn, String strTitle) {

        mTvTitle.setText(strTitle);
        //mBtnRightText.setText(btnText);

        if (rightTextBtn) {
            mBtnRightImg.setVisibility(View.VISIBLE);
        } else {
            mBtnRightImg.setVisibility(View.INVISIBLE);
        }

        if (backBtn) {
            mBtnBack.setVisibility(View.VISIBLE);
            mImgLeft.setVisibility(View.VISIBLE);
        } else {
            mBtnBack.setVisibility(View.INVISIBLE);
            mImgLeft.setVisibility(View.INVISIBLE);
        }

        if (rightTextBtn) {
            mBtnRightText.setVisibility(View.VISIBLE);
            mImgRight.setVisibility(View.VISIBLE);
        } else {
            mBtnRightText.setVisibility(View.INVISIBLE);
            mImgRight.setVisibility(View.INVISIBLE);
        }

        if (false) {
            mImgicon.setVisibility(View.INVISIBLE);
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
                break;
            case R.id.imgActionBarBack:
                System.gc();
                finish();
                break;
        }
    }
}
