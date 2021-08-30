import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class App {

    public static void main(String[] args) throws IOException {
        InteractiveState state = InteractiveState.PromptRequestType;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        printWelcomeMessage();
        while (state != InteractiveState.End) {
            String line = in.readLine( );
            state = state.nextState(line);
        }
        System.exit(0);
    }

    public static void printWelcomeMessage() {
        System.out.println("Welcome to Zendesk Search");
        System.out.println("Type 'quit' to exit at any time, Press 'Enter' to continue");
        System.out.println();
        System.out.println();
        System.out.println("\t Select search options: ");
        System.out.println("\t * Press 1 to search Zendesk ");
        System.out.println("\t * Press 2 to view a list of searchable fields ");
        System.out.println("\t * Type 'quit' to exit ");
    }

}
