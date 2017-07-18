package com.example.administrator.car_control_by_bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements SensorEventListener{


    private SensorManager mSensorManager;
    private Sensor mSensor;
    Calendar mCalendar;

    public byte[] message = new byte[1];
    private Vibrator vibrator;

    private int ENABLE_BLUETOOTH = 2;



    OutputStream outputStream = null;
    private static final UUID MY_UUID_SECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");//蓝牙串口服务相关UUID
    private String blueAddress = "20:13:11:22:05:23";//蓝牙模块的MAC地址

    private TextView textX;
    private TextView textY;
    private TextView textZ;
    private TextView textUp;
    private TextView textLeft;
    private TextView textRgiht;
    private TextView textDown;
    private TextView textStop;
    private TextView Rssi;
    float[]  ORIENTATION;
    float[] magneticValues;
    float[] accelerometerValues;

    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mBluetoothDevice;
    BluetoothSocket bluetoothSocket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textX = (TextView) findViewById(R.id.textX);
        textY = (TextView) findViewById(R.id.textY);
        textZ = (TextView) findViewById(R.id.textZ);
        textUp = (TextView) findViewById(R.id.textUp);
        textLeft = (TextView) findViewById(R.id.textLeft);
        textRgiht = (TextView) findViewById(R.id.textRight);
        Rssi = (TextView) findViewById(R.id.ress);
        textDown = (TextView) findViewById(R.id.textDown);
        textStop = (TextView) findViewById(R.id.textStop);
        accelerometerValues = new float[3];//加速度参数
        magneticValues = new float[3];//磁场参数
        ORIENTATION=new float[3];//方向参数
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);//传感器初始化

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        //判断蓝牙是否开启
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "不支持蓝牙", Toast.LENGTH_LONG).show();
            finish();
        } else if (!mBluetoothAdapter.isEnabled()) {
            Log.d("true", "开始连接");
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, ENABLE_BLUETOOTH);
        }
        //指定蓝牙设备，建立远程蓝牙设备实例
        mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(blueAddress);
        thread.start();


    }




//    //发送数据演示
//    switch (v.getId()){
//        case R.id.imagebutton1:
//            message[0]= (byte) 0x41;
//            vibrator();
//            Toast.makeText(this,"前进",Toast.LENGTH_SHORT).show();
//            bluesend(message);
//            Log.d("cy08",""+message[0]);
//            break;

    //重力传感逻辑
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override//根据手机偏转的角度向蓝牙发送信息
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor == null) {
            return;
        }

        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            ORIENTATION = event.values.clone();


            /*if (y < 0 && z < 10) {
                textUp.setText("up");
                message[0]= (byte) 0x41;
                //vibrator();
                Toast.makeText(this,"前进",Toast.LENGTH_SHORT).show();
                bluesend(message);
                Log.d("cy08",""+message[0]);
            } else {
                textUp.setText("wait");
            }

            if (y > 0 && z < 10) {
                textDown.setText("down");
                message[0]= (byte) 0x42;
                //vibrator();
                Toast.makeText(this,"后退",Toast.LENGTH_SHORT).show();
                bluesend(message);
                Log.d("cy08",""+message[0]);
            } else {
                textDown.setText("wait");
            }

            if (x > 0 && z < 10) {
                textLeft.setText("left");
                message[0]= (byte) 0x44;
                //vibrator();
                Toast.makeText(this,"左转",Toast.LENGTH_SHORT).show();
                bluesend(message);
                Log.d("cy08",""+message[0]);
            } else {
                textLeft.setText("wait");
            }

            if (x < 0 && z < 10) {
                textRgiht.setText("right");
                message[0]= (byte) 0x43;
                //vibrator();
                Toast.makeText(this,"右转",Toast.LENGTH_SHORT).show();
                bluesend(message);
                Log.d("cy08",""+message[0]);
            } else {
                textRgiht.setText("wait");
            }

            if ((x < 5 && z > 5) || (y < 5 && z > 5)) {
                textStop.setText("stop");
                message[0]= (byte) 0x40;
                //vibrator();
                Toast.makeText(this,"停止",Toast.LENGTH_SHORT).show();
                bluesend(message);
                Log.d("cy08",""+message[0]);
            } else {
                textStop.setText("wait");
            }*/
        }



        int x = (int) ORIENTATION[0]/30;
        int y = (int) ORIENTATION[1]/30;
        int z = (int) ORIENTATION[2]/30;
        mCalendar = Calendar.getInstance();
        long stamp = mCalendar.getTimeInMillis();

        textX.setText(String.valueOf(x));
        textY.setText(String.valueOf(y));
        textZ.setText(String.valueOf(z));



    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        try{
            bluetoothSocket.close();

        }catch (IOException e){
            e.printStackTrace();
        }


    }

    @Override
    protected void onResume(){
        super.onResume();
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);// ORIENTATION
        Sensor accelerometerSensor = mSensorManager.getDefaultSensor(Sensor.
                TYPE_ACCELEROMETER);
        Sensor magneticSensor = mSensorManager.getDefaultSensor(Sensor.
                TYPE_MAGNETIC_FIELD);
        // 参数三，检测的精准度
        mSensorManager.registerListener(this, accelerometerSensor,
                SensorManager.SENSOR_DELAY_NORMAL);// //加速度传感器精度
        mSensorManager.registerListener(this, mSensor,
                SensorManager.SENSOR_DELAY_NORMAL);// //方向传感器精度
        mSensorManager.registerListener(this, magneticSensor,
                SensorManager.SENSOR_DELAY_NORMAL);//磁场传感器精度
        mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(blueAddress);
        /*try{
            bluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
            Log.d("true","开始连接");
            bluetoothSocket.connect();
            Log.d("true","完成连接");
        }catch (IOException e){
            e.printStackTrace();
        }*/

    }
    Thread thread=new Thread(){
        public void run(){
            try{
                bluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
                Log.d("true","开始连接");
                bluetoothSocket.connect();
                Log.d("true","完成连接");
            }catch (IOException e){
                e.printStackTrace();
            }
        }

    };
    public void bluesend(byte[] message){
        try{
            outputStream = bluetoothSocket.getOutputStream();
            Log.d("send", Arrays.toString(message));
            outputStream.write(message);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}

