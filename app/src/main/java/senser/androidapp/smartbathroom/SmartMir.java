package senser.androidapp.smartbathroom;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SmartMir extends BaseActivity {

    Adapter adapter;
    ViewPager viewPager;

    // 사용법, 고객센터, 로그아웃 버튼
    ImageButton explanationImgBtn, customCenterImgBtn, logoutImgBtn, returnBtn, bluetoothBtn;

    private static final String DEVICE_NAME = "DEVICE_NAME";
    private static final String LOG = "LOG";

    // Подсветка crc
    private static final String CRC_OK = "#FFFF00";
    private static final String CRC_BAD = "#FF0000";

    private static final SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm:ss.SSS");

    private static String MSG_NOT_CONNECTED;
    private static String MSG_CONNECTING;
    private static String MSG_CONNECTED;

    private static DeviceConnector connector;
    public static BluetoothResponseHandler mHandler;

    private StringBuilder logHtml;
    private TextView logTextView;

    private MessageTimer mMessageTimer; // 참조변수

    // Настройки приложения
    private boolean hexMode, checkSum, needClean;
    private boolean show_timings, show_direction;
    private String command_ending;
    private String deviceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_mir);

        viewPager = (ViewPager) findViewById(R.id.view);
        adapter = new Adapter(this);
        viewPager.setAdapter(adapter);

        explanationImgBtn = (ImageButton) findViewById(R.id.explanationImgBtn);
        explanationImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SmartMir.this, Explanation.class);
                startActivity(intent);
            }
        });

        customCenterImgBtn = (ImageButton) findViewById(R.id.customCenterImgBtn);
        customCenterImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_customCenter(v);
            }
        });

        logoutImgBtn = (ImageButton) findViewById(R.id.logoutImgBtn);
        logoutImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_logout(v);
            }
        });

        returnBtn = (ImageButton) findViewById(R.id.returnBtn);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SmartMir.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        bluetoothBtn = (ImageButton) findViewById(R.id.bluetoothBtn);
        bluetoothBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SmartMir.super.isAdapterReady()) {
                    if (isConnected()) stopConnection();
                    else startDeviceListActivity();
                } else {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
            }
        });

        if (mHandler == null) mHandler = new BluetoothResponseHandler(this);
        else mHandler.setTarget(this);

        MSG_NOT_CONNECTED = getString(R.string.msg_not_connected);
        MSG_CONNECTING = getString(R.string.msg_connecting);
        MSG_CONNECTED = getString(R.string.msg_connected);

        mMessageTimer = MessageTimer.getInstance();
        mMessageTimer.init(this);

     /*   MSG_NOT_CONNECTED = getString(R.string.msg_not_connected);
        MSG_CONNECTING = getString(R.string.msg_connecting);
        MSG_CONNECTED = getString(R.string.msg_connected);

        this.logHtml = new StringBuilder();
        if (savedInstanceState != null) this.logHtml.append(savedInstanceState.getString(LOG));*/

    }

    // ==========================================================================


    /**
     * Проверка готовности соединения
     */
    private boolean isConnected() {
        return (connector != null) && (connector.getState() == DeviceConnector.STATE_CONNECTED);
    }


    /**
     * Разорвать соединение
     */
    private void stopConnection() {
        if (connector != null) {
            connector.stop();
            connector = null;
            deviceName = null;
        }
    }
    // ==========================================================================


    /**
     * Список устройств для подключения
     */
    private void startDeviceListActivity() {
        stopConnection();
        Intent serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }
    // ============================================================================


    /**
     * Обработка аппаратной кнопки "Поиск"
     *
     * @return
     */
    @Override
    public boolean onSearchRequested() {
        if (super.isAdapterReady()) startDeviceListActivity();
        return false;
    }
    // ==========================================================================

    @Override
    public void onStart() {
        super.onStart();

        // hex mode
        final String mode = Utils.getPrefence(this, getString(R.string.pref_commands_mode));
        this.hexMode = "HEX".equals(mode);

        // checksum
        final String checkSum = Utils.getPrefence(this, getString(R.string.pref_checksum_mode));
        this.checkSum = "Modulo 256".equals(checkSum);

        // Окончание строки
        this.command_ending = getCommandEnding();

        // Формат отображения лога команд
        this.show_timings = Utils.getBooleanPrefence(this, getString(R.string.pref_log_timing));
        this.show_direction = Utils.getBooleanPrefence(this, getString(R.string.pref_log_direction));
        this.needClean = Utils.getBooleanPrefence(this, getString(R.string.pref_need_clean));
    }
    // ============================================================================

    /**
     * Получить из настроек признак окончания команды
     */
    private String getCommandEnding() {
        String result = Utils.getPrefence(this, getString(R.string.pref_commands_ending));
        if (result.equals("\\r\\n")) result = "\r\n";
        else if (result.equals("\\n")) result = "\n";
        else if (result.equals("\\r")) result = "\r";
        else result = "";
        return result;
    }
    // ============================================================================


    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    BluetoothDevice device = btAdapter.getRemoteDevice(address);
                    if (SmartMir.super.isAdapterReady() && (connector == null)) setupConnector(device);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                super.pendingRequestEnableBt = false;
                if (resultCode != Activity.RESULT_OK) {
                    Utils.log("BT not enabled");
                }
                break;
        }
    }*/
    // ==========================================================================


    /**
     * Установка соединения с устройством
     */
    private void setupConnector(BluetoothDevice connectedDevice) {
        stopConnection();
        try {
            String emptyName = getString(R.string.empty_device_name);
            DeviceData data = new DeviceData(connectedDevice, emptyName);
            connector = new DeviceConnector(data, mHandler);
            connector.connect();
        } catch (IllegalArgumentException e) {
            Utils.log("setupConnector failed: " + e.getMessage());
        }
    }
    // ==========================================================================

    void appendLog(String message, boolean hexMode, boolean outgoing, boolean clean) {

        StringBuilder msg = new StringBuilder();
        if (show_timings) msg.append("[").append(timeformat.format(new Date())).append("]");
        if (show_direction) {
            final String arrow = (outgoing ? " << " : " >> ");
            msg.append(arrow);
        } else msg.append(" ");

        // Убрать символы переноса строки \r\n
        message = message.replace("\r", "").replace("\n", "");

        // Проверка контрольной суммы ответа
        String crc = "";
        boolean crcOk = false;
        if (checkSum) {
            int crcPos = message.length() - 2;
            crc = message.substring(crcPos);
            message = message.substring(0, crcPos);
            crcOk = outgoing || crc.equals(Utils.calcModulo256(message).toUpperCase());
            if (hexMode) crc = Utils.printHex(crc.toUpperCase());
        }

        // Лог в html
        msg.append("<b>")
                .append(hexMode ? Utils.printHex(message) : message)
                .append(checkSum ? Utils.mark(crc, crcOk ? CRC_OK : CRC_BAD) : "")
                .append("</b>")
                .append("<br>");

        logHtml.append(msg);
        logTextView.append(Html.fromHtml(msg.toString()));

        final int scrollAmount = logTextView.getLayout().getLineTop(logTextView.getLineCount()) - logTextView.getHeight();
        if (scrollAmount > 0)
            logTextView.scrollTo(0, scrollAmount);
        else logTextView.scrollTo(0, 0);
    }
    // =========================================================================


    void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
    // ==========================================================================

/*    *//**
     * Обработчик приёма данных от bluetooth-потока
     *//*
    private class BluetoothResponseHandler extends Handler {
        private WeakReference<SmartMir> mActivity;

        public BluetoothResponseHandler(SmartMir activity) {
            mActivity = new WeakReference<SmartMir>(activity);
        }

        public void setTarget(SmartMir target) {
            mActivity.clear();
            mActivity = new WeakReference<SmartMir>(target);
        }

    }*/

    private String getRightNow() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm:ss");
        return sdf.format(new Date());
    }
    // ==========================================================================

    public void btn_logout(View v) {
        new AlertDialog.Builder(this)
                .setTitle("로그아웃").setMessage("로그아웃 하시겠습니까?")
                .setPositiveButton("로그아웃", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent intent = new Intent(SmartMir.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                })
                .show();
    }

    public void btn_customCenter(View v) {
        new AlertDialog.Builder(this)
                .setTitle("통화").setMessage("고객센터로 전화하시겠습니까?")
                .setPositiveButton("통화", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        try{
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("tel:/01039748850")));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                })
                .show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(DEVICE_NAME, deviceName);
        if (logTextView != null) {
            outState.putString(LOG, logHtml.toString());
        }
    }
    // ============================================================================


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.device_control_activity, menu);
        final MenuItem bluetooth = menu.findItem(R.id.menu_search);
        if (bluetooth != null) bluetooth.setIcon(this.isConnected() ?
                R.drawable.ic_action_device_bluetooth_connected :
                R.drawable.ic_action_device_bluetooth);
        return true;
    }
    // ============================================================================


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_search:
                if (super.isAdapterReady()) {
                    if (isConnected()) stopConnection();
                    else startDeviceListActivity();
                } else {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
                return true;

            case R.id.menu_clear:
                if (logTextView != null) logTextView.setText("");
                return true;

            case R.id.menu_send:
                if (logTextView != null) {
                    final String msg = logTextView.getText().toString();
                    final Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, msg);
                    startActivity(Intent.createChooser(intent, getString(R.string.menu_send)));
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    // ============================================================================

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    BluetoothDevice device = btAdapter.getRemoteDevice(address);
                    if (super.isAdapterReady() && (connector == null)) setupConnector(device);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                super.pendingRequestEnableBt = false;
                if (resultCode != Activity.RESULT_OK) {
                    Utils.log("BT not enabled");
                }
                break;
        }
    }
    // ==========================================================================


    private class BluetoothResponseHandler extends Handler {
        private WeakReference<SmartMir> mActivity;

        public BluetoothResponseHandler(SmartMir activity) {
            mActivity = new WeakReference<SmartMir>(activity);
        }

        public void setTarget(SmartMir target) {
            mActivity.clear();
            mActivity = new WeakReference<SmartMir>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            SmartMir activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case MESSAGE_STATE_CHANGE:

                        Utils.log("MESSAGE_STATE_CHANGE: " + msg.arg1);
                        switch (msg.arg1) {
                            case DeviceConnector.STATE_CONNECTED:
                                break;
                            case DeviceConnector.STATE_CONNECTING:
                                break;
                            case DeviceConnector.STATE_NONE:
                                break;
                        }
                        activity.invalidateOptionsMenu();
                        break;

                    case MESSAGE_READ:
                        String readMessage = (String)msg.obj;
                        byte[] rxmsg = Utils.hexStringToByteArray(readMessage);
                        if(readMessage != null) {
                            int command = rxmsg[3];
                            if(command == MessageTimer.REPORT_SENGING_VALUE_IND) {
                                byte[] buf = new byte[4];
                                for(int i = 0;i < 4;i++)
                                    buf[i] = rxmsg[i + 5];
                                float t = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                                for(int i = 0;i < 4;i++)
                                    buf[i] = rxmsg[i + 10];
                                float h = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                                MainActivity.temp.setText(String.format("%.2f",t));
                                MainActivity.hum.setText(String.format("%.2f",h));
                            }
                        }
                        /*
                        final String readMessage = (String) msg.obj;
                        Log.w("debug", "rxMsg = " + readMessage + ", length = " + readMessage.length());
                        if (readMessage != null) {
//                            activity.appendLog(Utils.printHexString(readMessage), true, false, activity.needClean);
                            activity.appendLog(Utils.printHexString(readMessage), hexMode, true, activity.needClean);
                            int command = readMessage.charAt(3);
                            if(command == MessageTimer.RETRIEVE_LED_STATUS_RESP) {
                                if(mMessageTimer.isRunning(MessageTimer.RETRIEVE_LED_STATUS_REQ)) {
                                    mMessageTimer.stop();
                                    if(readMessage.charAt(4) == 0x01) {
                                        ledValueBtn.setText("OFF");
                                        ledStatus = 1;
                                    }
                                    else {
                                        ledValueBtn.setText("ON");
                                        ledStatus = 0;
                                    }
                                    dTimeView.setText(getRightNow());
                                }
                            }
                            else if(command == MessageTimer.CHANGE_LED_STATUS_RESP) {
                                if(mMessageTimer.isRunning(MessageTimer.CHANGE_LED_STATUS_REQ)) {
                                    mMessageTimer.stop();
                                    if(readMessage.charAt(4) == 0x01) {
                                        if(ledStatus == 0x00) {
                                            ledValueBtn.setText("OFF");
                                            ledStatus = 1;
                                        }
                                        else {
                                            ledValueBtn.setText("ON");
                                            ledStatus = 0;
                                        }
                                    }
                                    dTimeView.setText(getRightNow());
                                }
                            }
                            else if(command == MessageTimer.REPORT_SENGING_VALUE_IND) {
                                byte[] buf = new byte[4];
                                for(int i = 0;i < 4;i++)
                                    buf[i] = (byte)readMessage.charAt(i + 5);
                                float t = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                                for(int i = 0;i < 4;i++)
                                buf[i] = (byte)readMessage.charAt(i + 10);
                                    float h = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                                tValueView.setText(String.format("%.2f",t));
                                hValueView.setText(String.format("%.2f",h));
                                hTimeView.setText(getRightNow());
                            }
                        }
*/
                        break;

                    case MESSAGE_DEVICE_NAME:
                        activity.setDeviceName((String) msg.obj);
                        break;

                    case MESSAGE_WRITE:
                        // stub
                        break;

                    case MESSAGE_TOAST:
                        // stub
                        break;
                }
            }
        }
    }

    public void fanOnOff(int resultMsg){
        if(isConnected()) {

            StringBuilder sb = new StringBuilder();
            String Msg = Integer.toString(resultMsg);
            sb.append("0b0b044501");
            if(resultMsg == 1) {
                sb.append("0"+Msg);
            }
            else {
                sb.append("0"+Msg);
            }
            sb.append("0f0f");
            String commandString = sb.toString();
            byte[] command = Utils.toHex(commandString);
            connector.write(command);

        }
    }
    public void fanAuto(int resultMsg, int onHum, int offHum){
        if(isConnected()) {

            StringBuilder sb = new StringBuilder();

            String Msg = Integer.toString(resultMsg);
            String on = Integer.toString(onHum);
            String off = Integer.toString(offHum);

            sb.append("0b0b084401");
            if(resultMsg == 1) {
                sb.append("0"+Msg+on+off);
            }
            else {
                sb.append("0"+Msg);
            }
            sb.append("0f0f");
            String commandString = sb.toString();
            byte[] command = Utils.toHex(commandString);
            connector.write(command);

        }
    }

    public void bathwater(int resultMsg, int height, int temp){
        if(isConnected()) {

            StringBuilder sb = new StringBuilder();

            String Msg = Integer.toString(resultMsg);
            String Height = Integer.toString(height);
            String Temp = Integer.toString(temp);

            sb.append("0b0b124301");
            if(resultMsg == 1) {
                sb.append("0"+Msg+"0"+Height+Temp);
            }
            else {
                sb.append("0"+Msg);
            }
            sb.append("0f0f");
            String commandString = sb.toString();
            byte[] command = Utils.toHex(commandString);
            connector.write(command);

        }
    }


}