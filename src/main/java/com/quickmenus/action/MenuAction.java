package com.quickmenus.action;
public record MenuAction(Type type, String argument) {
    public enum Type {
        CLOSE,
        MESSAGE,
        PROXY,
        SOUND,
        COMMAND,
        CONSOLE,
        PLAYER,
        PAGE_NEXT,
        PAGE_PREV,
        PAGE_GO,
        PERMISSION_CHECK,
        TOGGLE_VANISH,
        ECO_WITHDRAW,
        ECO_DEPOSIT,
        ECO_BALANCE_CHECK
    }
    public static MenuAction parse(String raw) {
        if (raw == null || raw.isBlank()) return null;
        raw = raw.trim();
        if (!raw.startsWith("[")) return null;
        int end = raw.indexOf(']');
        if (end == -1) return null;
        String token = raw.substring(1, end).toUpperCase();
        String arg   = end + 1 < raw.length() ? raw.substring(end + 2).trim() : "";
        return switch (token) {
            case "CLOSE"            -> new MenuAction(Type.CLOSE,            arg);
            case "MESSAGE"          -> new MenuAction(Type.MESSAGE,           arg);
            case "PROXY"            -> new MenuAction(Type.PROXY,             arg);
            case "SOUND"            -> new MenuAction(Type.SOUND,             arg);
            case "COMMAND"          -> new MenuAction(Type.COMMAND,           arg);
            case "CONSOLE"          -> new MenuAction(Type.CONSOLE,           arg);
            case "PLAYER"           -> new MenuAction(Type.PLAYER,            arg);
            case "PAGE_NEXT"        -> new MenuAction(Type.PAGE_NEXT,         arg);
            case "PAGE_PREV"        -> new MenuAction(Type.PAGE_PREV,         arg);
            case "PAGE_GO"          -> new MenuAction(Type.PAGE_GO,           arg);
            case "PERMISSION_CHECK" -> new MenuAction(Type.PERMISSION_CHECK,  arg);
            case "TOGGLE_VANISH"     -> new MenuAction(Type.TOGGLE_VANISH,      arg);
            case "ECO_WITHDRAW"      -> new MenuAction(Type.ECO_WITHDRAW,       arg);
            case "ECO_DEPOSIT"       -> new MenuAction(Type.ECO_DEPOSIT,        arg);
            case "ECO_BALANCE_CHECK" -> new MenuAction(Type.ECO_BALANCE_CHECK,  arg);
            default                  -> null;
        };
    }
}