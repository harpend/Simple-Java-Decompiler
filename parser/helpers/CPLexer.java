package parser.helpers;

public class CPLexer {
    private char peek = ' ';
    private String cpString = "";
    private int index = 0;

    public CPLexer(String s) {
        this.cpString = s;
    }

    public Token nextToken() {
        if (index < this.cpString.length()) {
            peek = this.cpString.charAt(index);
        } else {
            return new Token(Token.Terminal.END, "");
        }

        if ((peek >= 'a' && peek <= 'z') || (peek >= 'A' && peek <= 'Z')) {
            StringBuffer b = new StringBuffer();
            do { 
                b.append(peek);
                if (index + 1 < this.cpString.length()) {
                    peek = this.cpString.charAt(++index);
                } else {
                    break;
                }
            } while ((peek >= 'a' && peek <= 'z') || (peek >= 'A' && peek <= 'Z') || peek == '/');

            return new Token(Token.Terminal.CLASS, b.toString());
        } else if (peek == ';') {
            index++;
            return new Token(Token.Terminal.SC, ";");
        } else if (peek == '<') {
            index++;
            return new Token(Token.Terminal.LA, "<");
        } else if (peek == '>') {
            index++;
            return new Token(Token.Terminal.RA, ">");
        }
        
        System.out.print( "ERR Token - CPLexer");
        System.exit(1);
        return new Token(Token.Terminal.ERR, "");
    }
}
