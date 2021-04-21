package com.binbla.lspBot;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.*;

public final class LspBotMain extends JavaPlugin {
    public static final LspBotMain INSTANCE = new LspBotMain();

    public LspBotMain() {
        super(new JvmPluginDescriptionBuilder("com.binbla.botProject.LspBot", "1.0")
                .name("LspBot")
                .author("bla")
                .build());
    }

    @Override
    public void onEnable() {
        reloadPluginConfig(Config.INSTANCE);
        System.setProperty("http.agent",Config.INSTANCE.getUserAgent());
        GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, this::handleGMsg);
        GlobalEventChannel.INSTANCE.subscribeAlways(FriendMessageEvent.class, this::handleFMsg);
        getLogger().info("LSP插件加载完成！玩得开心");
        GlobalEventChannel.INSTANCE.subscribeOnce(BotOnlineEvent.class,e ->{
            if(Config.INSTANCE.getBotID().contains(e.getBot().getId()))
                try{
                    e.getBot().getFriend(Config.INSTANCE.getOwner()).sendMessage("大人，您的Bot上线了！");
                }catch (Exception exception){
                    getLogger().info("什么？？你的机器人竟然不加主人为好友？");
                }

        });
    }
    public Boolean checkGroupPermission(MessageEvent gMsg){
        if(gMsg.getSender().getId() == Config.INSTANCE.getOwner())return true;
        if(gMsg.getMessage().serializeToMiraiCode().startsWith("[mirai:at:"+gMsg.getBot().getId()+"]")){
            //是否at机器人开头
            if(Config.INSTANCE.getBotID().contains(gMsg.getSubject().getBot().getId())){
                //机器人是否在列表
                if(Config.INSTANCE.getGroupMode().containsKey(""+gMsg.getSubject().getId())){
                    //是否在可用群组内
                    if(Config.INSTANCE.getUsesListMode()){
                        //用户名单模式 true为黑名单,false为白名单
                        //黑
                        //用户不在黑名单?
                        return !Config.INSTANCE.getUserList().contains(gMsg.getSender().getId());
                    }else{
                        //白
                        //用户在白名单?
                        return Config.INSTANCE.getUserList().contains(gMsg.getSender().getId());
                    }
                }
            }
        }
        return false;
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
         */
    }
}
