# lspbot-mirai-plugin

mirai机器人的入门开发————哪个男孩子不想拥有一个自己的色图机器人呢

## 开发之前

笔者是使用Java开发的，kotlin真的打脑壳

目前mirai的文章很渣，没错，是真的很渣！但好在一点一点进步

这里贴一下需要了解的文档（有点耐心！

[Mirai的github](https://github.com/mamoe/mirai)

过于难懂的话就不说了

mirai是一个库，你可以用这个库搞一个自己的机器人。但是，我费那功夫干嘛

所以接下来我们要搞的内容就是插件！

## 准备东西

[MCL一键启动器](https://github.com/iTXTech/mcl-installer/releases)

Archlinux用户可以直接在aur安装，默认是安装到`/opt/mcl`里面的，路径权限给的`777`(汗～)

这个东西可以让你直接在服务器上登陆上QQ，就和MC启动器一样

但是，里面是干干净净的，啥玩意儿都没有！

所以这时候我们需要插件来让咱的机器人能够按我们的思维想法对消息，主要是**对消息做出反应**。或是主动发出消息。就像两个人在QQ上对话一样。

把文档贴出来：

[Mirai Console](https://github.com/mamoe/mirai-console/blob/master/docs/BuiltInCommands.md#mirai-console---builtin-commands)的内建命令

[MiraiConsole的开发文档](https://github.com/mamoe/mirai-console/blob/master/docs/README.md#mirai-console)理解这几个插件基础的内容

[CoreApi](https://github.com/mamoe/mirai/blob/dev/docs/CoreAPI.md)重点理解**事件**

注意：我们要开发的是Plugin,别一股脑往core上撞

[Gradle](https://github.com/mamoe/mirai-console/blob/master/docs/ConfiguringProjects.md)配置开发模板

大致到这里，就能看懂plugin的内容联系了

接下来就可以去开动脑筋搞插件了。我的源码章法挺乱，但是如果你是在找不到相关的Java项目，你可以下载来琢磨琢磨
