package com.binbla.lspBot;

import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.SimpleServiceMessage;

import java.util.Map;

/**
 * >ClassName LspThread.java
 * >Description TODO
 * >Author binbla
 * >Version 1.0.0
 * >CreateTime 2021-04-20  19:02
 */
public class LspThread extends Thread {
    MessageEvent gMsg;

    LspThread(MessageEvent gMsg) {
        this.gMsg = gMsg;
        this.start();
    }

    @Override
    public void run() {
        //检查是否有限时撤回
        if (Config.INSTANCE.getRecallTime() == 0) {
            gMsg.getSubject().sendMessage(buildMsg());
        } else {
            gMsg.getSubject().sendMessage(buildMsg()).recallIn(Config.INSTANCE.getRecallTime());
        }
    }

    public MessageChain buildMsg() {
        String[] command = gMsg.getMessage().serializeToMiraiCode().split("\\s");
        //command[] 为消息链 不过首先得at机器人才能来到这里
        //command[1] 为命令，后面的都是参数，有可能不存在
        //标准格式: @bot command [args]
        MessageChain chain;
        try {//先来一个try包着。接下来命令处理会有大量异常（不正确的指令，不合法的参数）
            if (command.length == 1) {
                //如果只是at了机器人却没有内容
                throw new LspException(100);//所有异常都在LspException类里面
            } else {
                int cmdCode = checkCommand(command[1]);//获取指令代码
                switch (cmdCode) {
                    case 0:
                    case 1:
                    case 2:
                    case 3: {
                        if (checkOwner()) {
                            switchMode(cmdCode);
                        } else {
                            throw new LspException(102);
                        }
                    }//前四个都是切换模式，所以可以放一起
                    case 4://检查当前群的状态
                        chain = checkStatus();
                        break;
                    case 5: {//修改限时撤回时间
                        try {
                            if (checkOwner()) {
                                Long time = Long.parseLong(command[2]);
                                setRecall(time);
                            } else {
                                throw new LspException(102);
                            }
                        } catch (NumberFormatException e) {
                            throw new LspException(103);
                        }
                        chain = checkStatus();
                        break;
                    }
                    case 6: {//获取帮助
                        chain = lspHelp();
                        break;
                    }
                    case 7: {//将用户添加到名单，未来可能考虑重构成黑白分明的模式
                        try {
                            if (checkOwner()) {
                                Long userID = Long.parseLong(command[2]);
                                addUserList(userID);
                            } else {
                                throw new LspException(102);
                            }
                        } catch (NumberFormatException e) {
                            throw new LspException(103);
                        }
                    }
                    case 8: {//查看名单
                        chain = checkUserList();
                        break;
                    }
                    case 9: {//名单模式切换（黑/白）
                        if (checkOwner()) {
                            shiftUserList();
                            chain = checkUserList();
                            break;
                        } else {
                            throw new LspException(102);
                        }
                    }
                    case 10: {//色图！！！
                        if (checkSetuPermission()) {
                            if (command.length == 2) {
                                chain = getSetu();
                            } else {
                                chain = getSetu(command[2]);
                            }
                            break;
                        } else {
                            throw new LspException(101);
                        }
                    }
                    default: {
                        throw new LspException(103);
                    }
                }
            }
        } catch (LspException e) {
            chain = new MessageChainBuilder().append(e.getInf()).build();
            //从自定义异常类中返回字符串构造消息
        }
        return chain;
    }

    public int checkCommand(String command) {
        //检查command是啥,详情看Config.kt。这里应该写个枚举类的，不然读者有点费劲
        if (Config.INSTANCE.getCommand_mode0().contains(command)) return 0;
        if (Config.INSTANCE.getCommand_mode1().contains(command)) return 1;
        if (Config.INSTANCE.getCommand_mode2().contains(command)) return 2;
        if (Config.INSTANCE.getCommand_off().contains(command)) return 3;
        if (Config.INSTANCE.getCommand_inf().contains(command)) return 4;
        if (Config.INSTANCE.getCommand_callBack().contains(command)) return 5;
        if (Config.INSTANCE.getCommand_help().contains((command))) return 6;
        if (Config.INSTANCE.getCommand_addUserList().contains(command)) return 7;
        if (Config.INSTANCE.getCommand_shiftListMode().contains(command)) return 9;
        if (Config.INSTANCE.getCommand_checkUserList().contains(command)) return 8;

        if (Config.INSTANCE.getCommand_get().contains(command)) return 10;
        return -1;
    }

    public Boolean checkOwner() {
        //检查命令的发起人是否为主人
        return Config.INSTANCE.getOwner() == gMsg.getSender().getId();
    }

    public MessageChain getSetu() {
        //无参数色图
        return getSetu("");
    }

    public MessageChain getSetu(String keyWord) {
        //带参数色图
        MessageChain chain;
        try {
            //调用LspConnection中的url构造方法获取json
            String apiReturnedJson = LspConnection.getJsonString(keyWord, "" + gMsg.getSubject().getId());
            if ("".equals(apiReturnedJson)) {
                throw new LspException(-2);
                //json为空，则连接出问题了
            }
            JsonClass jsonClass = JsonClass.getAPI(apiReturnedJson);
            //这里调用自己的Json类，把api返回来的json转成实体
            if (jsonClass.code != 0) {
                throw new LspException(jsonClass.code);
                //api返回来的json状态码不为0。则拉取不到图，详细返回code请看lolicon API的使用说明
            }
            chain = new MessageChainBuilder().append(new SimpleServiceMessage(60, getXmlString(jsonClass))).build();
            //经典！！构造卡片消息，这样就不用先把图下载到服务器再上传到QQ了
        } catch (LspException e) {
            chain = new MessageChainBuilder().append(e.getInf()).build();
            //所有异常汇总，由LspException来处理异常信息为文本可读消息，发送到目的群组
        }
        return chain;
    }

    public void switchMode(int modeCode) {
        //切换名单模式
        Map<String, Integer> map = Config.INSTANCE.getGroupMode();
        map.put("" + gMsg.getSubject().getId(), modeCode);
    }

    public String getXmlString(JsonClass json) {
        //构造xml消息卡片
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<msg serviceID=\"1\" action=\"web\" url=\"" + json.data.get(0).url + "\">" +
                "<item><title>" + json.data.get(0).title + "</title>" +
                "<summary>" +
                "Author:" + json.data.get(0).author +
                "\nR18:" + json.data.get(0).r18 +
                ",剩余:" + json.quota +
                ",重置时间:" + json.quota_min_ttl +
                "</summary>" +
                "<picture cover=\"" + json.data.get(0).url +
                "\"/></item>" +
                "</msg>";
    }

    public Boolean checkSetuPermission() {
        //检查当前群是否不能发色图（状态3）
        return !Config.INSTANCE.getGroupMode().get("" + gMsg.getSubject().getId()).equals(3);
    }

    public MessageChain checkStatus() {
        //查看机器人在当前群的限制
        MessageChain chain;
        try {
            if (Config.INSTANCE.getGroupMode().containsKey("" + gMsg.getSubject().getId())) {
                String mode = "淦！";
                switch (Config.INSTANCE.getGroupMode().get("" + gMsg.getSubject().getId())) {
                    case 0:
                        mode = "保护未成年人";
                        break;
                    case 1:
                        mode = "都给我冲！";
                        break;
                    case 2:
                        mode = "我全都要！";
                        break;
                    case 3:
                        mode = "已关闭";
                        break;
                }
                chain = new MessageChainBuilder()
                        .append("当前群:" + gMsg.getSubject().getId())
                        .append("\n当前模式:" + mode)
                        .append("\n自动撤回:" + Config.INSTANCE.getRecallTime() + "ms")
                        .append("\n机器人所有者:" + Config.INSTANCE.getOwner())
                        .build();
            } else {
                throw new LspException(101);
            }
        } catch (LspException e) {
            chain = new MessageChainBuilder().append(e.getInf()).build();
        }
        return chain;
    }

    public MessageChain lspHelp() {
        //帮助
        MessageChain chain;
        String help = "命令格式：" +
                "\n@机器人 命令 [参数列表]" +
                "\n当前支持的命令:\n" +
                "\n帮助:" + Config.INSTANCE.getCommand_help() +
                "\n色图:" + Config.INSTANCE.getCommand_get() +
                "\n关闭插件:" + Config.INSTANCE.getCommand_off() +
                "\n修改撤回：" + Config.INSTANCE.getCommand_callBack() +
                "\n查询状态：" + Config.INSTANCE.getCommand_inf() +
                "\n普通模式：" + Config.INSTANCE.getCommand_mode0() +
                "\nR18模式：" + Config.INSTANCE.getCommand_mode1() +
                "\n混合模式：" + Config.INSTANCE.getCommand_mode2() +
                "\n添加名单：" + Config.INSTANCE.getCommand_addUserList() +
                "\n切换名单模式：" + Config.INSTANCE.getCommand_shiftListMode() +
                "\n查看名单详细：" + Config.INSTANCE.getCommand_checkUserList();
        chain = new MessageChainBuilder().append(help).build();
        return chain;
    }

    public void setRecall(Long time) {
        //设置自动撤回时间
        Config.INSTANCE.setRecallTime(time);
    }

    public void addUserList(Long userID) {
        //将用户添加到list或是移除
        if (!Config.INSTANCE.getUserList().contains(userID))
            Config.INSTANCE.getUserList().add(userID);
        else {
            Config.INSTANCE.getUserList().remove(userID);
        }
    }

    public void shiftUserList() {
        //标注可简化内容，极大降低可读性
        //切换名单模式
        if (Config.INSTANCE.getUsesListMode()) {
            Config.INSTANCE.setUsesListMode(false);
        } else {
            Config.INSTANCE.setUsesListMode(true);
        }
    }

    public MessageChain checkUserList() {
        //检查名单状态
        String list = Config.INSTANCE.getUsesListMode() ? "黑名单" : "白名单";
        return new MessageChainBuilder().append("当前名单模式是" + list)
                .append("\n" + Config.INSTANCE.getUserList())
                .build();
    }
}
