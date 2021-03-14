package lexer;

public class LexerTest {
    public static void main(String[] args) throws Exception{
        Lexer lexer = new Lexer("src/lexer/lexerTestInput2");

        while(true) {
            Token t = lexer.nextToken();
            System.out.println(t);
            if(t == null) {
                break;
            }
        }

    }
}
