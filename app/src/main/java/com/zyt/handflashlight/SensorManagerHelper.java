package com.zyt.handflashlight;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class SensorManagerHelper implements SensorEventListener {


    private final  int speedShreshold=3000;
    private final int updateIntervalTime=50;
    private SensorManager sensorManager;
    private Sensor sensor;
    private OnShakeListener onShakeListener;
    private Context context;
    private float lastX;
    private float lastY;
    private float lastZ;
    private long lastUpdateTime;

    public SensorManagerHelper(Context context){
        this.context=context;
        start();
    }

    /**
     * 开始检测
     */
    public void start(){
        //获取传感器管理器
        sensorManager =(SensorManager)context.getSystemService(Context.SENSOR_SERVICE);

        if(sensorManager!=null){
            //获取重力传感器
            sensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        //注册
        if(sensor!=null){
            sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_GAME);
        }
    }

    /**
     * 停止检测
     */
    public void stop(){
        sensorManager.unregisterListener(this);
    }

    /**
     * 摇晃监听接口
     */
    public interface OnShakeListener{
        void onShake();
    }




    /**
     * 设置重力感应监听器
     */
    public void setOnShakeListener(OnShakeListener listener){
        onShakeListener=listener;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * 重力感应器感应获得变化数据
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        //现在检测时间
        long currentUpdateTime=System.currentTimeMillis();
        //两次检测的间隔时间
        long timeInterval=currentUpdateTime-lastUpdateTime;
        //判断是否到达检测时间
        if(timeInterval<updateIntervalTime){
            return;
        }
        //现在时间变成最后检测的时间
        lastUpdateTime=currentUpdateTime;
        //获取，xyz坐标
        float x=event.values[0];
        float y=event.values[1];
        float z=event.values[2];
        //获得xyz坐标变化值
        float deltaX=x-lastX;
        float deltaY=y-lastY;
        float deltaZ=z-lastZ;
        //将现在坐标变成最后坐标
        lastX=x;
        lastY=y;
        lastZ=z;
        double speed=Math.sqrt(deltaX*deltaX+deltaY*deltaY+deltaZ*deltaZ)/timeInterval*10000;

        //达到速度阈值
        if(speed>=speedShreshold){
            onShakeListener.onShake();
        }else{

        }
    }



}
