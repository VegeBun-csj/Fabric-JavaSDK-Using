## Fabric Java SDK的使用(high level API —— gateway)

> 在windows上使用 JavaSDK 连接到远程服务器

使用Java SDK 1.4.4 ,因为我Fabric版本是1.4.4 ，使用的GateWay的API，运行的例子是官方的Fabcar例子，因为实际开发不可能是在虚拟机上运行，所以在Windows上运行SDK远程连接到服务器，将Fabcar中的例子先拷贝至windows上

### 目录结构如下：

![image-20201008143422546](C:\Users\Bun\AppData\Roaming\Typora\typora-user-images\image-20201008143422546.png)

`main`的目录中`java`放SDK代码，`resources`中放节点的密钥文件`crypto-config`，以及`CCP`(连接配置文件)，当然`wallet`（用户的身份文件）也设置生成在这个目录下面。

#### 运行之前的注意点：

- 将`first-network`目录的`connection-org1.yaml`文件和`crypto-config`文件拷贝至`resources`目录下，每一次启动`fabcar`网络的时候，这里的`CCP文件`和`crypto-config`都是不同的，这里需要注意。

- 修改CCP文件中的IP地址，将其中所有的节点的grpc通信地址的URL：`url: grpcs://localhost:7051`换成url: `grpcs://你的服务器的IP地址:7051`，比如我这里peer0org1的就是改成`url: grpcs://192.168.162.139:7051`

- 将`ClientApp.java`中的

  ```java
  System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
  builder.identity(wallet, "ccc").networkConfig(networkConfigPath).discovery(false);
  ```

  将这两个`true`属性改成`false` ！！！！，这一步很重要

  > 这里原来是true主要是开启本地的discovery service，因为是远程连接到服务器，所以不能开启本地的discovery service,否则就会出现如下错误
  >
  > ```shell
  > 14:21:23.725 [main] ERROR org.hyperledger.fabric.sdk.Channel - Channel Channel{id: 1, name: mychannel} Sending proposal with transaction: 59ae0524176afe4bc9b43bee3b27f636c13974b7c1954887e4edea24bc3e0272 to Peer{ id: 13, name: peer0.org1.example.com:7051, channelName: mychannel, url: grpcs://localhost:7051, mspid: Org1MSP} failed because of: gRPC failure=Status{code=UNAVAILABLE, description=io exception, cause=io.netty.channel.AbstractChannel$AnnotatedConnectException: Connection refused: no further information: localhost/0:0:0:0:0:0:0:1:7051
  > Caused by: java.net.ConnectException: Connection refused: no further information
  > ```
  >
  > 可以看到其中的url：grpcs://localhost:7051，这里居然是localhost，而我们并不是在windows本地

- 在windows的hosts文件中添加 ip和域名的映射！！！

  > 进入到`C:\Windows\System32\drivers\etc`，在其中加入
  >
  > ```shell
  > 192.168.162.139 peer0.org1.example.com
  > 192.168.162.139 peer1.org1.example.com
  > 192.168.162.139 peer0.org2.example.com
  > 192.168.162.139 peer1.org2.example.com
  > 192.168.162.139 orderer.example.com
  > ```
  >
  > 前面是服务器的地址，后面是节点，因为这些节点都是在一个服务器上的，所以ip都是一样！！！
  >
  > 如果不配置就运行client端的交易会出现四个peer节点和orderer节点的域名找不到，解析不出来：
  >
  > ```shell
  > 警告: [Channel<31>: (peer0.org2.example.com:9051)] Failed to resolve name. status=Status{code=UNAVAILABLE, description=Unable to resolve host peer0.org2.example.com, cause=java.lang.RuntimeException: java.net.UnknownHostException: peer0.org2.example.com
  > 	at io.grpc.internal.DnsNameResolver.resolveAll(DnsNameResolver.java:420)
  > 	....
  > 警告: [Channel<29>: (peer0.org1.example.com:7051)] Failed to resolve name. status=Status{code=UNAVAILABLE, description=Unable to resolve host peer0.org1.example.com, cause=java.lang.RuntimeException: java.net.UnknownHostException: peer0.org1.example.com
  > 	at io.grpc.internal.DnsNameResolver.resolveAll(DnsNameResolver.java:420)
  > 	....
  > 警告: [Channel<39>: (peer1.org2.example.com:10051)] Failed to resolve name. status=Status{code=UNAVAILABLE, description=Unable to resolve host peer1.org2.example.com, cause=java.lang.RuntimeException: java.net.UnknownHostException: peer1.org2.example.com
  > 	at io.grpc.internal.DnsNameResolver.resolveAll(DnsNameResolver.java:420)
  > 	....
  > 	警告: [Channel<37>: (peer1.org1.example.com:8051)] Failed to resolve name. status=Status{code=UNAVAILABLE, description=Unable to resolve host peer1.org1.example.com, cause=java.lang.RuntimeException: java.net.UnknownHostException: peer1.org1.example.com
  > 	at io.grpc.internal.DnsNameResolver.resolveAll(DnsNameResolver.java:420)
  > 	...
  > 	警告: [Channel<49>: (orderer.example.com:7050)] Failed to resolve name. status=Status{code=UNAVAILABLE, description=Unable to resolve host orderer.example.com, cause=java.lang.RuntimeException: java.net.UnknownHostException: orderer.example.com
  > 	at io.grpc.internal.DnsNameResolver.resolveAll(DnsNameResolver.java:420)
  > ```
  >
  > 域名无法解析，所以我们就在hosts文件中添加它们的映射，本地的windows就能认识他们啦！！！！！



下面来运行三个文件就行啦！！！最后调用交易如下：

![image-20201008143449177](C:\Users\Bun\AppData\Roaming\Typora\typora-user-images\image-20201008143449177.png)

