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
        if (index + 1 < this.cpString.length()) {
            peek = this.cpString.charAt(index++);
        } else {
            return new Token(Token.Terminal.END, "");
        }

        if (Character.isAlphabetic(peek)) {
            StringBuffer b = new StringBuffer();
            do { 
                b.append(peek);
                if (index + 1 < this.cpString.length()) {
                    peek = this.cpString.charAt(index++);
                } else {
                    break;
                }
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
