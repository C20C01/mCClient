# 简介

## 支持正版服务器的命令行MC登陆器

### 关键词：Java、支持正版验证、命令行（无图形界面）、仅支持协议757、758（MC 1.18+）、~~简单~~

+ 使用Java编写，许多代码来自另一开源项目 [Another-Minecraft-Chat-Client](https://github.com/Defective4/Another-Minecraft-Chat-Client)，mCClient算是它的简化版本吧，只是加上了对正版的支持。

+ 目前仅支持使用UUID及AccessToken进行正版验证，还没搞微软那个登录，如何获取UUID和AccessToken可以看看下面的说明。

+ 本人Java功力不深，~~这个东西更多是一个半成品或者是一个示例，只有较少的功能。~~

### 现阶段的具体功能

+ 设置服务器的host、port、protocol及玩家的username、uuid、token（AccessToken） 。

+ 使用设置上面好的参数登录正版/离线模式的服务器（离线模式可用不填uuid和token，username也可以任意填写）。

+ 获取服务器的信息（就是MC服务器列表），这个可能信息会多一点，可以看到目前在线的玩家（应该吧）。

+ 获取游戏内的消息，向游戏发送消息，没有给太多消息（一串Json）进行分析，看起来会有一点乱。

+ 在玩家生命值、饥饿值发生变化时，返回数值。

+ 当玩家死亡时，自动重生。同时返回提示。

+ 保存玩家的参数到其文件夹内自动生成的json文档（mCClient.json），下次就能直接使了。

***

### 2022/4/26 更新到1.4版本：

1. 增加了对聊天json的解析，聊天更方便了。不用再看着一大串json聊天了。
2. 增加了对服务器信息json的解析，观感更棒了。
3. 增加了暂存聊天功能，在命令模式中（/cc），服务器发送过来的消息会被暂存，省的正打着字被其他消息给顶差了行。
   在退出命令模式后，暂存的消息会全部输出出来。
4. 增加了时间显示，显示消息产生的时间，看聊天记录更爽了。
5. 增加了对一些异常的捕捉，程序应该更稳了，就算炸掉应该也能找到原因。
6. 增加了“send”功能：在命令模式中发送消息，与第3点相配合。
7. 增加了文本切割功能，避免一次发送的文本字节数>256被服务器踢出，现在会把过长的文本分成若干部分，依次发出。
   仍存在漏洞，依次发送未设置延迟，导致过短时间发送过多次消息还是会被踢出：“垃圾信息”。
8. 增加了对实体的数据包的支持：
    1. “showe”功能：显示周围的实体的信息，尚未对type进行解析，还是一堆数字，但可以去自行查表。
    2. “attack”功能：挺好玩的一个功能，攻击所有可攻击到的实体或指定实体（使用“showe”提供的实体ID）。
       挺神奇的一点，这个可以做到无视野隔墙攻击，可不要滥用哦。主要是为打盔甲架，处死怪物使得。
9. 增加了“item”功能，切换手持物品，目前不是很完善，没太大用途（除非你知道你快捷栏里都是啥）。
10. 增加了“move”功能，小范围移动，瞬移到指定坐标，也是没啥用。赶路还得是靠真人赶路，再使软件挂机。
11. 增加了“music”功能，这是个大头，搞了挺长时间的。像是脚本那种，提前布置好音符盒（一个音一个盒）。
    可以按照给出的谱子自动演奏（原版可用哦），不受红石限制（指音符间隔不能短于一红石刻），支持 [nbs](https://opennbs.org/) 格式。
    nbs格式支持来自：[NoteBlockAPI](https://github.com/xxmicloxx/NoteBlockAPI) ，不过仅支持老版的，不难转换就是注意一下。网上有很多nbs资源。
    可以去服务器开音乐会了，哈哈哈。音符盒必须按照后面给出的规定格式建造。
12. 增加了提示，每层子命令都会提供相应的命令，及其说明。更容易上手了。
13. 改进了命令交互模式，现在进入子命令后在执行大部分命令后，不会直接返回到主页面，而是需要输入“back”。

#### 音符盒演奏功能的演示视频：[B站视频](https://www.bilibili.com/video/BV1Ai4y1m7dy)

***

#### 关于正版验证的UUID及AccessToken的获取

我平常是使用HMCL登陆器的（一个开源的MC登陆器，可以去Github上看看）。在用微软账号授权后，它的认证系统可以帮助我们获取我们的UUID及AccessToken。

在与启动器同一文件夹内的hmcl.json内就能看到。

# 具体实现

可以去参考 [wiki.vg](https://wiki.vg/Protocol) ，以及 [列表](https://wiki.vg/Client_List)内的其他开源客户端。

也可以看看我写的一个小总结[mc的通信协议及其实现](https://c20c01.github.io/2022/02/23/mc%E7%9A%84%E9%80%9A%E4%BF%A1%E5%8D%8F%E8%AE%AE%E5%8F%8A%E5%85%B6%E5%AE%9E%E7%8E%B0/)。

# 不足之处请多多包涵。 2022/2/23

## ps：

因为里面搞中文注释会没法编译，搞英文又看不懂，所以就没搞注释了。
里面那些注释应该都来自上面提到的“AMCC”，那个比较高级（就是没法支持正版验证，不然我就直接使那个了，当然也就没有这个了），起码有UI，从里面copy、学到了不少。
正版验证后的加密对话，那部分是从mc源码学到的，做mod的那个项目里就能看到。
我也不太会用Git，不太敢直接在人家那里搞的，再加上他那个有点难懂，于是就单搞了这个，比较简单明了的版本，比较适合大家从里面学习mc的相关协议（学我的Java还是算了吧）。