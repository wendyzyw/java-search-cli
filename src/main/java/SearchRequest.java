import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import java.util.Iterator;
import java.util.Map;

public class SearchRequest {
    private final static int TOTAL_SPACE = 20;
    private final SearchSystem searchSystem;
    private boolean searchUsers = false;
    private String searchTerm;
    private String searchValue;
    private JsonNode searchResult;

    public SearchRequest() {
        this.searchSystem = new SearchSystem();
    }

    public void startSearchProcess( String input ) {
        this.setSearchValue(input);
        this.printSearchHeader();
        this.performSearch();
        this.printSearchResult();
    }

    public void performSearch() {
        this.searchResult = this.searchSystem.performSearch(this.searchTerm, this.searchValue);
    }

    public void printSearchResult() {
        Iterator<Map.Entry<String, JsonNode>> iterator = searchResult.fields();
        while (iterator.hasNext()) {
            Map.Entry<String, JsonNode> element = iterator.next();
            String spacing = String.format("%1$"+ (TOTAL_SPACE-element.getKey().length()) +"s", "");
            String value = element.getValue().getNodeType() == JsonNodeType.STRING ? element.getValue().textValue() : element.getValue().toString();
            System.out.print(element.getKey() + spacing + value );
            System.out.println();
        }
    }

    public void printSearchHeader() {
        String items = searchUsers ? "users" : "tickets";
        System.out.println("Searching " + items + " for " + searchTerm + " with a value of " + searchValue);
    }

    public void setSearchUsers(boolean searchUsers) {
        this.searchUsers = searchUsers;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

}
