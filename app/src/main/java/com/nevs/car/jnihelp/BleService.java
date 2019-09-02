package com.nevs.car.jnihelp;

import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.nevs.car.constant.Constant;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelpers;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;
import com.nevs.car.z_start.MyApp;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Administrator on 2017/5/21.
 *
 * https://blog.csdn.net/xj10160/article/details/73655112
 * https://blog.csdn.net/kgdwbb/article/details/58199846
 * https://blog.csdn.net/uu13567/article/details/78017421
 */

public class BleService extends Service {

    BluetoothGattCharacteristic mReadCharacteristic  ;
    BluetoothGattCharacteristic mWriteCharacteristic ;
    BluetoothGattCharacteristic mNotifyCharacteristic;

    public static final String WRITE ="com.nevs.bluetooth.wRITE";
    public static final String SER ="com.nevs.bluetooth.SER";
    public static final String TOAST ="com.nevs.bluetooth.TOAST";
    public static final String ACCEPT ="com.nevs.bluetooth.ACCEPT";
    public static final String ACCEPT21 ="com.nevs.bluetooth.ACCEPT21";
    public static final String BYTEACCEPT ="com.nevs.bluetooth.BYTEACCEPT";
    public static final String BYTEACCEPT22 ="com.nevs.bluetooth.BYTEACCEPT22";
    public static final String BYTEACCEPT2 ="com.nevs.bluetooth.BYTEACCEPT2";
    public static final String BYTEACCEPTSS ="com.nevs.bluetooth.BYTEACCEPTSS";
    public static final String BYTEACCEPTSTART ="com.nevs.bluetooth.BYTEACCEPTSTART";
    public static final String BYTESTARTJIE ="com.nevs.bluetooth.BYTESTARTJIE";
    public static final String BYTEBOOKINGSTART ="com.nevs.bluetooth.BYTEBOOKINGSTART";
    public static final String BYTESTARTJIEPIN ="com.nevs.bluetooth.BYTESTARTJIEPIN";
    public static final String ENTERPIN ="com.nevs.bluetooth.ENTERPIN";
    public static final String BYTEPINEND ="com.nevs.bluetooth.BYTEPINEND";
    public static final String BYTEPINEND2 ="com.nevs.bluetooth.BYTEPINEND2";
    public static final String BYTEUNLOCKF ="com.nevs.bluetooth.BYTEUNLOCKF";
    public static final String BYTEUNLOCKJ ="com.nevs.bluetooth.BYTEUNLOCKJ";
    public static final String BYTEUNLOCKJ2 ="com.nevs.bluetooth.BYTEUNLOCKJ2";
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;//蓝牙设备地址
    public  BluetoothGatt mBluetoothGatt ;
    //BluetoothDevice device=null;
    public int mConnectionState = 0;

    public BluetoothGattService bluetoothGattService;


    private static final int STATE_DISCONNECTED = 0;//蓝牙断开
    private static final int STATE_CONNECTING = 1;//正在为连接中
    private static final int STATE_CONNECTED = 2;//蓝牙连接

    public final static String ACTION_GATT_CONNECTED = "com.charon.www.NewBluetooth.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "com.charon.www.NewBluetooth.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.charon.www.NewBluetooth.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE = "com.charon.www.NewBluetooth.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA = "com.charon.www.NewBluetooth.EXTRA_DATA";
    public final static String READ_RSSI = "com.charon.www.NewBluetooth.READ_RSSI";
    public final static String SETMTU = "com.charon.www.NewBluetooth.SETMTU";
    public final static String SETMTUONSUC = "com.charon.www.NewBluetooth.SETMTUONSUC";
    public final static String NOTIFYRENZ = "com.charon.www.NewBluetooth.NOTIFYRENZ";
    public List<UUID> writeUuid = new ArrayList<>();
    public List<UUID> readUuid =new ArrayList<>();
    public List<UUID> notifyUuid =new ArrayList<>();

    private final static String TAG = BleService.class.getSimpleName();

    private final IBinder mBinder = new LocalBinder();

//    private MainControllActivity controlActivity = new MainControllActivity();
//    BluetoothGattCharacteristic alertLevel;

    List<String>  listappend=new ArrayList<>();
    List<String>  lists=new ArrayList<>();


    public static int step=1;
    private int mtuone = 0;
    private boolean isRunOnServicesDiscovered=false;

    public static LogBlueLin callback2;
    public static void setLog(LogBlueLin blueListener){
        callback2=blueListener;
    }
    public static void toLog(int i,String str){
        if(callback2!=null) {
            callback2.getLog(i,str);
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    public void close() {
//        if (mBluetoothGatt == null) {
//            return;
//        }
//        mBluetoothGatt.close();
//        mBluetoothGatt = null;

        if (mBluetoothGatt == null) {

            return;

        }

        MLog.e("mBluetoothGatt closed");

        mBluetoothDeviceAddress = null;

        disconnect();

        mBluetoothGatt.close();

        mBluetoothGatt = null;


    }
    public class LocalBinder extends Binder {
        public BleService getService() {
            return BleService.this;
        }
    }

    public boolean initialize() {//3

        // For API level 18 and above, get a reference to BluetoothAdapter
        // through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                MLog.e("不能初始化BluetoothManager.");
                return false;
            }
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            MLog.e("不能获取a BluetoothAdapter.");
            return false;
        }
        return true;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean connect(final String address) {//4
        if(mConnectionState==2||mConnectionState==STATE_CONNECTING){//1正在为连接中
            MLog.e("bleserve蓝牙处于连接状态无需连接");
            return true;
        }
        MLog.e( "连接:" + mBluetoothDeviceAddress);
        //controlActivity.invalidateOptionsMenu();
        if (mBluetoothAdapter == null || address == null) {
            MLog.e("BluetoothAdapter不能初始化 or 未知 address.");
            return false;
        }

        // 以前连接过的设备，重新连接. (��ǰ���ӵ��豸�� ������������)
        if (mBluetoothDeviceAddress != null
                && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            MLog.e("尝试使用现在的 mBluetoothGatt连接.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                showToast(STATE_CONNECTING);
                return true;
            } else {
                return false;
            }
        }

        BluetoothDevice device=null;
        try {
          //  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//　使用新API时，最好在调用方法前判断下系统版本，否则会出现意想不到的错误。
        device = mBluetoothAdapter
                    .getRemoteDevice(address);
            if (device == null) {
                MLog.e("设备没找到，不能连接");
                return false;
            }
        }catch (Exception e){
            MLog.e("地址异常连接异常："+e.toString());
            return  false;
        }

        // We want to directly connect to the device, so we are setting the
        // autoConnect
        // parameter to false.

        //获取BluetoothDevice对象后调用coonnectGatt()进行连接
        close();//MM
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback,2);//5这里改进成 List
        //这个方法需要三个参数：一个Context对象，自动连接（boolean值,表示只要BLE设备可用是否自动连接到它），和BluetoothGattCallback调用。

        MLog.e("Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        showToast(STATE_CONNECTING);
        //mBluetoothGatt.readRemoteRssi();
        return true;
    }

    public void disconnect() {
        if (mBluetoothAdapter == null) {
            MLog.e("BluetoothAdapter not initialized");
            return;
        }
        //controlActivity.invalidateOptionsMenu();
        mBluetoothGatt.disconnect();

        //mBluetoothGatt = null;
    }


    int i=0;
    int j=0;



/**
 *更改mtu
 * */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean setMTU(int mtu){
        MLog.e("setMTU "+mtu+"更改MTU");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            if(mtu>20){
                boolean ret =   mBluetoothGatt.requestMtu(mtu-3);
                MLog.e("requestMTU "+mtu+" ret="+ret);
                return ret;
            }
        }

        return false;
    }

    //VVVVVVVVV
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)//mtu值更改成功以后的回调
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status){
            MLog.e("onMtuChanged mtu="+mtu+",status="+status);
            final Intent intent = new Intent(SETMTU);
            // intent.putExtra("UUIDW",writeUuid.get(0).toString());
            intent.putExtra("MTU","MTU值修改成功回调:"+"onMtuChanged mtu="+mtu+",status="+status);

            sendBroadcast(intent);//广播
        }



        //5
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override//连接状态改变的回调A
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {

            String intentAction;
            MLog.e("status" + status);

            if (newState == BluetoothProfile.STATE_CONNECTED) {//当连接状态发生改变

                new SharedPHelper(MyApp.getInstance()).put(Constant.BLUECONSTATE,"1");//1为连接，0为断开

                toLog(101,"连接状态");

                mBluetoothGatt = gatt;

//                                if(mtuone==0) {
//                                    setMTU(135);//设置MTU
//                                    mtuone = 1;
//                                }
                intentAction = ACTION_GATT_CONNECTED;

                broadcastUpdate(intentAction);//6
                MLog.e("已连接GATT server");
                // Attempts to discover services after successful connection.

                showToast(STATE_CONNECTED);
                try {
                    Thread.sleep(1000);//1000
                    mConnectionState = STATE_CONNECTED;
                    MLog.e("Attempting to start service discovery:"
                            + mBluetoothGatt.discoverServices());
                    //重置重连状态0
                    new SharedPHelpers(MyApp.getInstance(),"rz"+new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).put(Constant.ISRECONINGSUC,"0");
                    //toLog(168,"168");

//                    mConnectionState = STATE_CONNECTED;
//                    while(!isRunOnServicesDiscovered){
//
//                            Thread.sleep(500);
//
//                        MLog.e("discoverServices");
//                        mBluetoothGatt.discoverServices();
//                        new SharedPHelpers(MyApp.getInstance(),"rz"+new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).put(Constant.ISRECONINGSUC,"0");
//
//                    }


//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            mConnectionState = STATE_CONNECTED;
//                            MLog.e("Attempting to start service discovery:"
//                                    + mBluetoothGatt.discoverServices());
//                            //重置重连状态0
//                            new SharedPHelpers(MyApp.getInstance(),"rz"+new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).put(Constant.ISRECONINGSUC,"0");
//                        }
//                    },1000);




                } catch (Exception e) {
                    e.printStackTrace();
                }
//                Log.e("tag", "Attempting to start service discovery:"
//                           + mBluetoothGatt.discoverServices());

               // gatt.discoverServices();
               // mBluetoothGatt.discoverServices();

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {//当设备无法连接
                new SharedPHelper(MyApp.getInstance()).put(Constant.BLUECONSTATE,"0");//1为连接，0为断开

                new SharedPHelpers(MyApp.getInstance(),"rerz"+new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).put(Constant.ISDISCON,"1");//1为断开过
                toLog(102,"连接断开");

                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                close(); // 防止出现status 133     cc
//                if (mBluetoothGatt != null) {
//                    mBluetoothGatt.close();
//                    device = null;
//                }
                MLog.e("Disconnected from GATT server.");
                broadcastUpdate(intentAction);   //发送广播

                showToast(STATE_DISCONNECTED);
            } else {//cc加上
                MLog.e("onConnectionStateChange received: " + status);

                intentAction = ACTION_GATT_DISCONNECTED;

                mConnectionState = STATE_DISCONNECTED;

                close(); // 防止出现status 133

                broadcastUpdate(intentAction);

                connect(mBluetoothDeviceAddress);
            }
        }


//        @Override
//        // 发现新服务端  //发现服务的回调 B
//        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
//            if (status == BluetoothGatt.GATT_SUCCESS) {
////cc                BluetoothGattService service = gatt.getService(UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb"));
////                List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
////                readC = service.getCharacteristic(UUID.fromString("0000fff6-0000-1000-8000-00805f9b34fb"));
////                writeC = service.getCharacteristic(UUID.fromString("0000fff7-0000-1000-8000-00805f9b34fb"));
////                enableNotifi(gatt,readC);
//
//
//                                List<BluetoothGattService> supportedGattServices = gatt.getServices();
//                MLog.e("服务");
//                if (gatt == null) {
//                    return;
//                }
//                //找到Server端的服务，也许有多个，也许只有一个，看项目需求定制
//                Log.e("tag", "找到了GATT服务");
//
//                for (int i = 0; i < supportedGattServices.size(); i++) {
//                    Log.e("tag", "GATT服务列表：" + supportedGattServices.get(i).getType());
//                        Log.e("tag", "找到了的UUID服务：" + supportedGattServices.get(i).getUuid().toString());
//
//
//                }
//                String uuid=supportedGattServices.get(0).getUuid().toString();
//
//                BluetoothGattService mBluetoothGattService = gatt.getService(UUID.fromString(uuid));
//                mReadCharacteristic = mBluetoothGattService.getCharacteristic(UUID.fromString("0000fee2-0000-1000-8000-00805ffff000"));
//                mWriteCharacteristic = mBluetoothGattService.getCharacteristic(UUID.fromString("0000fee2-0000-1000-8000-00805ffff000"));
//                enableNotifi(gatt,mReadCharacteristic);
//
//
//
//
//
//
//
////                List<BluetoothGattService> supportedGattServices = gatt.getServices();
////                MLog.e("服务");
////                if (gatt == null) {
////                    return;
////                }
////                //找到Server端的服务，也许有多个，也许只有一个，看项目需求定制
////                Log.e("tag", "找到了GATT服务");
////
////                for (int i = 0; i < supportedGattServices.size(); i++) {
////                    Log.e("tag", "GATT服务列表：" + supportedGattServices.get(i).getType());
////                        Log.e("tag", "找到了的UUID服务：" + supportedGattServices.get(i).getUuid().toString());
////                   // Log.e("tag", "找到了的UUID服务下c：" + supportedGattServices.get(i).getCharacteristics().get(i).getUuid());
////
////                }
////
////
////
//////00002a05-0000-1000-8000-00805f9b34fb         0000fee3-0000-1000-8000-00805ffff000
////                for (BluetoothGattService gattService : supportedGattServices) {
////                    List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
////                    for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
////                        int charaProp = gattCharacteristic.getProperties();
////                        Log.e("onServicesDisc中中中", " ：" + gattCharacteristic.getUuid());
////                        if("00002a05-0000-1000-8000-00805f9b34fb".equals(gattCharacteristic.getUuid().toString())){
////                          //  linkLossService=bluetoothGattService;
////                            alertLevel=gattCharacteristic;
////                            Log.e("TAG","遍历："+alertLevel.getUuid().toString());
////                        }
////
////                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
////                            Log.e("tag", "gattCharacteristic的UUID为:" + gattCharacteristic.getUuid());
////                            Log.e("tag", "gattCharacteristic的属性为:  可读");
////                            readUuid.add(gattCharacteristic.getUuid());
////
////                        }
////                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
////                            Log.e("tag", "gattCharacteristic的UUID为:" + gattCharacteristic.getUuid());
////                            Log.e("tag", "gattCharacteristic的属性为:  可写");
////                            writeUuid.add(gattCharacteristic.getUuid());
////                            //记录可写的UUID
////                            final Intent intent = new Intent(WRITE);
////                           // intent.putExtra("UUIDW",writeUuid.get(0).toString());
////                            intent.putExtra("UUIDW","00002a05-0000-1000-8000-00805f9b34fb");
////
////                            sendBroadcast(intent);//广播
////
////                        }
////                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
////                            Log.e("tag", "gattCharacteristic的UUID为:" + gattCharacteristic.getUuid() + gattCharacteristic);
////                            Log.e("tag", "gattCharacteristic的属性为:  具备通知属性");
////                            notifyUuid.add(gattCharacteristic.getUuid());
////                        }
////                    }
////
////                }
////                enableNotification(true,gatt,alertLevel);//必须要有，否则接收不到数据
////                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
//
//            } else {
//                Log.e("tag", "onServicesDiscovered received: " + status);
//            }
//        }


        @Override
        // 发现新服务端  //发现服务的回调 B
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == mBluetoothGatt.GATT_SUCCESS) {
                MLog.e("发现服务中");
                isRunOnServicesDiscovered=true;
//                if(mtuone==0) {
//                    setMTU(135);//设置MTU
//                    mtuone = 1;
//                }


// ww               List<BluetoothGattService> supportedGattServices = gatt.getServices();
//                MLog.e("服务"+supportedGattServices.size());
//                if (gatt == null) {
//                    return;
//                }
//                //找到Server端的服务，也许有多个，也许只有一个，看项目需求定制
//                Log.e("tag", "找到了GATT服务");
//
//                for (int i = 0; i < supportedGattServices.size(); i++) {
//                //CCTJ    Log.e("tag", "GATT服务列表：" + supportedGattServices.get(i).getType());
//                    //CCTJ    Log.e("tag", "找到了的UUID服务：" + supportedGattServices.get(i).getUuid().toString());
//                    //CCTJ   MLog.e("发现："+i);
//                    lists.add("UUID服务：" + supportedGattServices.get(i).getUuid().toString());
//                    final Intent intent = new Intent(SER);
//                    // intent.putExtra("UUIDW",writeUuid.get(0).toString());
//                    intent.putExtra("suuid", lists.get(i));
//
//                    sendBroadcast(intent);//广播
//
//
//
//                }
//
////                String uuid=supportedGattServices.get(0).getUuid().toString();
////
////                BluetoothGattService mBluetoothGattService = gatt.getService(UUID.fromString(uuid));
////                mReadCharacteristic = mBluetoothGattService.getCharacteristic(UUID.fromString("0000fee2-0000-1000-8000-00805ffff000"));
////                mWriteCharacteristic = mBluetoothGattService.getCharacteristic(UUID.fromString("0000fee2-0000-1000-8000-00805ffff000"));
////                enableNotifi(gatt,mReadCharacteristic);
//
//
//                for (BluetoothGattService gattService : supportedGattServices) {
//                    List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
//                 //CCTJ   MLog.e("onServicesDisc中中中"+" ：" + gattService.getUuid().toString());
//                    for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
//                        int charaProp = gattCharacteristic.getProperties();
//                        //CCTJ     MLog.e("onCharacteristic中中中"+" ：" + gattCharacteristic.getUuid());
////                        if("00002a05-0000-1000-8000-00805f9b34fb".equals(gattCharacteristic.getUuid().toString())){
////                          //  linkLossService=bluetoothGattService;
////                            alertLevel=gattCharacteristic;
////                            Log.e("TAG","遍历："+alertLevel.getUuid().toString());
////                        }
//
//                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
//                            //CCTJ    Log.e("tag", "gattCharacteristic的UUID为:" + gattCharacteristic.getUuid());
//                            //CCTJ    Log.e("tag", "gattCharacteristic的属性为:  可读");
//                            readUuid.add(gattCharacteristic.getUuid());
//                            listappend.add(gattCharacteristic.getUuid() + "<" + i + ">" + "可读");
//
//                        }
//                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
//                            //CCTJ  Log.e("tag", "gattCharacteristic的UUID为:" + gattCharacteristic.getUuid());
//                            //CCTJ  Log.e("tag", "gattCharacteristic的属性为:  可写");
//                            writeUuid.add(gattCharacteristic.getUuid());
//                            listappend.add(gattCharacteristic.getUuid() + "<" + i + ">" + "可写");
//
//
//                        }
//                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
//                            //CCTJ   Log.e("tag", "gattCharacteristic的UUID为:" + gattCharacteristic.getUuid() + gattCharacteristic);
//                           //CCTJ Log.e("tag", "gattCharacteristic的属性为:  具备通知属性");
//                            notifyUuid.add(gattCharacteristic.getUuid());
//                            listappend.add(gattCharacteristic.getUuid() + "<" + i + ">" + "通知");
//                        }
//                        i++;
//                        //记录可写的UUID 所有的
//                        final Intent intent = new Intent(WRITE);
//                        // intent.putExtra("UUIDW",writeUuid.get(0).toString());
//                        intent.putExtra("uuids", listappend.get(i));
//
//                        sendBroadcast(intent);//广播
//                    }
//
//                }
////                enableNotification(true,gatt,alertLevel);//必须要有，否则接收不到数据
////                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
//
//                //  intent.putStringArrayListExtra(name, value)

//                BluetoothGattService mBluetoothGattService = gatt.getService(BleUUID.UUID_SERVICE);
//                mReadCharacteristic = mBluetoothGattService.getCharacteristic(BleUUID.UUID_READ);
//                mWriteCharacteristic = mBluetoothGattService.getCharacteristic(BleUUID.UUID_WRITE);
//                enableNotifi(gatt, mReadCharacteristic);
//                getRssiVal();


                BluetoothGattService mBluetoothGattService = mBluetoothGatt.getService(BleUUID.UUID_SERVICE);
                mReadCharacteristic = mBluetoothGattService.getCharacteristic(BleUUID.UUID_READ);
                mWriteCharacteristic = mBluetoothGattService.getCharacteristic(BleUUID.UUID_WRITE);
                enableNotifi(mBluetoothGatt, mReadCharacteristic);
             //79   getRssiVal();

            } else {
                Log.e("tag", "onServicesDiscovered received: " + status);
            }
        }


        // 读写特性  //读操作的回调 C
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            System.out.println("onCharacteristicRead");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                MLog.e("读取成功" + characteristic.getValue());
                //  mBluetoothLeService.setCharacteristicNotification(characteristic, true);
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt,
                                      BluetoothGattDescriptor descriptor, int status) {

            System.out.println("onDescriptorWriteonDescriptorWrite = " + status
                    + ", descriptor =" + descriptor.getUuid().toString());
        }

        //数据返回的回调（此处接收BLE设备返回数据） D
        //如果对一个特性启用通知,当远程蓝牙设备特性发送变化，回调函数onCharacteristicChanged( ))被触发。
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {

            //所有的收到的消息都在这里接收
            MLog.e("shonCharacteristicChanged: " + StringUtils.bytesToHexString(characteristic.getValue()));

            MLog.e("收到数据："+StringUtils.bytesToHexString(characteristic.getValue()));
           //cztj Log.e("tag","收到数据："+ Base64.encode(characteristic.getValue()));


//            //分包处理数据
//            btBuffer.appendBuffer(characteristic.getValue());
//            while (true){
//                boolean ret = subPackageOnce(btBuffer);
//                if (false == ret) break;
          //  }



            MLog.e("缓存的第几步："+new SharedPHelpers(MyApp.getInstance(),"rz"+new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).get(Constant.BLUESTEP,"1")+"");


            //ffa5000000  异常排除
            if(characteristic.getValue()[2]==0){
                MLog.e("service收到心跳包");
            }else {
                switch (new SharedPHelpers(MyApp.getInstance(),"rz"+new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).get(Constant.BLUESTEP,"1")+""){
                    case "1"://认证发送
                        byte[] value = characteristic.getValue();
                        byte id=value[2];
                        MLog.e("收到通知ID："+id);
                       // MLog.e("16jinz:"+(byte)0xff+"  "+0xff+"  "+(byte)254);

                        final Intent intent = new Intent(ACCEPT);
                        intent.putExtra("AHex", StringUtils.bytesToHexString(characteristic.getValue()));
                        intent.putExtra("FRAMID",id+"");
                        sendBroadcast(intent);//广播

                        if(id==-1){
                            // step=2;
                            new SharedPHelpers(MyApp.getInstance(),"rz"+new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).put(Constant.BLUESTEP,"2");
                        }

                        break;
                    case "2"://认证接收
                        MLog.e("认证接收");
                        byte[] value2 = characteristic.getValue();

                        final Intent intent2 = new Intent(BYTEACCEPT);
                        intent2.putExtra("byteacept2",value2);
                        sendBroadcast(intent2);//广播
                        if(value2[2]==-1){
                            // step=3;
                            new SharedPHelpers(MyApp.getInstance(),"rz"+new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).put(Constant.BLUESTEP,"3");
                        }

                        break;

                    case "3":
                        MLog.e("认证接收后AES发送");
                        byte[] value3 = characteristic.getValue();

                        final Intent intent3 = new Intent(BYTEACCEPTSS);
                        intent3.putExtra("byteacept3",value3);
                        sendBroadcast(intent3);//广播

                        if(value3[2]==-1||value3[2]==0){//防止发送完成那边没有回ff.而是回的ffa5000000
                            // step=4;
                            new SharedPHelpers(MyApp.getInstance(),"rz"+new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).put(Constant.BLUESTEP,"4");

                        }
                        break;

                    case "4"://cccc
                        MLog.e("认证2接收");
                        byte[] value4 = characteristic.getValue();
                        final Intent intent4 = new Intent(BYTEACCEPT2);
                        intent4.putExtra("byteacept4",value4);
                        sendBroadcast(intent4);//广播
                        if(value4[2]==-1){
                            // step=5;
                            new SharedPHelpers(MyApp.getInstance(),"rz"+new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).put(Constant.BLUESTEP,"5");
                        }
                        break;

                    case "5":
                        MLog.e("startbooking发送");
                        byte[] value5 = characteristic.getValue();

                        final Intent intent5 = new Intent(BYTEACCEPTSTART);
                        intent5.putExtra("byteacept5",value5);
                        sendBroadcast(intent5);//广播
                        if(value5[2]==-1){
                            //  step=6;
                            new SharedPHelpers(MyApp.getInstance(),"rz"+new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).put(Constant.BLUESTEP,"6");
                        }

                        break;
                    case "6"://startbooking接收
                        MLog.e("startbooking接收");
                        byte[] value6 = characteristic.getValue();

                        final Intent intent6 = new Intent(BYTESTARTJIE);
                        intent6.putExtra("byteacept6",value6);
                        sendBroadcast(intent6);//广播
                        if(value6[2]==-1){
                            // step=7;
                            new SharedPHelpers(MyApp.getInstance(),"rz"+new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).put(Constant.BLUESTEP,"7");
                            MLog.e("step=7");
                        }

                        break;


                    case "7":
                        MLog.e("bookingstarted  OK发送");
                        byte[] value7 = characteristic.getValue();

                        final Intent intent7 = new Intent(BYTEBOOKINGSTART);
                        intent7.putExtra("byteacept7",value7);
                        sendBroadcast(intent7);//广播
                        if(value7[2]==-1){
                            //step=8;
                            new SharedPHelpers(MyApp.getInstance(),"rz"+new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).put(Constant.BLUESTEP,"8");
                            MLog.e("step=8");
                        }
                        break;
                    case "8"://ccccc
                        // if(new SharedPHelper(getApplicationContext()).get("step8","0").toString().equals("1")) {
                        MLog.e("解读PIN接收endpin");
                        byte[] value8 = characteristic.getValue();

                        final Intent intent8 = new Intent(BYTESTARTJIEPIN);
                        intent8.putExtra("byteacept8", value8);
                        sendBroadcast(intent8);//广播
                        if (value8[2] == -1) {
                            //step = 9;
                            new SharedPHelpers(MyApp.getInstance(),"rz"+new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).put(Constant.BLUESTEP,"9");
                            MLog.e("step=9");
                        }

                        //  }
                        break;
                    case "9"://输入PIN发送
                        MLog.e("bookingstarted发送");
                        byte[] value9 = characteristic.getValue();

                        final Intent intent9 = new Intent(ENTERPIN);
                        intent9.putExtra("byteacept9",value9);
                        sendBroadcast(intent9);//广播
                        if(value9[2]==-1){
                            // step=10;
                            new SharedPHelpers(MyApp.getInstance(),"rz"+new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).put(Constant.BLUESTEP,"10");
                            MLog.e("step=10");
                        }
                        break;
                    case "10"://ccccc
                        //   if(new SharedPHelper(getApplicationContext()).get("step8","0").toString().equals("1")) {
                        MLog.e("最后接收输入PIN成功后的返回消息1");
                        byte[] value10 = characteristic.getValue();

                        final Intent intent10 = new Intent(BYTEPINEND);
                        intent10.putExtra("byteacept10", value10);
                        sendBroadcast(intent10);//广播
                        if (value10[2] == -1) {
                            // step = 20;
                            new SharedPHelpers(MyApp.getInstance(),"rz"+new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).put(Constant.BLUESTEP,"20");
                            MLog.e("step=11");
                        }
                        // }
                        break;
                    case "20"://ccccc
                        //   if(new SharedPHelper(getApplicationContext()).get("step8","0").toString().equals("1")) {
                        MLog.e("最后接收输入PIN成功后的返回消息2");
                        byte[] value20 = characteristic.getValue();

                        final Intent intent20 = new Intent(BYTEPINEND2);
                        intent20.putExtra("byteacept20", value20);
                        sendBroadcast(intent20);//广播
                        if (value20[2] == -1) {
                            // step = 11;
                            new SharedPHelpers(MyApp.getInstance(),"rz"+new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).put(Constant.BLUESTEP,"11");
                            MLog.e("step=11");
                        }
                        // }
                        break;
                    case "11":
                        MLog.e("unlock发送");
                        byte[] value11 = characteristic.getValue();
                        final Intent intent11 = new Intent(BYTEUNLOCKF);
                        intent11.putExtra("byteacept11",value11);
                        sendBroadcast(intent11);//广播
                        if(value11[2]==-1){
                            // step=12;
                            new SharedPHelpers(MyApp.getInstance(),"rz"+new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).put(Constant.BLUESTEP,"12");
                            MLog.e("step=12");
                        }
                        break;
                    case "12"://ccccc
                        MLog.e("unlock1接收");
                        byte[] value12 = characteristic.getValue();
                        final Intent intent12 = new Intent(BYTEUNLOCKJ);
                        intent12.putExtra("byteacept12", value12);
                        sendBroadcast(intent12);//广播
                        if (value12[2] == -1) {
                            //step = 13;
                            new SharedPHelpers(MyApp.getInstance(),"rz"+new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).put(Constant.BLUESTEP,"13");
                            MLog.e("step=13");
                        }

                        break;
                    case "13"://ccccc
                        MLog.e("unlock2接收");
                        byte[] value13 = characteristic.getValue();
                        final Intent intent13 = new Intent(BYTEUNLOCKJ2);
                        intent13.putExtra("byteacept13", value13);
                        sendBroadcast(intent13);//广播
                        if (value13[2] == -1) {
                            // step = 14;
                            new SharedPHelpers(MyApp.getInstance(),"rz"+new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).put(Constant.BLUESTEP,"14");
                            MLog.e("step=14");
                        }

                        break;
                    case "14":
                        MLog.e("步骤14");
                        break;

                    case "21"://重连发送
                        byte[] value21 = characteristic.getValue();
                        byte id21=value21[2];
                        MLog.e("重连发送收到通知ID："+id21);
                        // MLog.e("16jinz:"+(byte)0xff+"  "+0xff+"  "+(byte)254);

                        final Intent intent21 = new Intent(ACCEPT21);
                        intent21.putExtra("AHex21", StringUtils.bytesToHexString(characteristic.getValue()));
                        intent21.putExtra("FRAMID21",id21+"");
                        sendBroadcast(intent21);//广播

                        if(id21==-1){
                            // step=2;
                            new SharedPHelpers(MyApp.getInstance(),"rz"+new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).put(Constant.BLUESTEP,"22");
                        }

                        break;
                    case "22"://重连接收
                        MLog.e("认证接收");
                        byte[] value22 = characteristic.getValue();

                        final Intent intent22 = new Intent(BYTEACCEPT22);
                        intent22.putExtra("byteacept22",value22);
                        sendBroadcast(intent22);//广播
                        if(value22[2]==-1){
                            // step=3;
                          //  new SharedPHelpers(MyApp.getInstance(),"rz"+new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).put(Constant.BLUESTEP,"3");
                        }

                        break;

                }

            }





        }

        //读取信号
        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            //MainControllActivity.rssi = rssi;
        //79    broadcastUpdate(READ_RSSI);
            MLog.e("信号："+rssi+"");
        }

        //写操作的回调  E
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
            MLog.e("--------write success----- status:" + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.e("TAG", "写入成功" + characteristic.getValue());
                Log.e("TAG", "写入数据:" + StringUtils.bytesToHexString(characteristic.getValue()));
              //cc  Log.e("tag","BASE64:"+Base64.encode(characteristic.getValue()));
                final Intent intent = new Intent(TOAST);
                intent.putExtra("Hex", StringUtils.bytesToHexString(characteristic.getValue()));
                sendBroadcast(intent);//广播
            }else if (status == BluetoothGatt.GATT_FAILURE){
                Log.e("onCharacteristicWrite中", "写入失败");
            }else if (status == BluetoothGatt.GATT_WRITE_NOT_PERMITTED){
                Log.e("onCharacteristicWrite中", "没权限");
            }
        }

    };




//        @Override
//        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
//            super.onMtuChanged(gatt, mtu, status);
//
//            if (status == BluetoothGatt.GATT_SUCCESS) {
//                this.supportedMTU = mtu;//local var to record MTU size
//            }
//
//        }
//        int supportedMTU = 0;

        private void enableNotifi(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic charactristicRead) {

            if (bluetoothGatt != null) {
                bluetoothGatt.setCharacteristicNotification(charactristicRead, true);
                MLog.e("通知开启：" + bluetoothGatt.setCharacteristicNotification(charactristicRead, true));
                BluetoothGattDescriptor descriptor = charactristicRead
                        .getDescriptor(UUID
                                .fromString("00002902-0000-1000-8000-00805f9b34fb"));
                // 数据改变通知的方式有两种方式，一种广播，一种消息通知
                byte[] vlue = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
                descriptor.setValue(vlue);
                bluetoothGatt.writeDescriptor(descriptor);
                final Intent intent = new Intent(NOTIFYRENZ);
                sendBroadcast(intent);//广播
            }
        }
//^^^^^^^^


        private void broadcastUpdate(final String action) {//9发送广播
            final Intent intent = new Intent(action);
            sendBroadcast(intent);//广播
        }

        public List<BluetoothGattService> getSupportedGattServices() {
            if (mBluetoothGatt == null)
                return null;

            return mBluetoothGatt.getServices();
        }

        public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {

            if (mBluetoothAdapter == null || mBluetoothGatt == null) {
                MLog.e("BluetoothAdapter not initialized");
                return;
            } else mBluetoothGatt.writeCharacteristic(characteristic);

        }

        private void enableNotification(boolean enable, BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if (gatt == null || characteristic == null)
                return; //这一步必须要有 否则收不到通知
            gatt.setCharacteristicNotification(characteristic, enable);
        }


        /**
         * 发送指令
         */
        public void sendOrder(final byte[] value) {
            if (mBluetoothGatt != null && mWriteCharacteristic != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(150);
                            MLog.e("1");
                            mWriteCharacteristic.setValue(value);
//                        Log.d(TAG, bytesToString(value));
                            mBluetoothGatt.writeCharacteristic(mWriteCharacteristic);
                            MLog.e("2");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            MLog.e("3");
                        }
                    }
                }).start();
            } else {
                MLog.e( "找不到对应的通道，找不到对应写入的特征");
            }
        }


//        public void sendOrders(final byte[] value, final int i) {
//            if (mBluetoothGatt != null && mWriteCharacteristic != null) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            Thread.sleep(150);
//                            mWriteCharacteristic.setValue(value);
//                            mBluetoothGatt.writeCharacteristic(mWriteCharacteristic);
//                            MLog.e("在写中"+i);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }).start();
//            } else {
//                Log.i("Ble", "找不到对应的通道，找不到对应写入的特征");
//            }
//
//
//        }

    public void sendOrders(final byte[] value, final int i) {
            try {
                if (mBluetoothGatt != null && mWriteCharacteristic != null) {
                    mWriteCharacteristic.setValue(value);
                    mBluetoothGatt.writeCharacteristic(mWriteCharacteristic);
                    MLog.e("在写中"+i+"---------------------------------第"+(i+1)+"包");

                    toLog(103,"在写中"+i+"---------------------------------第"+(i+1)+"包");

                } else {
                    MLog.e("找不到对应的通道，找不到对应写入的特征");
                }

            }catch (Exception e){
                MLog.e("发送异常："+e);
            }


    }




//    private boolean subPackageOnce(BluetoothBuffer buffer) {
//        if (null == buffer) return false;
//        if (buffer.getBufferSize() >= 14) {
//            byte[] rawBuffer =  buffer.getBuffer();
//            //求包长
//            if (isHead(rawBuffer)){
//                pkgSize = byteToInt(rawBuffer[2], rawBuffer[3]);
//            }else {
//                pkgSize = -1;
//                for (int i = 0; i < rawBuffer.length-1; ++i){
//                    if (rawBuffer[i] == -2 && rawBuffer[i+1] == 1){
//                        buffer.releaseFrontBuffer(i);
//                        return true;
//                    }
//                }
//                return false;
//            }
//            //剥离数据
//            if (pkgSize > 0 && pkgSize <= buffer.getBufferSize()) {
//                byte[] bufferData = buffer.getFrontBuffer(pkgSize);
//                long time = System.currentTimeMillis();
//                buffer.releaseFrontBuffer(pkgSize);
//                //在这处理数据
//                deal something。。。。。
//                return true;
//            }
//        }
//        return false;
//    }
//

    public boolean getRssiVal() {
        if (mBluetoothGatt == null)
            return false;
        return mBluetoothGatt.readRemoteRssi();

    }



    public void showToast(final int state){
        final boolean lang= MyUtils.getLanguage(MyApp.getInstance());
        Handler handlerThree=new Handler(Looper.getMainLooper());
        handlerThree.post(new Runnable(){
            public void run(){
                switch (state){
                    case 0:
                        if(lang){
                            ActivityUtil.showToast(MyApp.getInstance(),"蓝牙已断开");
                        }else {
                            ActivityUtil.showToast(MyApp.getInstance(),"Bluetooth is disconnected");
                        }

                        break;
                    case 1:
                        if(lang){
                            ActivityUtil.showLongToast(MyApp.getInstance(),"蓝牙正在连接中");
                        }else {
                            ActivityUtil.showLongToast(MyApp.getInstance(),"Bluetooth is connecting");
                        }

                        break;
                    case 2:
                      //  ActivityUtil.showToast(MyApp.getInstance(),getResources().getString(R.string.blueconnect));
                        break;
                }
            }
        });


    }

}
