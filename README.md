### netty + protobuf 后端实现

#### chat-api 聊天服务

#### chat-proto 传输协议部分

> 目前只实现了 设备注册，心跳，文字聊天
  支持图片 语音，位置消息，要配置云存储

#### 单元测试部分

 ```java


@Test
// [ClientTest.java](chat-api%2Fsrc%2Ftest%2Fjava%2Fcom%2Ftaoyiyi%2Fchat%2Fclient%2FClientTest.java)
public void user1Chat(){
        ChatClient.get().setUserCode(TestLocalMsgConstant.fromUser).checkConnect();
        sendMsg(TestLocalMsgConstant.fromUser,TestLocalMsgConstant.toUser);
        }


@Test
public void u2Chat(){
        ChatClient.get().setUserCode(TestLocalMsgConstant.toUser).checkConnect();
        sendMsg(TestLocalMsgConstant.toUser,TestLocalMsgConstant.fromUser);
        }
 ```
> 只有服务端的核心部分 和一些基础proto ，如果不满足可以自定义proto 和对应的 processors


### todolist
> 目前服务是单节的。没有做集群配置，没有mq 和redis

- 1. 可以做集群方面的优化， 客户端通过http接口上传ucode通过一致性hash获取聊天服务的ip，后端在推送消息的时候通过hash 路由
2.  缓存
3. mq





