package com.binbla.lspBot

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
object Config : AutoSavePluginConfig("LspConfig") {
    @ValueDescription("机器人所有者")
    val owner: Long by value(905908099L)

    @ValueDescription("机器人ID")
    val botID: MutableList<Long> by value(mutableListOf(1449427875L, 725541084L))

    @ValueDescription("lolicon地址")
    val address: String by value("https://api.lolicon.app/setu/")

    @ValueDescription("lolicon apikey")
    var apiKey: String by value("23070864607a3ae45adbd7")

    @ValueDescription("群号：是否开启R18（0青少年1壮年2全都要3关闭） 未在列表则不开启LSP功能")
    var groupMode: MutableMap<String, Int> by value(mutableMapOf("221789270" to 0, "903743026" to 0))

    @ValueDescription("撤回时间：0不撤回，1000一秒撤回")
    var recallTime: Long by value(0L)

    @ValueDescription("发送图片的张数,这个我相信没必要一次发两张,所以不绑定群了,不要修改！")
    val num: Int by value(1)

    @ValueDescription("限制图片大小（过大的图会很慢")
    var size1200: Boolean by value(true)

    @ValueDescription("用户列表黑名单(true)or白名单(false)")
    var usesListMode: Boolean by value(true)

    @ValueDescription("用户列表，所有群通用")
    var userList: MutableList<Long> by value(mutableListOf(123456789L, 987654321L))

    @ValueDescription("设置代理")
    var withProxyOrNot: Boolean by value(false)

    @ValueDescription("代理地址，别乱来。先翻翻lolicon的说明")
    var proxyAddress: String by value("i.pixiv.cat")

    @ValueDescription("浏览器User agent")
    var userAgent: String by value("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; QQBrowser/7.0.3698.400)")

    @ValueDescription("指令")
    var command_help: MutableList<String> by value(mutableListOf("帮助", "help"))
    var command_get: MutableList<String> by value(mutableListOf("lsp", "LSP", "色图时间", "涩图时间", "涩图来", "色图来"))
    var command_off: MutableList<String> by value(mutableListOf("关闭插件", "封印"))
    var command_inf: MutableList<String> by value(mutableListOf("状态"))
    var command_mode0: MutableList<String> by value(mutableListOf("普通模式", "青少年模式", "儿童模式"))
    var command_mode1: MutableList<String> by value(mutableListOf("R18模式", "青壮年模式", "成人模式"))
    var command_mode2: MutableList<String> by value(mutableListOf("我全都要", "来者不拒", "混合模式", "关怀模式"))
    var command_callBack: MutableList<String> by value(mutableListOf("限时撤回"))
    var command_addUserList: MutableList<String> by value(mutableListOf("ban", "禁", "禁止", "放出", "允许"))
    var command_checkUserList: MutableList<String> by value(mutableListOf("查看名单"))
    var command_shiftListMode: MutableList<String> by value(mutableListOf("切换名单模式"))
}
