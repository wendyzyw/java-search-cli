import com.fasterxml.jackson.databind.JsonNode;

import java.util.Set;

public class SearchRequest {
    private final SearchSystem searchSystem;
    private boolean searchUsers = false;
    private String searchTerm;
    private String searchValue;
    private JsonNode searchResult;

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
            this.searchResult = this.searchSystem.performSearch("user", this.searchTerm, this.searchValue);
        } else {
            this.searchResult = this.searchSystem.performSearch("ticket", this.searchTerm, this.searchValue);
        }
    }

    public void printSearchFields() {
        this.searchSystem.initializeData();
        Set<String> userFields = this.searchSystem.retrieveFieldsFormArrayNode( "user" );
        Set<String> ticketFields = this.searchSystem.retrieveFieldsFormArrayNode( "ticket" );
        System.out.println("---------------------------------------------");
        System.out.println("Search users with: ");
        userFields.forEach(System.out::println);
        System.out.println("---------------------------------------------");
        System.out.println("Search tickets with: ");
        ticketFields.forEach(System.out::println);
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

    public boolean validateSearchTerm() {
        if ( searchUsers ) {
            Set<String> userFields = this.searchSystem.retrieveFieldsFormArrayNode( "user" );
            if ( !userFields.contains( searchTerm ) ) {
                return false;
            }
            return true;
        } else {
            Set<String> ticketFields = this.searchSystem.retrieveFieldsFormArrayNode( "ticket" );
            if ( !ticketFields.contains( searchTerm ) ) {
                return false;
            }
            return true;
        }
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

}
