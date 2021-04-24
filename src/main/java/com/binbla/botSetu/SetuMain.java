package com.binbla.botSetu;

import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.BotOnlineEvent;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;

public final class SetuMain extends JavaPlugin {
    public static final SetuMain INSTANCE = new SetuMain();

    private SetuMain() {
        super(new JvmPluginDescriptionBuilder("com.binbla.botSetu.plugin", "2.0")
                .name("SetuTime")
                .author("binbla")
                .build());
    }

    @Override
    public void onEnable() {
        reloadPluginConfig(Config.INSTANCE);
        System.setProperty("http.agent", Config.INSTANCE.getUserAgent());
        GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, this::handleGMsg);
        GlobalEventChannel.INSTANCE.subscribeAlways(FriendMessageEvent.class, this::handleFMsg);
        getLogger().info("LSP插件加载完成！玩得开心");
        GlobalEventChannel.INSTANCE.subscribeOnce(BotOnlineEvent.class, e -> {
            if (Config.INSTANCE.getBotID().contains(e.getBot().getId()))
                try {
                    e.getBot().getFriend(Config.INSTANCE.getOwner()).sendMessage("大人，您的Bot上线了！");
                } catch (Exception exception) {
                    getLogger().info("什么？？你的机器人竟然不加主人为好友？");
                }

        });
    }

    public Boolean checkGroupPermission(MessageEvent gMsg) {

        if (gMsg.getMessage().serializeToMiraiCode().startsWith("[mirai:at:" + gMsg.getBot().getId() + "]")) {
            //是否at机器人开头
            if (Config.INSTANCE.getBotID().contains(gMsg.getSubject().getBot().getId())) {
                //机器人是否在列表
                if (gMsg.getSender().getId() == Config.INSTANCE.getOwner()) return true;
                //如果是机器人主人的话，不检查名单，直接许可
                if (Config.INSTANCE.getListMode().get("whiteListMode")) {
                    return Config.INSTANCE.getList().get("whiteList").contains(gMsg.getSender().getId());
                }
                if (Config.INSTANCE.getListMode().get("blackListMode")) {
                    return !Config.INSTANCE.getList().get("blackList").contains(gMsg.getSender().getId());
                }
                return true;
            }
        }
        return false;
    }

    public void handleGMsg(MessageEvent gMsg) {
        if (checkGroupPermission(gMsg)) {
            new SetuThread(gMsg);
        }
        /*
        检查是否at机器人
        检查群组是否可发图
        检查黑白名单是否启用，用户是否在其中
        检查内容是否包含色图关键字
        新建进程
        调用函数构造get请求并返回json
        处理json获取图片地址和作者等相关信息
        拉取图片，构造消息并发送
        进程结束
         */
    }

    public void handleFMsg(MessageEvent fMsg) {
//        if (fMsg.getSender().getId() == Config.INSTANCE.getOwner()) {
//            new BotManagerThread(fMsg);
//        }
        /*
        检查是否是主人
        读取指令
        操作
         */
    }
}
