# bot

一个机器人被以 `Bot` 对象描述。mirai 的交互入口点是 `Bot`。`Bot` 只可通过 [`BotFactory`](https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/BotFactory.kt#L22-L87) 内的 `newBot` 方法获得



```java
// Java
Bot bot = BotFactory.INSTANCE.newBot(    );
```



## 设备信息

Bot 默认使用全随机的设备信息。**在更换账号地点时候使用随机设备信息可能会导致无法登录**，当然，**成功登录时使用的设备信息也可以保存后在新的设备使用**。

```json
fileBasedDeviceInfo() // 存储为 "device.json" 
// 或
fileBasedDeviceInfo("myDeviceInfo.json") // 存储为 "myDeviceInfo.json"
```

#### 切换登录协议

Mirai 支持多种登录协议：`ANDROID_PHONE`，`ANDROID_PAD`，`ANDROID_WATCH`，默认使用 `ANDROID_PHONE`。

若登录失败，可尝试切换协议。**但注意部分功能在部分协议上不受支持**

#### 启用列表缓存

Mirai 在启动时会拉取全部好友列表和群成员列表。当账号拥有过多群时登录可能缓慢，开启列表缓存会大幅加速登录过程。

```java
// Java
contactListCache.setFriendListCacheEnabled(true) // 开启好友列表缓存
contactListCache.setGroupMemberListCacheEnabled(true) // 开启群成员列表缓存
contactListCache.setSaveIntervalMillis(60000) // 可选设置有更新时的保存时间间隔, 默认 60 秒
```

### 获取当前所有 `Bot` 实例

在登录后 `Bot` 实例会被自动记录。可在 `Bot.instances` 获取到当前**在线**的所有 `Bot` 列表。



# contact

| 类型           | 描述                                                        | 最低支持的版本 |
| -------------- | ----------------------------------------------------------- | -------------- |
| `ContactOrBot` | `Contact` 和 `Bot` 的公共接口                               | 2.0            |
| `OtherClient`  | Bot 的*其他客户端*, 如 "我的 iPad", "我的电脑"              | 2.0            |
| `Bot`          | 机器人对象                                                  | 2.0            |
| `Contact`      | 联系人对象, 即所有的群, 好友, 陌生人, 群成员等              | 2.0            |
| `Group`        | 群对象                                                      | 2.0            |
| `User`         | 用户对象, 即 "个人". 包含好友, 陌生人, 群成员, 临时会话用户 | 2.0            |
| `Friend`       | 好友对象                                                    | 2.0            |
| `Stranger`     | 陌生人对象                                                  | 2.0            |
| `Member`       | 群成员对象, 属于一个 `Group`.                               | 2.0            |

基于面向对象的设计，可直接获取 `Contact` 的属性如 `nick`，`permission`。请在实践时在接口源码内查看更清晰的说明。

要主动发送一条消息，总是调用 `Contact.sendMessage(message)`*（`message` 在后文介绍）*。

可通过 `Bot.getFriend`， `Bot.getGroup`，`Bot.getStranger` 获取相关对象，也可以通过事件获取 *（事件在后文介绍）*。



# Event

## 事件系统

Mirai 以事件驱动。

每个事件都实现接口 [`Event`](https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/event/Event.kt#L21-L62)，且继承 `AbstractEvent`。
 实现 `CancellableEvent` 的事件可以被取消（`CancellableEvent.cancel`）

```java
// 创建监听
Listener listener = GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, event -> {
    event.getSubject().sendMessage("Hello!");
})

listener.complete(); // 停止监听 
```

## 事件列表一览

提示:

- 在 IntelliJ 平台双击 shift 可输入类名进行全局搜索
- 在 IntelliJ 平台, 按 alt + 7 可打开文件的结构, [效果图](https://github.com/mamoe/mirai/blob/dev/.github/EZSLAB`K@YFFOW47{090W8B.png)

- - ``

### 

### [Bot](https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/event/events/bot.kt)

- Bot 登录完成: BotOnlineEvent
- Bot 离线: BotOfflineEvent
  - 主动: Active
  - 被挤下线: Force
  - 被服务器断开或因网络问题而掉线: Dropped
  - 服务器主动要求更换另一个服务器: RequireReconnect
- Bot 重新登录: BotReloginEvent
- Bot 头像改变: BotAvatarChangedEvent
- Bot 昵称改变: BotNickChangedEvent
- Bot 被戳: BotNudgedEvent

### 

### 消息

- 被动收到消息：

  MessageEvent

  - 群消息：GroupMessageEvent
  - 好友消息：FriendMessageEvent
  - 群临时会话消息：GroupTempMessageEvent
  - 陌生人消息：StrangerMessageEvent
  - 其他客户端消息：OtherClientMessageEvent

- 主动发送消息前: 

  MessagePreSendEvent

  - 群消息: GroupMessagePreSendEvent
  - 好友消息: FriendMessagePreSendEvent
  - 群临时会话消息: GroupTempMessagePreSendEvent
  - 陌生人消息：StrangerMessagePreSendEvent
  - 其他客户端消息：OtherClientMessagePreSendEvent

- 主动发送消息后: 

  MessagePostSendEvent

  - 群消息: GroupMessagePostSendEvent
  - 好友消息: FriendMessagePostSendEvent
  - 群临时会话消息: GroupTempMessagePostSendEvent
  - 陌生人消息：StrangerMessagePostSendEvent
  - 其他客户端消息：OtherClientMessagePostSendEvent

- 消息撤回: 

  MessageRecallEvent

  - 好友撤回: FriendRecall
  - 群撤回: GroupRecall
  - 群临时会话撤回: TempRecall

- 图片上传前: [BeforeImageUploadEvent](https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/event/events/ImageUploadEvent.kt)

- 图片上传完成: 

  ImageUploadEvent

  - 图片上传成功: Succeed
  - 图片上传失败: Failed

- 戳一戳: [NudgeEvent](https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/event/events/NudgeEvent.kt)

### 

### [群](https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/event/events/group.kt)

- 机器人被踢出群或在其他客户端主动退出一个群: BotLeaveEvent
  - 机器人主动退出一个群: Active
  - 机器人被管理员或群主踢出群: Kick
- 机器人在群里的权限被改变: BotGroupPermissionChangeEvent
- 机器人被禁言: BotMuteEvent
- 机器人被取消禁言: BotUnmuteEvent
- 机器人成功加入了一个新群: BotJoinGroupEvent

#### 

#### 群设置

- 群设置改变: GroupSettingChangeEvent
  - 群名改变: GroupNameChangeEvent
  - 入群公告改变: GroupEntranceAnnouncementChangeEvent
  - 全员禁言状态改变: GroupMuteAllEvent
  - 匿名聊天状态改变: GroupAllowAnonymousChatEvent
  - 允许群员邀请好友加群状态改变: GroupAllowMemberInviteEvent

#### 

#### 群成员

##### 

##### 成员列表变更

- 成员已经加入群: MemberJoinEvent
  - 成员被邀请加入群: Invite
  - 成员主动加入群: Active
- 成员已经离开群: MemberLeaveEvent
  - 成员被踢出群: Kick
  - 成员主动离开群: Quit
- 一个账号请求加入群: MemberJoinRequestEvent
- 机器人被邀请加入群: BotInvitedJoinGroupRequestEvent

##### 

##### 名片和头衔

- 成员群名片改动: MemberCardChangeEvent
- 成员群头衔改动: MemberSpecialTitleChangeEvent

##### 

##### 成员权限

- 成员权限改变: MemberPermissionChangeEvent

##### 

##### 动作

- 群成员被禁言: MemberMuteEvent
- 群成员被取消禁言: MemberUnmuteEvent

### 

### [好友](https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/event/events/friend.kt)

- 好友昵称改变: FriendRemarkChangeEvent
- 成功添加了一个新好友: FriendAddEvent
- 好友已被删除: FriendDeleteEvent
- 一个账号请求添加机器人为好友: NewFriendRequestEvent
- 好友头像改变: FriendAvatarChangedEvent
- 好友昵称改变: FriendNickChangedEvent
- 好友输入状态改变: FriendInputStatusChangedEvent

## 事件通道

[事件通道](https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/event/EventChannel.kt)是监听事件的入口。 **在不同的事件通道中可以监听到不同类型的事件**。

### 

### 获取事件通道

[`GlobalEventChannel`](https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/event/GlobalEventChannel.kt) 是最大的通道：所有的事件都可以在 [`GlobalEventChannel`](https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/event/GlobalEventChannel.kt) 监听到。**因此，[`GlobalEventChannel`](https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/event/GlobalEventChannel.kt) 会包含来自所有 `Bot` 实例的事件。**

通常不会直接使用 [`GlobalEventChannel`](https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/event/GlobalEventChannel.kt)，而是使用经过 [通道操作](https://github.com/mamoe/mirai/blob/dev/docs/Events.md#通道操作) 操作的子通道。

### 过滤

`GlobalEventChannel` 包含任何 `Event`，可以通过 `EventChannel.filter` 过滤得到一个只包含期望的事件的 `EventChannel`。

```java
EventChannel channel = GlobalEventChannel.INSTANCE.filter(ev -> ev instanceof BotEvent && ((BotEvent) ev).bot.id == 123456); // 筛选来自某一个 Bot 的事件
```

## 在 `EventChannel` 监听事件

使用：

- `EventChannel.subscribe`：监听事件并自行觉得何时停止
- `EventChannel.subscribeAlways`：一直监听事件
- `EventChannel.subscribeOnce`：只监听一次事件

```
bot.eventChannel.subscribeAlways<GroupMessageEvent> { event ->
    // this: GroupMessageEvent
    // event: GroupMessageEvent
    
    subject.sendMessage("Hello from mirai!")
}
bot.eventChannel.subscribeAlways(GroupMessageEvent.class, event -> {
    event.getSubject().sendMessage("Hello from mirai!");
})
```

> 实现细节可查看源码内注释。



## 实现事件

只要实现接口 `Event` 并继承 `AbstractEvent` 的对象就可以被广播。

要广播一个事件，使用 `Event.broadcast()`（Kotlin）或 `EventKt.broadcast(Event)`（Java）。



# Message



在 Contacts 章节提到，要发送消息，使用 `Contact.sendMessage(Message)`。`Message` 架构如下图所示。

[![img](https://camo.githubusercontent.com/d70d6171058595d6be0e2d0938052d250a239f5c23ad786fd22d1fc0d1b3c648/68747470733a2f2f6d65726d6169642e696e6b2f696d672f65794a6a6232526c496a6f695932786863334e456157466e636d467458473563626d4e7359584e7a4945316c63334e685a32564461474670626c78755457567a6332466e5a554e6f59576c7549446f6754476c7a644835546157356e6247564e5a584e7a5957646c666c78755847354e5a584e7a5957646c504877744c55316c63334e685a32564461474670626c78755457567a6332466e5a5478384c5331546157356e6247564e5a584e7a5957646c58473563626b316c63334e685a32564461474670626942764c53306755326c755a32786c5457567a6332466e5a567875584735546157356e6247564e5a584e7a5957646c504877744c55316c63334e685a325644623235305a573530584735546157356e6247564e5a584e7a5957646c504877744c55316c63334e685a32564e5a5852685a47463059567875584734694c434a745a584a7459576c6b496a7037496e526f5a57316c496a6f695a47566d5958567364434a394c434a3163475268644756465a476c30623349694f6d5a6862484e6c6651)](https://mermaid-js.github.io/mermaid-live-editor/#/edit/eyJjb2RlIjoiY2xhc3NEaWFncmFtXG5cbmNsYXNzIE1lc3NhZ2VDaGFpblxuTWVzc2FnZUNoYWluIDogTGlzdH5TaW5nbGVNZXNzYWdlflxuXG5NZXNzYWdlPHwtLU1lc3NhZ2VDaGFpblxuTWVzc2FnZTx8LS1TaW5nbGVNZXNzYWdlXG5cbk1lc3NhZ2VDaGFpbiBvLS0gU2luZ2xlTWVzc2FnZVxuXG5TaW5nbGVNZXNzYWdlPHwtLU1lc3NhZ2VDb250ZW50XG5TaW5nbGVNZXNzYWdlPHwtLU1lc3NhZ2VNZXRhZGF0YVxuXG4iLCJtZXJtYWlkIjp7InRoZW1lIjoiZGVmYXVsdCJ9LCJ1cGRhdGVFZGl0b3IiOmZhbHNlfQ)

`SingleMessage` 表示单个消息元素。
 `MessageChain`（消息链） 是 `List<SingleMessage>`。主动发送的消息和从服务器接收消息都是 `MessageChain`。

## 消息类型

Mirai 支持富文本消息。

*单个消息元素（`SingleMessage`）* 分为 *内容（`MessageContent`）* 和 *元数据（`MessageMetadata`）*。

实践中，消息内容和消息元数据会混合存在于消息链中。

### 

### 内容

*内容（`MessageContent`）* 即为 *纯文本*、*提及某人*、*图片*、*语音* 和 *音乐分享* 等**有内容**的数据，一条消息中必须包含内容才能发送。

### 

### 元数据

*元数据（`MessageMetadata`）* 包含 *来源*、*引用回复* 和 *秀图标识* 等。

- *消息来源*（`MessageSource`）存在于每条消息中，包含唯一识别信息，用于撤回和引用回复的定位。
- *引用回复*（`QuoteReply`）若存在，则会在客户端中解析为本条消息引用了另一条消息。
- *秀图标识*（`ShowImageFlag`）若存在，则表明这条消息中的图片是以秀图发送（QQ 的一个功能）。

元数据与内容的区分就在于，一条消息没有元数据也能显示，但一条消息不能没有内容。**元数据是消息的属性**。

> 回到 [目录](https://github.com/mamoe/mirai/blob/dev/docs/Messages.md#目录)

## 

## 消息元素

Mirai 支持多种消息类型。

消息拥有三种转换到字符串的表示方式。

| 方法                     | 解释                                                         |
| ------------------------ | ------------------------------------------------------------ |
| `serializeToMiraiCode()` | 对应的 Mirai 码. 消息的一种序列化方式，格式为 `[mirai:TYPE:PROP]`，其中 `TYPE` 为消息类型, `PROP` 为属性 |
| `contentToSting()`       | QQ 对话框中以纯文本方式会显示的消息内容。无法用纯文字表示的消息会丢失信息，如任何图片都是 `[图片]` |
| `toString()`             | Java 对象的 `toString()`，会尽可能包含多的信息用于调试作用，**行为可能不确定** |

各类型消息元素及其 `contentToString()` 如下表格所示。

| [`MessageContent`](https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/message/data/SingleMessage.kt) 类型 | 解释                     | `contentToString()`     | 最低支持的版本 |
| ------------------------------------------------------------ | ------------------------ | ----------------------- | -------------- |
| [`PlainText`](https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/message/data/PlainText.kt) | 纯文本                   | `$content`              | 2.0            |
| [`Image`](https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/message/data/Image.kt) | 自定义图片               | `[图片]`                | 2.0            |
| [`At`](https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/message/data/At.kt) | 提及某人                 | `@$target`              | 2.0            |
| [`AtAll`](https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/message/data/AtAll.kt) | 提及全体成员             | `@全体成员`             | 2.0            |
| [`Face`](https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/message/data/Face.kt) | 原生表情                 | `[表情对应的中文名]`    | 2.0            |
| [`FlashImage`](https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/message/data/FlashImage.kt) | 闪照                     | `[闪照]`                | 2.0            |
| [`PokeMessage`](https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/message/data/PokeMessage.kt) | 戳一戳消息（消息非动作） | `[戳一戳]`              | 2.0            |
| [`VipFace`](https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/message/data/VipFace.kt) | VIP 表情                 | `[${kind.name}]x$count` | 2.0            |
| [`LightApp`](https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/message/data/RichMessage.kt) | 小程序                   | `$content`              | 2.0            |
| [`Voice`](https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/message/data/Voice.kt) | 语音                     | `[语音消息]`            | 2.0            |
| [`MarketFace`](https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/message/data/MarketFace.kt) | 商城表情                 | `[表情对应的中文名]`    | 2.0            |
| [`ForwardMessage`](https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/message/data/ForwardMessage.kt) | 合并转发                 | `[转发消息]`            | 2.0  *(1)*     |
| [`SimpleServiceMessage`](https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/message/data/RichMessage.kt) | （不稳定）服务消息       | `$content`              | 2.0            |
| [`MusicShare`](https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/message/data/MusicShare.kt) | 音乐分享                 | `[分享]曲名`            | 2.1            |
| [`Dice`](https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/message/data/Dice.kt) | 骰子                     | `[骰子:$value]`         | 2.5            |
| [`FileMessage`](https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/message/data/FileMessage.kt) | 文件消息                 | `[文件]文件名称`        | 2.5            |

> *(1)*: [`ForwardMessage`](https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/message/data/ForwardMessage.kt) 在 2.0 支持发送, 在 2.3 支持接收

| [`MessageMetadata`](https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/message/data/SingleMessage.kt) 类型 | 解释           | 最低支持的版本 |
| ------------------------------------------------------------ | -------------- | -------------- |
| [`MessageSource`](https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/message/data/MessageSource.kt) | 消息来源元数据 | 2.0            |
| [`QuoteReply`](https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/message/data/QuoteReply.kt) | 引用回复       | 2.0            |
| [`ShowImageFlag`](https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/message/data/ShowImageFlag.kt) | 秀图标识       | 2.2            |
| [`RichMessageOrigin`](https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/message/data/RichMessageOrigin.kt) | 富文本消息源   | 2.3            |

### 

### 用法

只需要得到各种类型 `Message` 的实例就可以使用，可以直接发送（`Contact.sendMessage`）也可以连接到消息链中（`Message.plus`）。

可在上文表格中找到需要的类型并在源码内文档获取更多实践上的帮助。

> 回到 [目录](https://github.com/mamoe/mirai/blob/dev/docs/Messages.md#目录)

## 

## 消息链

前文已经介绍消息链，这里简略介绍消息链的使用。详细的使用请查看源码内注释。

### 

### 发送消息

在 [Contacts 章节](https://github.com/mamoe/mirai/blob/dev/docs/Contacts.md) 提到，要发送消息使用 `Contact.sendMessage`。`Contact.sendMessage` 的定义是：

```
 suspend fun sendMessage(message: Message): MessageReceipt<Contact>
```

要发送字符串消息，使用：（第一部分是 Kotlin，随后是 Java，下同）

```
contact.sendMessage("Hello!")
contact.sendMessage("Hello!");
```

发送字符串实际上是在发送纯文本消息。上面的代码相当于：

```
contact.sendMessage(PlainText("Hello!"))
contact.sendMessage(new PlainText("Hello!"));
```

要发送多元素消息，可将消息使用 `plus` 操作连接：

```
contact.sendMessage(PlainText("你要的图片是") + Image("{f8f1ab55-bf8e-4236-b55e-955848d7069f}.png")) // 一个纯文本加一个图片
contact.sendMessage(new PlainText("你要的图片是：").plus(Image.fromId("{f8f1ab55-bf8e-4236-b55e-955848d7069f
```

### 构造消息链

更复杂的消息则需要构造为消息链。

#### 在 Java 构造消息链

| 定义                                                         |
| ------------------------------------------------------------ |
| `public static MessageChain newChain(Iterable<Message> iterable)` |
| `public static MessageChain newChain(Message iterable...)`   |
| `public static MessageChain newChain(Iterator<Message> iterable...)` |

方法都位于 `net.mamoe.mirai.message.data.MessageUtils`。

使用 `newChain`：

```
MessageChain chain = MessageUtils.newChain(new PlainText("Hello"), Image.fromId("{f8f1ab55-bf8e-4236-b55e-955848d7069f}.png"));
```

使用 `MessageChainBuilder`:

```
MessageChain chain = new MessageChainBuilder()
    .append(new PlainText("string"))
    .append("string") // 会被构造成 PlainText 再添加, 相当于上一行
    .append(AtAll.INSTANCE)
    .append(Image.fromId("{f8f1ab55-bf8e-4236-b55e-955848d7069f}.png"))
    .build();
```

该示例中 `+` 是位于 `MessageChainBuilder` 的 `Message.unaryPlus` 扩展。使用 `+` 和使用 `add` 是相等的。

### 作为字符串处理消息

通常要把消息作为字符串处理，在 Kotlin 使用 `message.content` 或在 Java 使用 `message.contentToString()`。

获取到的字符串表示只包含各 [`MessageContent`](https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/message/data/SingleMessage.kt) 以官方风格显示的消息内容。如 `"你本次测试的成绩是[图片]"`、`[语音]`、`[微笑]`

### 

### 元素唯一性

部分元素只能单一存在于消息链中。这样的元素实现接口 [`ConstrainSingle`](https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/message/data/ConstrainSingle.kt)。

唯一的元素例如 *消息元数据* [`MessageSource`](https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/message/data/MessageSource.kt)，在连接时，新的（右侧）元素会替换旧的（左侧）元素。如：

```
val source1: MessageSource
val source2: MessageSource

val chain: MessageChain = source1 + source2
// 结果 chain 只包含一个元素，即右侧的 source2。
```

元素唯一性的识别基于 [`MessageKey`](https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/message/data/MessageKey.kt)。[`MessageKey`](https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/message/data/MessageKey.kt) 拥有多态机制。元素替换时会替换。如 [`HummerMessage`](https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/message/data/HummerMessage.kt) 的继承关系

```
              MessageContent
                    ↑
              HummerMessage
                    ↑
       +------------+-------------+------------+
       |            |             |            |
 PokeMessage     VipFace      FlashImage      ...
```

当连接一个 [`VipFace`](https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/message/data/VipFace.kt) 到一个 [`MessageChain`](https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/message/data/MessageChain.kt) 时，由于 [`VipFace`](https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/message/data/VipFace.kt) 最上层为 `MessageContent`，消息链中第一个 `MessageContent` 会被（保留顺序地）替换为 [`VipFace`](https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/message/data/VipFace.kt)，其他所有 `MessageContent` 都会被删除。

```
val chain = messageChainOf(quoteReply, plainText, at, atAll) // quoteReply 是 MessageMetadata, 其他三个都是 MessageContent
val result = chain + VipFace(VipFace.AiXin, 1) // VipFace 是 ConstrainSingle，最上层键为 MessageContent，因此替换所有的 MessageContent
// 结果为 [quoteReply, VipFace]
```

### 

### 获取消息链中的消息元素

#### 

#### A. 筛选 List

[`MessageChain`](https://github.com/mamoe/mirai/blob/dev/mirai-core-api/src/commonMain/kotlin/message/data/MessageChain.kt) 继承接口 `List<SingleMessage>`。

```
val image: Image? = chain.filterIsInstance<Image>().firstOrNull()
Image image = (Image) chain.stream().filter(Image.class::isInstance).findFirst().orElse(null);
```

在 Kotlin 要获取第一个指定类型实例还可以使用快捷扩展。

```
val image: Image? = chain.findIsInstance<Image>()
val image: Image = chain.firstIsInstance<Image>() // 不存在时 NoSuchElementException
```

#### 

#### B. 获取唯一消息

如果要获取 `ConstrainSingle` 的消息元素，可以快速通过键获得。

```
val quote: QuoteReply? = chain[QuoteReply] // 类似 Map.get
val quote: QuoteReply = chain.getOrFail(QuoteReply) // 不存在时 NoSuchElementException
QuoteReply quote = chain.get(QuoteReply.Key);
```

> 这是因为 `MessageKey` 一般都以消息元素的 `companion object` 实现

#### 

#### C. 使用属性委托

可在 Kotlin 使用属性委托。这样的方法与上述方法在性能上等价。

```
val image: Image by chain // 不存在时 NoSuchElementException
val image: Image? by chain.orNull()
val image: Image? by chain.orElse { /* 返回一个 Image */ }
```

### 

### 序列化

消息可以序列化为 JSON 字符串，使用 `MessageChain.serializeToJsonString` 和 `MessageChain.deserializeFromJsonString`。