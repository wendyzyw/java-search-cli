import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;

public class SearchSystem {
    private final static int TOTAL_SPACE = 20;
    private static final String USER_JSON_PATH = "C:\\wendy\\java-search-cli\\src\\main\\resources\\users.json";
    private static final String TICKET_JSON_PATH = "C:\\wendy\\java-search-cli\\src\\main\\resources\\tickets.json";

    private JsonNode userRootNode;
    private JsonNode ticketRootNode;

    public void printArrayNode( JsonNode arrayNode ) {
        for ( int i=0; i<arrayNode.size(); i++ ) {
            printObjectNode( (ObjectNode) arrayNode.get(i) );
            if ( i < arrayNode.size()-1 ) {
                System.out.println("---------------------------------------------");
            }
        }
    }

    private void printObjectNode( ObjectNode objectNode ) {
        Iterator<Map.Entry<String, JsonNode>> iterator = objectNode.fields();
        while (iterator.hasNext()) {
            Map.Entry<String, JsonNode> element = iterator.next();
            String spacing = String.format("%1$"+ (TOTAL_SPACE-element.getKey().length()) +"s", "");
            String value = element.getValue().getNodeType() == JsonNodeType.STRING ? element.getValue().asText(): element.getValue().toString();
            System.out.print(element.getKey() + spacing + value );
            System.out.println();
        }
    }

    public void initializeData() {
        this.userRootNode = readDataFromJsonFile( USER_JSON_PATH );
        this.ticketRootNode = readDataFromJsonFile( TICKET_JSON_PATH );
    }

    public JsonNode performSearch( String searchUpon, String searchTerm, String searchValue ) {
        JsonNode rootNode = null;
        if ( "user".equalsIgnoreCase( searchUpon ) ) {
            rootNode = this.userRootNode;
        }
        if ( "ticket".equalsIgnoreCase( searchUpon ) ) {
            rootNode = this.ticketRootNode;
        }
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        for ( int i=0; i<rootNode.size(); i++ ) {
            boolean matched = isMatched( rootNode.get(i), searchTerm, searchValue );
            if ( matched ) {
                ObjectNode newNode = rootNode.get(i).deepCopy();
                if ( "user".equalsIgnoreCase( searchUpon ) && newNode.get("_id") != null ) {
                    // If searching for user, find the tickets that have assignee_id equals to the user id
                    // search for all tickets that has assignee_id equals to the user ids
                    JsonNode associatesValue = searchForTargetTerm( ticketRootNode, "assignee_id", newNode.get("_id").toString(), "subject" );
                    newNode.set( "tickets", associatesValue );
                }
                if ( "ticket".equalsIgnoreCase( searchUpon ) && newNode.get("assignee_id") != null ) {
                    // If searching for ticket, find the corresponding user name that equals to the assignee_id
                    // search for the user that has _id equals to the assigneed_id
                    JsonNode associatesValue = searchForTargetTerm( userRootNode, "_id", newNode.get("assignee_id").toString(), "name" );
                    if ( associatesValue.size() > 0 ) {
                        newNode.set( "assignee_name", associatesValue.get( 0 ) );
                    }
                }
                arrayNode.add( newNode );
            }
        }
        return arrayNode;
    }

    private JsonNode readDataFromJsonFile( String path ) {
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

    private JsonNode searchForTargetTerm( JsonNode rootNode, String searchTerm, String searchValue, String targetTerm ) {
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        for ( int i=0; i<rootNode.size(); i++ ) {
            boolean matched = isMatched( rootNode.get(i), searchTerm, searchValue );
            if ( matched ) {
                arrayNode.add( rootNode.get(i).get( targetTerm ).asText() );
            }
        }
        return arrayNode;
    }

    private boolean isMatched( JsonNode node, String searchTerm, String searchValue ) {
        JsonNode valueNode = node.get(searchTerm);
        boolean matched = false;
        if (valueNode == null) return false;
        switch (valueNode.getNodeType()) {
            case STRING:
                if (valueNode.asText().contains(searchValue))  matched = true;
                break;
            case BOOLEAN:
                if (valueNode.asBoolean() == Boolean.parseBoolean(searchValue))  matched = true;
                break;
            case NUMBER:
                if (valueNode.asInt() == Integer.parseInt(searchValue))  matched = true;
                break;
            case ARRAY:
                for (int j = 0; j < valueNode.size(); j++) {
                    JsonNode elem = valueNode.get(j);
                    if (elem.asText().contains(searchValue)) {
                        matched = true;
                        break;
                    }
                }
                break;
            default:
                break;
        }
        return matched;
    }
}
