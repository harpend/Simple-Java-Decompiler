package parser.helpers;

public class CPParser {
    private StringBuffer type;
    private CPLexer lexer;
    private Token lookahead;
    
    public CPParser(String s) {
        this.lexer = new CPLexer(s);
        this.type = new StringBuffer();
    }

    public void parse() {
        this.lookahead = lexer.nextToken();
        expr();
    }

    private void expr() {
        match(Token.Terminal.CLASS); 
        if (lookahead.terminal == Token.Terminal.SC) {
            match(Token.Terminal.SC);
        } else if (lookahead.terminal == Token.Terminal.LA) {
            match(Token.Terminal.LA); term(); match(Token.Terminal.RA); match(Token.Terminal.SC);
        } else {
            System.out.println(lookahead.terminal);
        }
    }

    private void term() {
        match(Token.Terminal.CLASS); 
        if (lookahead.terminal == Token.Terminal.LA) {
            match(Token.Terminal.LA); term(); match(Token.Terminal.RA);
        }

        match(Token.Terminal.SC);
        if (lookahead.terminal == Token.Terminal.CLASS) {
            term();
        }
    }

    private void match(Token.Terminal t) {
        if (t == lookahead.terminal) {
            if (t == Token.Terminal.CLASS) {
                this.type.append(extractName(lookahead.value));
                lookahead = lexer.nextToken();
            } else if (t == Token.Terminal.SC) {
                lookahead = lexer.nextToken();
                if (lookahead.terminal == Token.Terminal.CLASS) {
                    this.type.append(", ");
                }
            } else {
                this.type.append(lookahead.value);
                lookahead = lexer.nextToken();
            }
        } else {
            System.out.println("Failed match");
            System.exit(1);
        }
    }

    private String extractName(String s) {
        int lastSlashIndex = s.lastIndexOf('/');
        if (lastSlashIndex != -1) {
            return s.substring(lastSlashIndex + 1, s.length());
        }

        return s;
    }

    public String getType() {
        return this.type.toString();
    }
}
