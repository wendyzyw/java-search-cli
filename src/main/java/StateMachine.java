import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class StateMachine {

    private InteractiveState state = InteractiveState.PromptRequestType;

    private final SearchRequest request = new SearchRequest();

    public void run() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        printWelcomeMessage();
        while (state != InteractiveState.End) {
            String line = in.readLine( );
            state = state.nextState(line, request);
        }
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
