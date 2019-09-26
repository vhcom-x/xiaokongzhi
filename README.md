# xiaokongzhi
小控制-Small control
使用浏览器控制你的设备。

把127.0.0.1改成你的服务器地址。

订阅的主题是：
主题：mac+"/#"

发送消息：
主题：mac+"/function/引脚号" 。
消息：{msg:on} 是打开。
消息：{msg:off} 是关闭。

解析消息：
主题：mac+"/status"。
消息：{msg:online} 是在线消息。
消息：{msg:offline} 是离线消息。


