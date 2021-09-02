import com.fasterxml.jackson.databind.JsonNode;

import java.util.Set;

public class SearchRequest {
    private final SearchSystem searchSystem;
    private boolean searchUsers = false;
    private String searchTerm;
    private String searchValue;
    private JsonNode searchResult;
    public static final String USER = "user";
    public static final String TICKET = "ticket";

    public SearchRequest() {
        this.searchSystem = new SearchSystem();
        this.searchSystem.initializeData();
    }

    public void resetRequest() {
        this.searchUsers = false;
        this.searchTerm = null;
        this.searchValue = null;
        this.searchResult = null;
    }

    public void startSearchProcess( String input ) {
        this.setSearchValue(input);
        this.printSearchHeader();
        this.performSearch();
        this.searchSystem.printArrayNode( this.searchResult );
        this.resetRequest();
    }

    public void performSearch() {
        if (this.searchUsers) {
            this.searchResult = this.searchSystem.performSearch(USER, this.searchTerm, this.searchValue);
        } else {
            this.searchResult = this.searchSystem.performSearch(TICKET, this.searchTerm, this.searchValue);
        }
    }

    public void printSearchFields() {
        printSearchFieldsForCategory( USER );
        printSearchFieldsForCategory( TICKET );
    }

    public void printSearchFieldsForCategory( String category ) {
        Set<String> fields = this.searchSystem.retrieveFieldsFormArrayNode( category );
        System.out.println("---------------------------------------------");
        System.out.println("Search " + category + "s with: ");
        fields.forEach(System.out::println);
    }

    public void printSearchHeader() {
        String items = searchUsers ? USER : TICKET + "s";
        System.out.println("Searching " + items + " for " + searchTerm + " with a value of " + searchValue);
    }

    public boolean validateSearchTerm() {
        if ( searchUsers ) {
            Set<String> userFields = this.searchSystem.retrieveFieldsFormArrayNode( USER );
            return userFields.contains(searchTerm);
        } else {
            Set<String> ticketFields = this.searchSystem.retrieveFieldsFormArrayNode( TICKET );
            return ticketFields.contains(searchTerm);
        }
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
