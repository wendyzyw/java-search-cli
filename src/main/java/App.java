import java.io.IOException;

public class App {

    public static void main(String[] args) throws IOException {
        StateMachine stateMachine = new StateMachine();
        stateMachine.run();
        System.exit(0);
    }

}
