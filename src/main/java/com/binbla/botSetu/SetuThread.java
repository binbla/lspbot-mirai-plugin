package com.binbla.botSetu;

import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.EmptyMessageChain;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.SimpleServiceMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.binbla.botSetu.SetuMain;

/**
 * >ClassName SetuThread.java
 * >Description TODO
 * >Author binbla
 * >Version 1.0.0
 * >CreateTime 2021-04-24  14:50
 */
public class SetuThread extends Thread {
    MessageEvent gMsg;

    SetuThread(MessageEvent gMsg) {
        this.gMsg = gMsg;
        this.start();
    }

    @Override
    public void run() {
        //检查是否有限时撤回
        if (Config.INSTANCE.getGroupMode().containsKey(gMsg.getSubject().getId()) && Config.INSTANCE.getGroupMode().get(gMsg.getSubject().getId()).get(1)!=0) {
            gMsg.getSubject().sendMessage(buildMsg()).recallIn(Config.INSTANCE.getGroupMode().get(gMsg.getSubject().getId()).get(1));
        } else {
            gMsg.getSubject().sendMessage(buildMsg());
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
                throw new SetuException(100);//所有异常都在SetuException类里面
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
                            throw new SetuException(102);
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
                                throw new SetuException(102);
                            }
                        } catch (NumberFormatException e) {
                            throw new SetuException(103);
                        }
                        chain = checkStatus();
                        break;
                    }
                    case 6: {//获取帮助
                        chain = setuHelp();
                        break;
                    }
                    case 7:
                    case 8: {//将用户添加到名单
                        if (checkOwner()) {
                            try {
                                Long userID = Long.parseLong(command[2]);
                                if (cmdCode == 7) {
                                    addList("whiteList", userID);
                                } else {
                                    addList("blackList", userID);
                                }
                            } catch (NumberFormatException e) {
                                throw new SetuException(103);
                            }
                        } else {
                            throw new SetuException(102);
                        }
                        chain = new MessageChainBuilder().append("哦～").build();
                        break;
                    }
                    case 9: {//查看名单
                        chain = checkUserList();
                        break;
                    }
                    case 10: {
                        if (checkOwner()) {
                            chain = shiftWhiteListMode();
                        } else throw new SetuException(102);
                        break;
                    }
                    case 11: {
                        if (checkOwner()) {
                            chain = shiftBlackListMode();
                        } else {
                            throw new SetuException(102);
                        }
                        break;
                    }
                    case 12: {
                        try {
                            Long userID = Long.parseLong(command[2]);
                            chain = removeWhiteList(userID);
                        } catch (NumberFormatException e) {
                            throw new SetuException(103);
                        }
                        break;
                    }
                    case 13: {
                        try {
                            Long userID = Long.parseLong(command[2]);
                            chain = removeBlackList(userID);
                        } catch (NumberFormatException e) {
                            throw new SetuException(103);
                        }
                        break;
                    }
                    case 99: {//色图！！！
                        if (checkSetuPermission()) {
                            if (command.length == 2) {
                                chain = getSetu();
                            } else {
                                chain = getSetu(command[2]);
                            }
                            break;
                        } else {
                            throw new SetuException(101);
                        }
                    }
                    default: {
                        throw new SetuException(103);
                    }
                }
            }
        } catch (SetuException e) {
            chain = new MessageChainBuilder().append(e.getInf()).build();
            //从自定义异常类中返回字符串构造消息
        }
        return chain;
    }

    public int checkCommand(String command) {
        //检查command是啥,详情看Config.kt。这里应该写个枚举类的，不然读着有点费劲
        if (Config.INSTANCE.getCommand_get().contains(command)) return 99;
        if (Config.INSTANCE.getCommand_mode0().contains(command)) return 0;
        if (Config.INSTANCE.getCommand_mode1().contains(command)) return 1;
        if (Config.INSTANCE.getCommand_mode2().contains(command)) return 2;
        if (Config.INSTANCE.getCommand_off().contains(command)) return 3;
        if (Config.INSTANCE.getCommand_inf().contains(command)) return 4;
        if (Config.INSTANCE.getCommand_callBack().contains(command)) return 5;
        if (Config.INSTANCE.getCommand_help().contains((command))) return 6;
        if (Config.INSTANCE.getCommand_addWhiteList().contains(command)) return 7;
        if (Config.INSTANCE.getCommand_addBlackList().contains(command)) return 8;
        if (Config.INSTANCE.getCommand_checkUserList().contains(command)) return 9;
        if (Config.INSTANCE.getCommand_shiftWhiteList().contains(command)) return 10;
        if (Config.INSTANCE.getCommand_shiftBlackList().contains(command)) return 11;
        if (Config.INSTANCE.getCommand_removeWhiteList().contains(command)) return 12;
        if (Config.INSTANCE.getCommand_removeBlackList().contains(command)) return 13;
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
            String apiUrl = buildGetUrl(keyWord, gMsg.getSubject().getId());
            String apiReturnedJson = SetuConnection.getJsonString(apiUrl);
            if ("".equals(apiReturnedJson)) {
                throw new SetuException(-2);
                //json为空，则连接出问题了
            }
            JsonClass jsonClass = JsonClass.getAPI(apiReturnedJson);
            //这里调用自己的Json类，把api返回来的json转成实体
            if (jsonClass.code != 0) {
                throw new SetuException(jsonClass.code);
                //api返回来的json状态码不为0。则拉取不到图，详细返回code请看lolicon API的使用说明
            }
            chain = new MessageChainBuilder().append(new SimpleServiceMessage(60, getXmlString(jsonClass))).build();
            //经典！！构造卡片消息，这样就不用先把图下载到服务器再上传到QQ了
        } catch (SetuException e) {
            chain = new MessageChainBuilder().append(e.getInf()).build();
            //所有异常汇总，由SetuException来处理异常信息为文本可读消息，发送到目的群组
        }
        return chain;
    }

    public void switchMode(int modeCode) {
        //切换模式
        Map<Long,List<Long>> temp= Config.INSTANCE.getGroupMode();
        List<Long> values;
        if(temp.containsKey(gMsg.getSubject().getId())){
            values = temp.get(gMsg.getSubject().getId());
            values.set(0, (long) modeCode);
        }else{
            values = new ArrayList<Long>();
            values.add((long) modeCode);
            values.add(0L);
            temp.put(gMsg.getSubject().getId(),values);
        }
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
        return Config.INSTANCE.getGroupMode().get(gMsg.getSubject().getId()).get(0) != 3;
    }

    public MessageChain checkStatus() {
        //查看机器人在当前群的限制
        MessageChain chain;
        try {
            if (Config.INSTANCE.getGroupMode().containsKey(gMsg.getSubject().getId())) {
                String mode = "淦！";
                List<Long> groupInf = Config.INSTANCE.getGroupMode().get(gMsg.getSubject().getId());
                int modeCode = Math.toIntExact(groupInf.get(0));
                switch (modeCode) {
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

                String tmp = groupInf.get(1) == 0 ? "已关闭" : groupInf.get(1)+"ms";
                String content =
                        "当前群:" + gMsg.getSubject().getId() +
                                "\n当前模式:" + mode +
                                "\n自动撤回：" + tmp ;
                chain = new MessageChainBuilder().append(content).build();
            } else {
                throw new SetuException(101);
            }

        } catch (SetuException e) {
            chain = new MessageChainBuilder().append(e.getInf()).build();
        }
        return chain;
    }

    public MessageChain setuHelp() {
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
                "\n添加白名单：" + Config.INSTANCE.getCommand_addWhiteList() +
                "\n添加黑名单：" + Config.INSTANCE.getCommand_addBlackList() +
                "\n移除白名单：" + Config.INSTANCE.getCommand_removeWhiteList() +
                "\n移除黑名单：" + Config.INSTANCE.getCommand_removeBlackList() +
                "\n白名单开关：" + Config.INSTANCE.getCommand_shiftWhiteList() +
                "\n黑名单开关：" + Config.INSTANCE.getCommand_shiftBlackList() +
                "\n查看名单详细：" + Config.INSTANCE.getCommand_checkUserList();
        chain = new MessageChainBuilder().append(help).build();
        return chain;
    }

    public void setRecall(Long time) {
        //设置自动撤回时间
        List<Long> temp = Config.INSTANCE.getGroupMode().get(gMsg.getSubject().getId());
        temp.set(1, time);
    }

    public void addList(String mode, Long userID) {
        //将用户添加到list
        if (Config.INSTANCE.getList().get(mode).contains(userID)) return;
        Set<Long> list = Config.INSTANCE.getList().get(mode);
        list.add(userID);
    }

    public MessageChain checkUserList() {
        MessageChain chain;
        String content = "名单状态：";
        content += "\n白名单：" + Config.INSTANCE.getListMode().get("whiteListMode") +
                "\n黑名单：" + Config.INSTANCE.getListMode().get("blackListMode") +
                "\n白名单列表：" + Config.INSTANCE.getList().get("whiteList") +
                "\n黑名单列表：" + Config.INSTANCE.getList().get("blackList");
        chain = new MessageChainBuilder().append(content).build();
        return chain;
    }

    public String buildGetUrl(String keyWord, Long groupID) {
        String buildUrl = Config.INSTANCE.getAddress();
        buildUrl += "?r18=" + Config.INSTANCE.getGroupMode().get(groupID);
        if (!("".equals(Config.INSTANCE.getApiKey()))) {
            buildUrl += "&apikey=" + Config.INSTANCE.getApiKey();
        }
        if (Config.INSTANCE.getWithProxyOrNot()) {
            buildUrl += "&proxy=" + Config.INSTANCE.getProxyAddress();
        }
        if (Config.INSTANCE.getSize1200()) {
            buildUrl += "&size1200=" + "true";
        }
        if (!("".equals(keyWord))) {
            buildUrl += "&keyword=" + keyWord;
        }
        buildUrl += "&num=" + Config.INSTANCE.getNum();
        return buildUrl;
    }

    public MessageChain shiftWhiteListMode() {
        Map<String, Boolean> temp = Config.INSTANCE.getListMode();
        temp.put("whiteListMode", !temp.get("whiteListMode"));
        return new MessageChainBuilder().append("哦～").build();
    }

    public MessageChain shiftBlackListMode() {
        Map<String, Boolean> temp = Config.INSTANCE.getListMode();
        temp.put("blackListMode", !temp.get("blackListMode"));
        return new MessageChainBuilder().append("哦～").build();
    }

    public MessageChain removeWhiteList(Long userID) {
        if (Config.INSTANCE.getList().get("whiteList").contains(userID)) {
            Config.INSTANCE.getList().get("whiteList").remove(userID);
        }
        return new MessageChainBuilder().append("哦～").build();
    }

    public MessageChain removeBlackList(Long userID) {
        if (Config.INSTANCE.getList().get("blackList").contains(userID)) {
            Config.INSTANCE.getList().get("blackList").remove(userID);
        }
        return new MessageChainBuilder().append("哦～").build();
    }

}
