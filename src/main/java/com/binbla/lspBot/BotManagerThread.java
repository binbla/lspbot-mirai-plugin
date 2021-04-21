package com.binbla.lspBot;

import net.mamoe.mirai.event.events.MessageEvent;

/**
 * >ClassName BotManager.java
 * >Description 暂时没有想法。私聊内容处理
 * >Author binbla
 * >Version 1.0.0
 * >CreateTime 2021-04-21  16:10
 */
public class BotManagerThread extends Thread {
    MessageEvent fMsg;

    BotManagerThread(MessageEvent fMsg) {
        this.fMsg = fMsg;
        this.start();
    }

    @Override
    public void run() {
        fMsg.getSender().sendMessage("傻了吧？我还没想好私聊管理能干啥。");
        //未来有空的话写写私聊修改命令啥的操作。毕竟色图机器人面向的是群。
    }
}
