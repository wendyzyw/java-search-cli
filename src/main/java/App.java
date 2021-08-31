import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class App {

    public static void main(String[] args) throws IOException {
        StateMachine stateMachine = new StateMachine();
        stateMachine.run();
        System.exit(0);
    }

}
