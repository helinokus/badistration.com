package com.helinok.pzbad_registration.Enums;

public enum GameCategories {
    // Юношеские категории (Underage)
    MS_U11("MS U11 (Boys Singles Under 11)"),
    MS_U13("MS U13 (Boys Singles Under 13)"),
    MS_U15("MS U15 (Boys Singles Under 15)"),
    MS_U17("MS U17 (Boys Singles Under 17)"),
    MS_U19("MS U19 (Boys Singles Under 19)"),
    MS_U21("MS U21 (Men Singles Under 21)"),

    MS_U23("MS U23 (Men Singles Under 23)"),
    WS_U11("WS U11 (Girls Singles Under 11)"),
    WS_U13("WS U13 (Girls Singles Under 13)"),
    WS_U15("WS U15 (Girls Singles Under 15)"),
    WS_U17("WS U17 (Girls Singles Under 17)"),
    WS_U19("WS U19 (Girls Singles Under 19)"),
    WS_U21("WS U21 (Women Singles Under 21)"),
    WS_U23("WS U23 (Women Singles Under 23)"),

    MD_U11("MD U11 (Boys Doubles Under 11)"),
    MD_U13("MD U13 (Boys Doubles Under 13)"),
    MD_U15("MD U15 (Boys Doubles Under 15)"),
    MD_U17("MD U17 (Boys Doubles Under 17)"),
    MD_U19("MD U19 (Boys Doubles Under 19)"),
    MD_U21("MD U21 (Men Doubles Under 21)"),
    MD_U23("MD U23 (Men Doubles Under 23)"),

    WD_U11("WD U11 (Girls Doubles Under 11)"),
    WD_U13("WD U13 (Girls Doubles Under 13)"),
    WD_U15("WD U15 (Girls Doubles Under 15)"),
    WD_U17("WD U17 (Girls Doubles Under 17)"),
    WD_U19("WD U19 (Girls Doubles Under 19)"),
    WD_U21("WD U21 (Women Doubles Under 21)"),
    WD_U23("WD U23 (Women Doubles Under 23)"),

    XD_U11("XD U11 (Mixed Doubles Under 11)"),
    XD_U13("XD U13 (Mixed Doubles Under 13)"),
    XD_U15("XD U15 (Mixed Doubles Under 15)"),
    XD_U17("XD U17 (Mixed Doubles Under 17)"),
    XD_U19("XD U19 (Mixed Doubles Under 19)"),
    XD_U21("XD U21 (Mixed Doubles Under 21)"),
    XD_U23("XD U23 (Mixed Doubles Under 23)"),

    // Категории по уровню
    MS_OPEN("MS Open (Men Singles Open)"),
    MS_A("MS A (Men Singles A Class)"),
    MS_B("MS B (Men Singles B Class)"),
    MS_C("MS C (Men Singles C Class)"),

    WS_OPEN("WS Open (Women Singles Open)"),
    WS_A("WS A (Women Singles A Class)"),
    WS_B("WS B (Women Singles B Class)"),
    WS_C("WS C (Women Singles C Class)"),

    MD_OPEN("MD Open (Men Doubles Open)"),
    MD_A("MD A (Men Doubles A Class)"),
    MD_B("MD B (Men Doubles B Class)"),
    MD_C("MD C (Men Doubles C Class)"),

    WD_OPEN("WD Open (Women Doubles Open)"),
    WD_A("WD A (Women Doubles A Class)"),
    WD_B("WD B (Women Doubles B Class)"),
    WD_C("WD C (Women Doubles C Class)"),

    XD_OPEN("XD Open (Mixed Doubles Open)"),
    XD_A("XD A (Mixed Doubles A Class)"),
    XD_B("XD B (Mixed Doubles B Class)"),
    XD_C("XD C (Mixed Doubles C Class)"),

    // Возрастные категории (Over age)
    MS_18PLUS("MS 18+ (Men Singles 18 and over)"),
    MS_30PLUS("MS 30+ (Men Singles 30 and over)"),
    MS_40PLUS("MS 40+ (Men Singles 40 and over)"),
    MS_50PLUS("MS 50+ (Men Singles 50 and over)"),
    MS_60PLUS("MS 60+ (Men Singles 60 and over)"),
    MS_70PLUS("MS 70+ (Men Singles 70 and over)"),

    WS_18PLUS("WS 18+ (Women Singles 18 and over)"),
    WS_30PLUS("WS 30+ (Women Singles 30 and over)"),
    WS_40PLUS("WS 40+ (Women Singles 40 and over)"),
    WS_50PLUS("WS 50+ (Women Singles 50 and over)"),
    WS_60PLUS("WS 60+ (Women Singles 60 and over)"),
    WS_70PLUS("WS 70+ (Women Singles 70 and over)"),

    MD_18PLUS("MD 18+ (Men Doubles 18 and over)"),
    MD_30PLUS("MD 30+ (Men Doubles 30 and over)"),
    MD_40PLUS("MD 40+ (Men Doubles 40 and over)"),
    MD_50PLUS("MD 50+ (Men Doubles 50 and over)"),
    MD_60PLUS("MD 60+ (Men Doubles 60 and over)"),
    MD_70PLUS("MD 70+ (Men Doubles 70 and over)"),

    WD_18PLUS("WD 18+ (Women Doubles 18 and over)"),
    WD_30PLUS("WD 30+ (Women Doubles 30 and over)"),
    WD_40PLUS("WD 40+ (Women Doubles 40 and over)"),
    WD_50PLUS("WD 50+ (Women Doubles 50 and over)"),
    WD_60PLUS("WD 60+ (Women Doubles 60 and over)"),
    WD_70PLUS("WD 70+ (Women Doubles 70 and over)"),

    XD_18PLUS("XD 18+ (Mixed Doubles 18 and over)"),
    XD_30PLUS("XD 30+ (Mixed Doubles 30 and over)"),
    XD_40PLUS("XD 40+ (Mixed Doubles 40 and over)"),
    XD_50PLUS("XD 50+ (Mixed Doubles 50 and over)"),
    XD_60PLUS("XD 60+ (Mixed Doubles 60 and over)"),
    XD_70PLUS("XD 70+ (Mixed Doubles 70 and over)");

    private final String displayName;

    GameCategories(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}