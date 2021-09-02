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

/**
 * This class is responsible for dealing with JsonNode operations, conversions from .json files into JsonNode representation,
 * traversing, searching, and updating JsonNode objects
 * =centralization for core search related business logic (etc. how User and Ticket are associated)
 * */
public class SearchSystem {
    private final static int TOTAL_SPACE = 20;
    private static final String USER_JSON_PATH = "\\src\\main\\resources\\users.json";
    private static final String TICKET_JSON_PATH = "\\src\\main\\resources\\tickets.json";

    private JsonNode userRootNode;
    private JsonNode ticketRootNode;

    private static final Map<String, JsonNode> SEARCH_CATEGORY_TO_JSONNODE_MAPPING = new HashMap<>();

    /**
     * perform searching on a rootNode that is a JsonNode representation of the users.json/ticket.json file upon
     * requested searchTerm and searchValue
     *
     * @param searchUpon "user" or "ticket"
     * @param searchTerm field name
     * @param searchValue value
     *
     * @return search result as a JsonNode (ArrayNode)
     */
    public JsonNode performSearch(String searchUpon, String searchTerm, String searchValue ) {
        JsonNode rootNode = SEARCH_CATEGORY_TO_JSONNODE_MAPPING.get( searchUpon );
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        for ( int i=0; i<rootNode.size(); i++ ) {
            boolean matched = isMatched( rootNode.get(i), searchTerm, searchValue );
            if ( matched ) {
                ObjectNode newNode = rootNode.get(i).deepCopy();
                switch (searchUpon) {
                    case SearchRequest.USER:
                        // If searching for user, find the tickets that have assignee_id equals to the user id
                        // search for all tickets that has assignee_id equals to the user ids
                        setAssociateFieldOnJsonNode( newNode, SearchRequest.TICKET, "_id", "assignee_id", "subject", "tickets" );
                        break;
                    case SearchRequest.TICKET:
                        // If searching for ticket, find the corresponding user name that equals to the assignee_id
                        // search for the user that has _id equals to the assigneed_id
                        setAssociateFieldOnJsonNode( newNode, SearchRequest.USER, "assignee_id", "_id", "name", "assignee_name" );
                        break;
                    default:
                        break;
                }
                arrayNode.add( newNode );
            }
        }
        return arrayNode;
    }

    /**
     * retrieve an array of associated values from the associated category and set it as a new field on the node
     *
     * @param node the new ObjectNode that will potentially get updated with a new field
     * @param associateCategory the other category that needs to be associated with
     * @param foreignKey field name that corresponds to the queried category
     * @param associateKey field name that corresponds to the associated category
     * @param targetField the field that needs to be extracted from the matched object of associated category
     * @param newField new field to be added to the node
     *
     */
    private void setAssociateFieldOnJsonNode( ObjectNode node, String associateCategory, String foreignKey, String associateKey, String targetField, String newField ) {
        if ( node.get( foreignKey ) != null ) {
            JsonNode rootNode = SEARCH_CATEGORY_TO_JSONNODE_MAPPING.get( associateCategory );
            JsonNode associatesValue = searchForTargetTerm( rootNode, associateKey, node.get( foreignKey ).toString(), targetField );
            node.set( newField, associatesValue );
        }
    }

    /**
     * extract an ArrayNode of string values that corresponds to the searchValue on the searchTerm
     *
     * @param rootNode userRootNode or ticketRoodNode
     * @param searchTerm field name
     * @param searchValue value
     * @param targetTerm the field that need to be extracted value from
     *
     * @return search result as an ArrayNode of string values (TextNode)
     */
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

    /**
     * check whether or not a JsonNode matches up to the searchValue upon the searchTerm
     *
     * @param node a JsonNode to be examined
     * @param searchTerm field name
     * @param searchValue value
     *
     * @return matched or not
     */
    private boolean isMatched( JsonNode node, String searchTerm, String searchValue ) {
        JsonNode valueNode = node.get( searchTerm );
        boolean matched = false;
        if ( valueNode == null ) return false;
        switch (valueNode.getNodeType()) {
            case STRING:
                if (valueNode.asText().toLowerCase().contains(searchValue.toLowerCase()))  matched = true;
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
            case BOOLEAN:
            case NUMBER:
            default:
                if (valueNode.toString().equalsIgnoreCase( searchValue ) )  matched = true;
                break;
        }
        return matched;
    }

/// --------------------------------------------------------------------------  methods that deal with JsonNode printing
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

/// ------------------------------------------- methods that deal with json file conversion into JsonNode representation
    public void initializeData() {
        String basePath = System.getProperty("user.dir");
        if ( this.userRootNode == null ) {
            this.userRootNode = readDataFromJsonFile( basePath + USER_JSON_PATH );
            SEARCH_CATEGORY_TO_JSONNODE_MAPPING.put( SearchRequest.USER, userRootNode );
        }
        if ( this.ticketRootNode == null ) {
            this.ticketRootNode = readDataFromJsonFile( basePath + TICKET_JSON_PATH );
            SEARCH_CATEGORY_TO_JSONNODE_MAPPING.put( SearchRequest.TICKET, ticketRootNode );
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

/// ---------------------------------------  methods that deal with retrieving distinct set of field names from JsonNode
    public Set<String> retrieveFieldsFormArrayNode( String searchUpon ) {
        Set<String> result = new HashSet<>();
        JsonNode rootNode = SEARCH_CATEGORY_TO_JSONNODE_MAPPING.get( searchUpon );
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

}
