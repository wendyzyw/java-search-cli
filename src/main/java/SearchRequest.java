import com.fasterxml.jackson.databind.JsonNode;

public class SearchRequest {
    private boolean searchUsers = false;
    private String searchTerm;
    private String searchValue;
    private JsonNode searchResult;

    public SearchRequest( SearchSystem searchSystem ) {
    }

    public String constructSearchHeader() {
        String items = searchUsers ? "users" : "tickets";
        return "Searching " + items + " for " + searchTerm + " with a value of " + searchValue;
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
