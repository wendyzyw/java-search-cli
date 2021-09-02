import com.fasterxml.jackson.databind.JsonNode;

import java.util.Set;

/**
* This class is responsible for holding all information required for a full round of search
 * it delegates search operation to the search system it encapsulates
 * kick start and finalized each round of search process by taking in user inputs and providing user feedbacks
 * */
public class SearchRequest {
    private final SearchSystem searchSystem;
    private boolean searchUsers = false;
    private String searchTerm;
    private String searchValue;
    private JsonNode searchResult;
    public static final String USER = "user";
    public static final String TICKET = "ticket";

    /**
     * initialize search system and search data from .json file
     */
    public SearchRequest() {
        this.searchSystem = new SearchSystem();
        this.searchSystem.initializeData();
    }

    public SearchRequest(boolean searchUsers, String searchTerm, String searchValue) {
        this.searchSystem = new SearchSystem();
        this.searchSystem.initializeData();
        this.searchUsers = searchUsers;
        this.searchTerm = searchTerm;
        this.searchValue = searchValue;
    }

    /**
     * clear all search related fields
     */
    public void resetRequest() {
        this.searchUsers = false;
        this.searchTerm = null;
        this.searchValue = null;
        this.searchResult = null;
    }

    /**
     * output: search header -> search system perform search -> output: search result -> clear search request
     *
     * @param input user input
     */
    public void startSearchProcess( String input ) {
        this.setSearchValue(input);
        this.printSearchHeader();
        this.performSearch();
        this.searchSystem.printArrayNode( this.searchResult );
        this.resetRequest();
    }

    /**
     * perform search based on category
     */
    public void performSearch() {
        if (this.searchUsers) {
            this.searchResult = this.searchSystem.performSearch(USER, this.searchTerm, this.searchValue);
        } else {
            this.searchResult = this.searchSystem.performSearch(TICKET, this.searchTerm, this.searchValue);
        }
    }

    /**
     * check if search term entered by user is a legit field name from .json
     */
    public boolean validateSearchTerm( String input ) {
        if ( searchUsers ) {
            Set<String> userFields = this.searchSystem.retrieveFieldsFormArrayNode( USER );
            return userFields.contains( input );
        } else {
            Set<String> ticketFields = this.searchSystem.retrieveFieldsFormArrayNode( TICKET );
            return ticketFields.contains( input );
        }
    }

/// ------------------------------------------------------------------ methods that deal with displaying output messages
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

/// ------------------------------------------------------------------------------------------------------------ setters
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
