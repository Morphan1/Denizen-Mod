package com.morphanone.denizenmod.utilities;

import com.denizenscript.denizencore.utilities.AsciiMatcher;
import net.minecraft.ChatFormatting;

import java.util.Objects;
import java.util.regex.Pattern;

public class AnsiChatFormatting {
    public static final char ESC_CHAR = '\u001B';
    public static final String ANSI_CODE_FORMAT = ESC_CHAR + "[%sm"; // https://en.wikipedia.org/wiki/ANSI_escape_code#SGR_(Select_Graphic_Rendition)_parameters
    public static final String ANSI_RGB_FORMAT = String.format(ANSI_CODE_FORMAT, "38;2;%d;%d;%d"); // https://en.wikipedia.org/wiki/ANSI_escape_code#24-bit

    public static final Pattern STRIP_FORMATTING_PATTERN = Pattern.compile("\u001B\\[\\d{1,2}(?:;\\d{0,3}){0,4}m");

    public static final String BLACK = createFromColor(ChatFormatting.BLACK);
    public static final String DARK_BLUE = createFromColor(ChatFormatting.DARK_BLUE);
    public static final String DARK_GREEN = createFromColor(ChatFormatting.DARK_GREEN);
    public static final String DARK_AQUA = createFromColor(ChatFormatting.DARK_AQUA);
    public static final String DARK_RED = createFromColor(ChatFormatting.DARK_RED);
    public static final String DARK_PURPLE = createFromColor(ChatFormatting.DARK_PURPLE);
    public static final String GOLD = createFromColor(ChatFormatting.GOLD);
    public static final String GRAY = createFromColor(ChatFormatting.GRAY);
    public static final String DARK_GRAY = createFromColor(ChatFormatting.DARK_GRAY);
    public static final String BLUE = createFromColor(ChatFormatting.BLUE);
    public static final String GREEN = createFromColor(ChatFormatting.GREEN);
    public static final String AQUA = createFromColor(ChatFormatting.AQUA);
    public static final String RED = createFromColor(ChatFormatting.RED);
    public static final String LIGHT_PURPLE = createFromColor(ChatFormatting.LIGHT_PURPLE);
    public static final String YELLOW = createFromColor(ChatFormatting.YELLOW);
    public static final String WHITE = createFromColor(ChatFormatting.WHITE);
    public static String OBFUSCATED = fromBasicCode("5");
    public static String BOLD = fromBasicCode("1");
    public static String STRIKETHROUGH = fromBasicCode("9");
    public static String UNDERLINE = fromBasicCode("4");
    public static String ITALIC = fromBasicCode("3");
    public static String RESET = fromBasicCode("");

    /**
     * For internal use. Use {@link #convert(ChatFormatting)} instead.
     */
    public static String createFromColor(ChatFormatting chatFormatting) {
        return fromRGB(Objects.requireNonNull(chatFormatting.getColor()));
    }

    public static String convert(ChatFormatting chatFormatting) {
        return switch (chatFormatting) {
            case BLACK -> BLACK;
            case DARK_BLUE -> DARK_BLUE;
            case DARK_GREEN -> DARK_GREEN;
            case DARK_AQUA -> DARK_AQUA;
            case DARK_RED -> DARK_RED;
            case DARK_PURPLE -> DARK_PURPLE;
            case GOLD -> GOLD;
            case GRAY -> GRAY;
            case DARK_GRAY -> DARK_GRAY;
            case BLUE -> BLUE;
            case GREEN -> GREEN;
            case AQUA -> AQUA;
            case RED -> RED;
            case LIGHT_PURPLE -> LIGHT_PURPLE;
            case YELLOW -> YELLOW;
            case WHITE -> WHITE;
            case OBFUSCATED -> OBFUSCATED;
            case BOLD -> BOLD;
            case STRIKETHROUGH -> STRIKETHROUGH;
            case UNDERLINE -> UNDERLINE;
            case ITALIC -> ITALIC;
            case RESET -> RESET;
        };
    }

    public static AsciiMatcher HEX_MATCHER = new AsciiMatcher("0123456789abcdefABCDEF");

    public static String fromHexRGB(String hex) {
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }
        if (hex.length() == 6 && HEX_MATCHER.isOnlyMatches(hex)) {
            return fromRGB(Integer.parseInt(hex, 16));
        }
        return null;
    }

    public static String fromRGB(int color) {
        return fromRGB(
                color >> 16 & 0xFF,
                color >> 8 & 0xFF,
                color & 0xFF
        );
    }

    public static String fromRGB(int r, int g, int b) {
        return String.format(ANSI_RGB_FORMAT, r, g, b);
    }

    public static String fromBasicCode(String code) {
        return String.format(ANSI_CODE_FORMAT, code);
    }

    public static String stripFormatting(String text) {
        return text == null ? null : STRIP_FORMATTING_PATTERN.matcher(text).replaceAll("");
    }
}
