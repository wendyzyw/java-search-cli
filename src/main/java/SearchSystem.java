import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SearchSystem {
    public static final String USER_JSON_PATH = "C:\\wendy\\java-search-cli\\src\\main\\resources\\users.json";

    public JsonNode performSearch( String searchTerm, String searchValue ) {
        ObjectMapper mapper = new ObjectMapper();
        byte[] jsonData;
        JsonNode rootNode;
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        try {
            jsonData = Files.readAllBytes(Paths.get(USER_JSON_PATH));
            rootNode = mapper.readTree(jsonData);

            for ( int i=0; i<rootNode.size(); i++ ) {
                JsonNode valueNode = rootNode.get(i).get( searchTerm );
                switch (valueNode.getNodeType()) {
                    case STRING:
                        if ( valueNode.asText().contains( searchValue ) ) {
                            arrayNode.add( rootNode.get(i) );
                        }
                        break;
                    case NUMBER:
                    case BOOLEAN:
                        if ( valueNode.asInt() == Integer.parseInt( searchValue ) ) {
                            arrayNode.add( rootNode.get(i) );
                        }
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arrayNode;
    }

}
