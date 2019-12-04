package com.zyt.handflashlight;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {


    private ConstraintLayout constraintLayout;
    private TextView tvCountdowntimer;
    private TextView tvTimeTitle;
    private TextView progressTitle;
    private TextView tvValue;
    private SeekBar seekBar;
    private ImageView ivLight;
    private Button btnAbout;
    private Switch mSwitch,shakeSwitch,runColorSwitch;
    private int timeStemp;
    private CountDownTimer timer;
    private Vibrator vibrator;
    private Context context;
    private CameraManager manager;
    private Camera camera=null;
    private boolean isOpenBd =false;
    private boolean isRandColor =false;
    private boolean isShake =false;
    private Camera.Parameters parameters;
    private int timeProgress=50;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        setContentView(R.layout.activity_main);
        initView();
        eventListener();
        getSharePreference();

    }


    private void initView(){
        constraintLayout=findViewById(R.id.ConstraintLayout);
        tvCountdowntimer=findViewById(R.id.tv_time);
        tvTimeTitle=findViewById(R.id.tv_timeTitle);
        progressTitle=findViewById(R.id.tv_process);
        tvValue=findViewById(R.id.tv_value);
        ivLight=findViewById(R.id.iv_light);
        btnAbout=findViewById(R.id.bt_info);
        seekBar=findViewById(R.id.sb_num);
        mSwitch=findViewById(R.id.sw_button);
        shakeSwitch=findViewById(R.id.sw_ShakeButton);
        runColorSwitch=findViewById(R.id.sw_RunColorButton);
        progressTitle.setVisibility(View.INVISIBLE);
        seekBar.setVisibility(View.INVISIBLE);
        tvValue.setVisibility(View.INVISIBLE);

        vibrator=(Vibrator)this.getSystemService(this.VIBRATOR_SERVICE);
    }

    private void eventListener(){


        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,AboutActivity.class);
                startActivity(intent);
            }
        });


        SensorManagerHelper sensorManagerHelper=new SensorManagerHelper(this);
        sensorManagerHelper.setOnShakeListener(new SensorManagerHelper.OnShakeListener() {
            @Override
            public void onShake() {
                if(isOpenBd) {
                    timeStemp=timeProgress;
                    getCountDownTime();
                    openFlash();
                }else {
                    timeStemp+=500;
                    getCountDownTime();
                    if(timeStemp==500) {
                        openFlash();
                        ivLight.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this,R.color.yellow));
                    }
                }

            }
        });


        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //Toast.makeText(MainActivity.this,"蹦迪模式开启",Toast.LENGTH_SHORT).show();
                    constraintLayout.setBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.colorAccent));
                    isOpenBd =true;
                    closeFlash();
                    runColorSwitch.setVisibility(View.VISIBLE);
                    tvTimeTitle.setVisibility(View.INVISIBLE);
                    tvCountdowntimer.setVisibility(View.INVISIBLE);
                    progressTitle.setVisibility(View.VISIBLE);
                    seekBar.setVisibility(View.VISIBLE);
                    tvValue.setVisibility(View.VISIBLE);
                    // getSharePreference();
                }else {
                    //Toast.makeText(MainActivity.this,"蹦迪模式关闭",Toast.LENGTH_SHORT).show();
                    constraintLayout.setBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.colorPrimary));
                    isOpenBd =false;
                    tvTimeTitle.setVisibility(View.VISIBLE);
                    tvCountdowntimer.setVisibility(View.VISIBLE);
                    progressTitle.setVisibility(View.INVISIBLE);
                    seekBar.setVisibility(View.INVISIBLE);
                    tvValue.setVisibility(View.INVISIBLE);
                    runColorSwitch.setVisibility(View.GONE);
                }
                timeCancel();
            }


        });


        runColorSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    isRandColor=true;
                }else {
                    isRandColor=false;
                }
            }
        });


        shakeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    isShake=true;
                }else {
                    isShake=false;
                }
                setSharePreference();
            }
        });



        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                timeProgress=progress;
                tvValue.setText(Integer.toString(progress));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setSharePreference();
            }
        });
    }


    private void timeCancel(){
        if(timer!=null){
            timer.cancel();
            timeStemp=0;
            tvCountdowntimer.setText("0:0:0");
        }

    }



    private void getCountDownTime(){
       if (timer!=null) {
           timer.cancel();
       }
        timer=new CountDownTimer(timeStemp,1000) {

            @Override
            public void onTick(long l) {
                long day = l / (1000 * 24 * 60 * 60); //单位天
                long hour = (l - day * (1000 * 24 * 60 * 60)) / (1000 * 60 * 60); //单位时
                long minute = (l - day * (1000 * 24 * 60 * 60) - hour * (1000 * 60 * 60)) / (1000 * 60); //单位分
                long second = (l - day * (1000 * 24 * 60 * 60) - hour * (1000 * 60 * 60) - minute * (1000 * 60)) / 1000;//单位秒
                tvCountdowntimer.setText(hour+":"+minute+":"+second);
            }
            @Override
            public void onFinish() {
                closeFlash();
                timeCancel();
            }
        };
        timer.start();
    }

    /**
     * 开启闪光灯
     */
    private void openFlash() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                manager = (CameraManager) this.getSystemService(Context.CAMERA_SERVICE);
                if (manager != null) {
                    manager.setTorchMode("0", true);
                }

            } else{
                camera = Camera.open();
                Camera.Parameters parameters = camera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(parameters);
                //camera.startPreview();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        if(isRandColor)
        {
            String r,g,b;
            Random random = new Random();
            r = Integer.toHexString(random.nextInt(256)).toUpperCase();
            g = Integer.toHexString(random.nextInt(256)).toUpperCase();
            b = Integer.toHexString(random.nextInt(256)).toUpperCase();

            r = r.length()==1 ? "0" + r : r ;
            g = g.length()==1 ? "0" + g : g ;
            b = b.length()==1 ? "0" + b : b ;

            constraintLayout.setBackgroundColor(Color.parseColor("#"+r+g+b));

        }

    }

    /**
     * 关闭闪光灯
     */
    private void closeFlash() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                if (manager == null) {
                    return;
                }
                manager.setTorchMode("0", false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (camera == null) {
                return;
            }
            parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(parameters);
            camera.stopPreview();
            camera.release();
        }
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            ivLight.setColorFilter(Color.WHITE);

        } else {
            ivLight.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this,R.color.white));
            ivLight.setColorFilter(Color.WHITE);
        }
        if(isShake)
        {
            camera = null;
            Vibrator vibrator = (Vibrator)this.getSystemService(this.VIBRATOR_SERVICE);
            vibrator.vibrate(100);
        }else {

        }

    }



    @Override
    public void onBackPressed() {
     super.onBackPressed();
        closeFlash();
        finish();
        System.exit(0);
    }


    private void setSharePreference(){
        SharedPreferences sp = getSharedPreferences("text", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();//获取编辑器
        editor.putInt("progressValue",timeProgress);
        editor.putBoolean("Shake",isShake);
        editor.apply();
    }

    private void getSharePreference()
    {
        SharedPreferences sp = getSharedPreferences("text", Context.MODE_PRIVATE);

            timeProgress=sp.getInt("progressValue",50);
            isShake=sp.getBoolean("Shake",false);
            seekBar.setProgress(timeProgress);
            shakeSwitch.setChecked(isShake);
            tvValue.setText(Integer.toString(timeProgress));
    }


}
