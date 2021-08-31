import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SearchSystem {
    public static final String USER_JSON_PATH = "C:\\wendy\\java-search-cli\\src\\main\\resources\\users.json";

    public JsonNode performSearch( String searchTerm, String searchValue ) {
        ObjectMapper mapper = new ObjectMapper();
        byte[] jsonData;
        JsonNode rootNode;
        JsonNode result = null;
        try {
            jsonData = Files.readAllBytes(Paths.get(USER_JSON_PATH));
            rootNode = mapper.readTree(jsonData);

            for ( int i=0; i<rootNode.size(); i++ ) {
                JsonNode idNode = rootNode.get(i).path( searchTerm );
                if ( idNode.asInt() == Integer.parseInt( searchValue ) ) {
                    result = rootNode.get(i);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
