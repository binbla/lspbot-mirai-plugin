# lspbot-mirai-plugin
mirai QQ机器人的入门开发——哪个男孩子不想拥有一个自己的色图机器人呢

<!--more-->

# Java 上手Mirai插件开发

接下来我会以一个不专业的Java程序设计开发者的方式，做一个色图机器人插件

> 哪个男孩子不想拥有一款自己的色图机器人呢

这篇文章主要提供给那些想使用Java语言开发简单机器人却苦于找不到例子的人。



# 上手之前

众所周知，Mirai的文档简直\*\*\*\*

翻看Mirai的文档就能劝退一大批新手，我指的是翻这个过程，而不是阅读的过程

现在官方似乎是想尽快补足文档这个短板，所以今后应该还是有易读的文档出现的

[官方文档](https://docs.mirai.mamoe.net/)

所有的文档都可以在这里找到

什么？你只看到了SDK和JVM平台Mirai开发？

那你一定是漏掉了顶部的三个主菜单！（我最先也是被左侧的层级菜单吸引住了

我们要开发的是色图机器人，是插件！这个插件的内容就是

1. 收到了来自群组的消息
2. 判断这个消息是不是要求机器人发色图。比如`@bot 来点色图 [关键词]`
3. 机器人确认了用户想要色图，于是新建一个发送色图的线程，然后继续监听消息事件
4. 这个线程就是去访问色图api或是直接从本地的色图库（你要是有的话），把色图取出来，变成File对象，定位消息的来源，再发出去

看上去是不是很简单？对，没骗你，真的就这么简单

鉴于官方文档没个顺序，所以后面我会以自己的理解把需要的文档链接贴出来。

# 准备环境

JVM和Idea的安装配置就不说了，这个你要是个Java程序员都有这东西（Eclipse？算了吧，还是Idea好使

**Jdk一定要是openJdk！**

## 机器人框架

[启动器](https://github.com/mamoe/mirai/blob/dev/docs/UserManual.md)：大致意思就是提供了两个版本的启动器，这里的启动器你可以理解为MineCraft的启动器。萝莉巴索的，直接去[mcl-installer](https://github.com/iTXTech/mcl-installer/releases)下载一键脚本。一键打开他不香吗？

Arch Linux用户直接`yay -S mirai-console-loader`就能在`/opt/mcl/`里面找到`mcl`启动脚本

> 注意，这个shell启动脚本一定要进到这个目录才能启动，因为里面使用的是相对路径。当然你也可以自己改。后期我们可以使用新建一个`mcl@user.service`，所以洒洒水啦

一键安装好后，就可以关闭这个程序了。

在程序目录下的`config/Console/`里面有一个`AutoLogin.yml`，按照里面的说明把帐号啥的填上去，那个`protocol`先别改，这个需要登陆几天后，再改成`ANDROID_PAD`，常登地点可以直接改。

填好之后，返回到程序目录，执行启动脚本。这时候你能发现自己的机器人上线了。

（但是啥功能都没有，你可以试着用其他QQ向它发送一条消息，就能看到控制台的消息输出）

**如果是群消息的话，不要屏蔽群消息！！**

如果登陆失败：[参考这里](https://docs.mirai.mamoe.net/mirai-login-solver-selenium/#%E8%BF%90%E8%A1%8C%E5%B9%B3%E5%8F%B0%E6%94%AF%E6%8C%81)

## 快速构建项目框架

[Mirai - Configuring Projects](https://docs.mirai.mamoe.net/ConfiguringProjects.html#a-%E4%BD%BF%E7%94%A8-gradle)

1. 在Idea的插件页搜索Mirai，安装；还有Kotlin要最新

2. 新建项目 

   1. 左侧选Mirai
   2. 然后选 Java 11 openjdk
   3. 然后这三项随意，先选后面的Gradle Groovy DSL 再选前面的Java，不然你生成的是Kotlin项目（淦！这是什么Bug吗
   4. 接下来又是随意填，有自己的域名也可以填上去
   5. 选择项目位置
   6. 完成

3. 这时候等Gradle自己把相关的东西弄好就行

4. 打开这个项目的src一路到底，应该是下图这个样子

   ![image-20210422093318961](https://i.loli.net/2021/04/22/UxBpZe9LQiS6NRw.png)

左侧的Plugin就是主类

右侧的Gradle里面的`plugin/Task/mirai/buildPlugin`就是构建jar包，生成目录在项目目录里的`build/mirai`里面

把生成后的jar文件移动到`mcl/plugins/`里面再重启mcl就能用了

> 建议直接做一个符号链接到mcl目录的plugins目录，这样就能生成后直接重启mcl就能用了

接下来我们看主类中的`onEnable()`

这是插件的代码入口点，我们就在这里写自己的代码

[又长又迷的文档](https://docs.mirai.mamoe.net/console/Plugins.html#%E8%AF%BB%E5%8F%96-plugindata-%E6%88%96-pluginconfig)

这里的文档，emm。。。看看也行。了解个大概

主要注意这个东西：

- 访问数据目录和配置目录（也就是配置文件的使用，等会会用到

# 事件

让大多数人犯困的事，应该怎么写？

理解一个事：机器人在受到消息后，其实是触发了一个事件，我们要接到这个事件，然后对其进行处理。

你可以理解为机器人在受到消息（任何消息）后都抛出了一个异常（事件），我们catch到这个异常（事件），然后把这个异常（事件）里的内容拿出来进行处理。这个事件是一个对象！

事件又分好几种，我们这里主要讲Message事件

这个Message事件对象里面包含了几乎所有这个消息相关的所有信息

- 发送人的id，昵称
- 消息内容
- 接到这个消息的主体：群id，群名
- 。。。。

只写个色图机器人的话只要理解下面几句就成，这样子，一个色图机器人就变成了文字处理编程

```java
public void onEnable() {
        GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, this::handleGMsg);
    	//接收到了一个群组消息事件，并把这个事件丢给了handleGMsg()函数
        GlobalEventChannel.INSTANCE.subscribeAlways(FriendMessageEvent.class, this::handleFMsg);
    	//接收到了一个私聊消息事件，并把这个事件丢给了handleFMsg()函数
        getLogger().info("LSP插件加载完成！玩得开心");
    	//控制台输出，可以不用理他
    }
public void handleGMsg(MessageEvent gMsg){
        if(checkGroupPermission(gMsg)){
            new LspThread(gMsg);
        }
        /*
        检查是否at机器人
        检查群组是否可发图
        检查用户是否被ban
        检查内容是否包含色图关键字
        新建进程
        调用函数构造get请求并返回json
        处理json获取图片地址和作者等相关信息
        拉取图片，构造消息并发送
        进程结束
         */
    }
    public void handleFMsg(MessageEvent fMsg){
        if(fMsg.getSender().getId() == Config.INSTANCE.getOwner()){
            new BotManagerThread(fMsg);
        }
        /*
        检查是否是主人
        读取指令
        操作
        进程结束
         */
    }
```

所有事件：[事件列表](https://github.com/mamoe/mirai/blob/dev/docs/CoreAPI.md)

# 具体开发

由于时间关系，接下来长话短说：

会用到的几种方式（很容易触类旁通）这里的event就是Gmsg或是FMsg：

- `event.getSender()`：获取发送者对象（群里的人）
  - `event.getSender().getId()`：获取发送者的ID
- `event.getSubject()`：获取消息主体对象（群）
  - `event.getSubject().sendMessage()`：向这个群发送内容
  - `event.getSubject().getID()`：获取群号
- `event.getMessage()`：获取消息内容
  - `event.getMessage().serializeToMiraiCode()`：将消息转成miraicode方便识别
  - `event.getMessage().toString()`：获取消息文字（有格式
- `event.getSubject().getBot()`：接收到这个消息的bot对象
  - `event.getSubject().getBot().getId()`：这个bot的ID

就上面这几种方式看明白了，马上就能触类旁通，你可以利用idea的补全查看该对象下其他的函数

例如：

![image-20210422103747450](https://i.loli.net/2021/04/22/GyVJ1uAnScUwBbe.png)

到这里，开发一个机器人插件就真的变成了一个**文字处理程序**

当然，你也可以玩得花，数据库，爬虫，只要你会写，这都通通不是事！

>  比如利用数据库和超级课程表提供的api开发一个课程表机器人，还能按时提前提醒你下节课在哪儿上，谁上，时间。。。。

下面是我开发的一个色图机器人，我丢在了github。我不是什么专业的Java程序员，所以结构会有点乱。

![](https://i.loli.net/2021/04/22/dVksTheCDi4lfRZ.jpg)

