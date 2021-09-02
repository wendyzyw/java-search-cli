import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class SearchSystem {
    private final static int TOTAL_SPACE = 20;
    private static final String USER_JSON_PATH = "C:\\wendy\\java-search-cli\\src\\main\\resources\\users.json";
    private static final String TICKET_JSON_PATH = "C:\\wendy\\java-search-cli\\src\\main\\resources\\tickets.json";

    private JsonNode userRootNode;
    private JsonNode ticketRootNode;

    private static final Map<String, JsonNode> SEARCH_NAME_TO_JSONNODE_MAPPING = new HashMap<>();

    public JsonNode performSearch(String searchUpon, String searchTerm, String searchValue ) {
        JsonNode rootNode = SEARCH_NAME_TO_JSONNODE_MAPPING.get( searchUpon );
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        for ( int i=0; i<rootNode.size(); i++ ) {
            boolean matched = isMatched( rootNode.get(i), searchTerm, searchValue );
            if ( matched ) {
                ObjectNode newNode = rootNode.get(i).deepCopy();

                // If searching for user, find the tickets that have assignee_id equals to the user id
                // search for all tickets that has assignee_id equals to the user ids
                setAssociateFieldOnJsonNode( newNode, SearchRequest.USER, SearchRequest.TICKET, searchUpon,
                        "_id", "assignee_id", "subject", "tickets" );

                // If searching for ticket, find the corresponding user name that equals to the assignee_id
                // search for the user that has _id equals to the assigneed_id
                setAssociateFieldOnJsonNode( newNode, SearchRequest.TICKET, SearchRequest.USER, searchUpon,
                        "assignee_id", "_id", "name", "assignee_name" );

                arrayNode.add( newNode );
            }
        }
        return arrayNode;
    }

    private void setAssociateFieldOnJsonNode( ObjectNode node, String category, String associateCategory, String searchUpon,
                                              String foreignKey, String associateKey, String targetField, String newField ) {
        if ( category.equalsIgnoreCase( searchUpon ) && node.get( foreignKey ) != null ) {
            JsonNode rootNode = SEARCH_NAME_TO_JSONNODE_MAPPING.get( associateCategory );
            JsonNode associatesValue = searchForTargetTerm( rootNode, associateKey, node.get( foreignKey ).toString(), targetField );
            node.set( newField, associatesValue );
        }
    }

    public void printArrayNode( JsonNode arrayNode ) {
        if ( arrayNode.size() == 0 ) {
            System.out.println( "No results found");
            return;
        }
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
            String spacing = String.format( "%1$"+ ( TOTAL_SPACE-element.getKey().length() ) +"s", "" );
            String value = convertValueNodeToPrintable( element.getValue() );
            System.out.print( element.getKey() + spacing + value );
            System.out.println();
        }
    }

    private String convertValueNodeToPrintable( JsonNode valueNode ) {
        switch ( valueNode.getNodeType() ) {
            case STRING:
                return valueNode.asText();
            case ARRAY:
                StringBuilder sb = new StringBuilder();
                sb.append("[");
                for ( int i=0; i<valueNode.size(); i++ ) {
                    if ( i != valueNode.size()-1 ) {
                        sb.append(valueNode.get(i).asText()).append(", ");
                    } else {
                        sb.append(valueNode.get(i).asText());
                    }
                }
                sb.append("]");
                return sb.toString();
            case BOOLEAN:
            case NUMBER:
            default:
                return valueNode.toString();
        }
    }

    public void initializeData() {
        if ( this.userRootNode == null ) {
            this.userRootNode = readDataFromJsonFile( USER_JSON_PATH );
            SEARCH_NAME_TO_JSONNODE_MAPPING.put( SearchRequest.USER, userRootNode );
        }
        if ( this.ticketRootNode == null ) {
            this.ticketRootNode = readDataFromJsonFile(TICKET_JSON_PATH);
            SEARCH_NAME_TO_JSONNODE_MAPPING.put( SearchRequest.TICKET, ticketRootNode );
        }
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

    public Set<String> retrieveFieldsFormArrayNode( String searchUpon ) {
        Set<String> result = new HashSet<>();
        JsonNode rootNode = SEARCH_NAME_TO_JSONNODE_MAPPING.get( searchUpon );
        for ( int i=0; i<rootNode.size(); i++ ) {
            Set<String> fields = retrieveFieldsFormObjectNode( (ObjectNode) rootNode.get(i) );
            result.addAll( fields );
        }
        return result;
    }

    private Set<String> retrieveFieldsFormObjectNode( ObjectNode node ) {
        Set<String> result = new HashSet<>();
        Iterator<Map.Entry<String, JsonNode>> iterator = node.fields();
        while (iterator.hasNext()) {
            Map.Entry<String, JsonNode> element = iterator.next();
            result.add( element.getKey() );
        }
        return result;
    }

    private JsonNode searchForTargetTerm( JsonNode rootNode, String searchTerm, String searchValue, String targetTerm ) {
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        for ( int i=0; i<rootNode.size(); i++ ) {
            boolean matched = isMatched( rootNode.get(i), searchTerm, searchValue );
            if ( matched && rootNode.get(i).get( targetTerm ) != null && rootNode.get(i).get( targetTerm ).getNodeType() == JsonNodeType.STRING ) {
                arrayNode.add( rootNode.get(i).get( targetTerm ).asText() );
            }
        }
        return arrayNode;
    }

    private boolean isMatched( JsonNode node, String searchTerm, String searchValue ) {
        JsonNode valueNode = node.get( searchTerm );
        boolean matched = false;
        if ( valueNode == null ) return false;
        switch (valueNode.getNodeType()) {
            case STRING:
                if (valueNode.asText().toLowerCase().contains(searchValue.toLowerCase()))  matched = true;
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
                    if (elem != null && elem.getNodeType() == JsonNodeType.STRING && elem.asText().toLowerCase().contains(searchValue.toLowerCase())) {
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
