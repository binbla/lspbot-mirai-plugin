package com.binbla.botSetu

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

/**
 * >ClassName Config.java
 * >Description Mirai官方说的数据类型建议Java用户混用Kotlin
 * >Author binbla
 * >Version 1.0.0
 * >CreateTime 2021-04-18  22:27
 */
object Config : AutoSavePluginConfig("SetuConfig") {
    @ValueDescription("机器人所有者")
    val owner: Long by value(905908099L)

    @ValueDescription("机器人ID")
    val botID: MutableList<Long> by value(mutableListOf(1449427875L, 725541084L))

    @ValueDescription("群号：\n" +
            "\t- 模式（0青少年1壮年2全都要3关闭）\n" +
            "\t- 撤回时间（ms）")
    var groupMode: MutableMap<Long,List<Long>> by value(mutableMapOf(221789270L to listOf<Long>(0L,60000L)))

    @ValueDescription("名单模式开关")
    var listMode:MutableMap<String,Boolean> by value(mutableMapOf("whiteListMode" to false,"blackListMode" to false))

    @ValueDescription("控制名单")
    var list:MutableMap<String,MutableSet<Long>> by value(mutableMapOf(
        "whiteList" to mutableSetOf<Long>(1449427875L,905908099L),
        "blackList" to mutableSetOf<Long>(123456789L)))

    @ValueDescription("lolicon地址")
    val address: String by value("https://api.lolicon.app/setu/")

    @ValueDescription("lolicon apikey")
    var apiKey: String by value("23070864607a3ae45adbd7")

    @ValueDescription("发送图片的张数,暂时不要修改！")
    val num: Int by value(1)

    @ValueDescription("限制图片大小（过大的图会很慢")
    val size1200: Boolean by value(true)

    @ValueDescription("设置代理")
    val withProxyOrNot: Boolean by value(false)

    @ValueDescription("代理地址，别乱来。先翻翻lolicon的说明")
    val proxyAddress: String by value("i.pixiv.cat")

    @ValueDescription("浏览器User agent")
    val userAgent: String by value("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; QQBrowser/7.0.3698.400)")

    @ValueDescription("指令")
    var command_help: MutableList<String> by value(mutableListOf("help","帮助"))
    var command_get: MutableList<String> by value(mutableListOf("lsp","色图","涩图","给我色图"))
    var command_off: MutableList<String> by value(mutableListOf("setuoff","关闭插件", "封印"))
    var command_inf: MutableList<String> by value(mutableListOf("status","状态"))
    var command_mode0: MutableList<String> by value(mutableListOf("普通模式", "青少年模式"))
    var command_mode1: MutableList<String> by value(mutableListOf("R18模式", "青壮年模式"))
    var command_mode2: MutableList<String> by value(mutableListOf("混合模式", "关怀模式"))
    var command_callBack: MutableList<String> by value(mutableListOf("recall","限时撤回"))
    var command_shiftWhiteList:MutableList<String> by value(mutableListOf("whiteList","白名单"))
    var command_shiftBlackList:MutableList<String> by value(mutableListOf("blackList","黑名单"))
    var command_addWhiteList: MutableList<String> by value(mutableListOf("ban", "加入黑名单"))
    var command_removeWhiteList:MutableList<String> by value(mutableListOf("移除黑名单"))
    var command_addBlackList:MutableList<String> by value(mutableListOf("allow","加入白名单"))
    var command_removeBlackList:MutableList<String> by value(mutableListOf("移除白名单"))
    var command_checkUserList: MutableList<String> by value(mutableListOf("checklist","查看名单"))
}
