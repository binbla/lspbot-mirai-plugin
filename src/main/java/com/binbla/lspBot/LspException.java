package com.binbla.lspBot;

/**
 * >ClassName LSPException.java
 * >Description 异常处理模块，从消息发送出去。
 * >Author binbla
 * >Version 1.0.0
 * >CreateTime 2021-04-19  22:46
 */
public class LspException extends Exception {
    Integer code;

    LspException(int code) {
        this.code = code;
    }

    public String getInf() {
        switch (code) {
            case 100:
                return "喵媌瞄？？？";
            case 101:
                return "汝所在群组没开启LSP功能";
            case 102:
                return "咳～...忒！你谁啊，本喵凭啥听你的!";
            case 103:
                return "你在瞎BB个啥？";
            case -2:
                return "淦哦！lolicon没连接上。。。";
            case -1:
                return "内部错误";
            case 0:
                return "成功";
            case 401:
                return "APIKEY不存在或被BAN";
            case 403:
                return "由于不规范的操作而被拒绝调用";
            case 404:
                return "找不到符合关键字的[色]图";
            case 429:
                return "达到调用额度限制";
            default:
                return "未知错误，速速通知bla修复";
        }
    }
}
