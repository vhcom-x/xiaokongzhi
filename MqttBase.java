package vhcom.cn.smallcontrol.mqtt;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


public class MqttBase implements Runnable {
    // mqtt创建时运行，退出时停止。
    // mqtt 初始化参数，连接，订阅，回调处理。
    // 参数
    private String gHost = "tcp://127.0.0.1:1883";
    private String gUserName = "vhcom";
    private String gPassWord = "vhcom";
    private String gClientId = "AndroidClient1";
    private String gStrTopic = "Topic";

    // 定义变量
    MqttClient gMqttClient;
    MqttConnectOptions gMqttConnectOptions;
    Handler gHandler;
    boolean mqttRunFlag = false;

    public MqttBase(Handler handler, String str) {
        mqttRunFlag = true;
        gHandler = handler;
        gClientId = str;
        gOptions();
        gLog("配置：");
        gLog("客户ID:" + gClientId);
        gLog("主题:" + gStrTopic);

    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

        while (true) {
            if (mqttRunFlag == true) {

                while (!gMqttClient.isConnected()) {
                    gConnect();
                    gSubscribe(gStrTopic, 1);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }

                }

            }
            try {
                gPublist(gStrTopic, "10秒一次一次", 1);
                Thread.sleep(10000);
            } catch (InterruptedException e) {
            }
        }

    }

    // 日记消息
    public void gLog(String str1) {
        Message gMessage = new Message();
        Bundle gBundle = new Bundle();
        gMessage.what = 1;
        gBundle.putString("log", str1);
        gMessage.setData(gBundle);
        gHandler.sendMessage(gMessage);

    }

    // 参数设定
    public void gOptions() {
        try {

            // host为主机名，test为clientid即连接MQTT的客户端ID，一般以客户端唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存
            gMqttClient = new MqttClient(gHost, gClientId, null);

            // MQTT的连接设置
            gMqttConnectOptions = new MqttConnectOptions();
            // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
            gMqttConnectOptions.setCleanSession(true);
            // 设置连接的用户名
            gMqttConnectOptions.setUserName(gUserName);
            // 设置连接的密码
            gMqttConnectOptions.setPassword(gPassWord.toCharArray());
            // 设置超时时间 单位为秒
            gMqttConnectOptions.setConnectionTimeout(10);
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            gMqttConnectOptions.setKeepAliveInterval(20);
            // 设置回调
            gMqttClient.setCallback(mqttCallback);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // 连接
    public void gConnect() {
        if (gMqttClient != null && mqttRunFlag == true
                && !gMqttClient.isConnected()) {
            try {
                gMqttClient.connect(gMqttConnectOptions);
                gLog("连接成功");
            } catch (Exception e) {
                e.printStackTrace();
                gLog("连接异常");
            }

        }
    }

    // 订阅
    public void gSubscribe(String topic, int qos) {
        if (gMqttClient != null && mqttRunFlag == true
                && gMqttClient.isConnected()) {
            try {
                gMqttClient.subscribe(topic, qos);
                gLog("订阅成功");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    // 发送消息
    public void gPublist(String topic, String msg, int qos) {

        if (gMqttClient != null && mqttRunFlag == true
                && gMqttClient.isConnected()) {
            try {
                MqttMessage message = new MqttMessage(msg.getBytes());
                message.setQos(qos);
                gMqttClient.getTopic(topic).publish(message);
            } catch (MqttException e) {

            }
        }

    }



    private MqttCallback mqttCallback = new MqttCallback() {
        @Override
        public void connectionLost(Throwable throwable) {

        }

        @Override
        public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
            String str1 = new String(mqttMessage.getPayload());

            gLog(str1);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

        }
    };

    // 开始运行
    public void gStart() {
        mqttRunFlag = true;

    }

    // mqtt关闭
    private void gClose() {
        if (gMqttClient != null) {
            try {
                gMqttClient.disconnect();

            } catch (MqttException e) {
                e.printStackTrace();

            }
        }

    }

    // 停止运行
    public void gStop() {
        gClose();
        mqttRunFlag = false;
    }

}
