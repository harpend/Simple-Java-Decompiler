package parser.helpers;

public class CPLexer {
    private char peek = ' ';
    private String cpString = "";
    private int index = 0;

    public CPLexer(String s) {
        this.cpString = s;
    }

    public Token nextToken() {
        peek = this.cpString.charAt(index++);
        if (Character.isAlphabetic(peek)) {
            StringBuffer b = new StringBuffer();
            do { 
                b.append(peek);
                peek = this.cpString.charAt(index++);
            } while (Character.isAlphabetic(peek) || peek == '/');

            return new Token(Token.Terminal.CLASS, b.toString());
        } else if (peek == ';') {
            return new Token(Token.Terminal.SC, ";");
        } else if (peek == '<') {
            return new Token(Token.Terminal.LA, "<");
        } else if (peek == '>') {
            return new Token(Token.Terminal.RA, ">");
        }

        return new Token(Token.Terminal.ERR, "");
    }
}
