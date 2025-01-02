package parser.helpers;

public class Token {
    public enum Terminal {
        CLASS, LA, RA, SC
    }

    public Terminal terminal;
    public String value;
    public Token(Terminal t, String v) {
        this.terminal = t;
        this.value = v;
    }
}
