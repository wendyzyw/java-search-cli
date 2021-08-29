import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "zendesk-search")
public class App implements Runnable {

    static CommandLine commandLine;

    public static void main(String[] args) {
        commandLine = new CommandLine(new App());
        commandLine.setExecutionStrategy(new CommandLine.RunLast());
        commandLine.execute(args);
    }

    @Override
    public void run() {
        System.out.println("Welcome to Zendesk Search");
        System.out.println("Type 'quit' to exit at any time, Press 'Enter' to continue");
        System.out.println();
        System.out.println();
        System.out.println("\t Select search options:");
        System.out.println("\t  * Press 1 to search Zendesk");
        System.out.println("\t  * Press 2 to view a list of searchable fields");
        System.out.println("\t  * Type 'quit' to exit");
//        commandLine.usage(System.out);
    }
}
