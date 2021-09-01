import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SearchSystem {

    private static final String USER_JSON_PATH = "C:\\wendy\\java-search-cli\\src\\main\\resources\\users.json";
    private static final String TICKET_JSON_PATH = "C:\\wendy\\java-search-cli\\src\\main\\resources\\tickets.json";
    private static final Map<Integer, String> DATA_TO_JSON_PATH_MAPPING = new HashMap<>();
    static {
        DATA_TO_JSON_PATH_MAPPING.put( 1, USER_JSON_PATH );
        DATA_TO_JSON_PATH_MAPPING.put( 2, TICKET_JSON_PATH );
    }
    private JsonNode userRootNode;
    private JsonNode ticketRootNode;

    public JsonNode getUserRootNode() {
        return userRootNode;
    }

    public JsonNode getTicketRootNode() {
        return ticketRootNode;
    }

    public void initializeData() {
        this.userRootNode = readDataFromJsonFile( USER_JSON_PATH );
        this.ticketRootNode = readDataFromJsonFile( TICKET_JSON_PATH );
    }

    public JsonNode readDataFromJsonFile( String path ) {
        ObjectMapper mapper = new ObjectMapper();
        byte[] jsonData;
        JsonNode rootNode = null;
        try {
            jsonData = Files.readAllBytes(Paths.get( path ));
            rootNode = mapper.readTree(jsonData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rootNode;
    }

    public JsonNode performSearch( JsonNode rootNode, String searchTerm, String searchValue ) {
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        for ( int i=0; i<rootNode.size(); i++ ) {
            JsonNode valueNode = rootNode.get(i).get( searchTerm );
            if ( valueNode == null ) continue;
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
                case ARRAY:
                    for ( int j=0; j<valueNode.size(); j++ ) {
                        JsonNode elem = valueNode.get(j);
                        if ( elem.asText().contains( searchValue ) ) {
                            arrayNode.add( rootNode.get(i) );
                            break;
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        return arrayNode;
    }

}
