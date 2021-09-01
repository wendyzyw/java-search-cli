import com.fasterxml.jackson.databind.JsonNode;

public class SearchRequest {
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
        this.searchSystem.printArrayNode( this.searchResult );
    }

    public void performSearch() {
        this.searchSystem.initializeData();
        if ( this.searchUsers ) {
            this.searchResult = this.searchSystem.performSearch( "user", this.searchTerm, this.searchValue );
        } else {
            this.searchResult = this.searchSystem.performSearch( "ticket", this.searchTerm, this.searchValue );
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
