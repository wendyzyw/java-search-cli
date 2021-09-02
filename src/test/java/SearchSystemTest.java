import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

/**
* This class aims to test the matching mechanism of the search functionality, as well as the
 * association between user and ticket
 * */
public class SearchSystemTest {

    @Test
    public void testIsMatchedMethodOnUserJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree("{\n" +
                "    \"_id\": 10,\n" +
                "    \"name\": \"Kari Vinson\",\n" +
                "    \"created_at\": \"2016-02-08T04:32:38-11:00\",\n" +
                "    \"verified\": false\n" +
                "  }");


        SearchSystem searchSystem = new SearchSystem();
        searchSystem.initializeData();

        Assert.assertTrue( searchSystem.isMatched( node, "_id", "10" ) );
        Assert.assertTrue( searchSystem.isMatched( node, "name", "kari" ) );
        Assert.assertTrue( searchSystem.isMatched( node, "created_at", "2016" ) );
        Assert.assertTrue( searchSystem.isMatched( node, "verified", "false" ) );

        Assert.assertFalse( searchSystem.isMatched( node, "_id", "77" ) );
        Assert.assertFalse( searchSystem.isMatched( node, "name", "test" ) );
        Assert.assertFalse( searchSystem.isMatched( node, "created_at", "2017" ) );
        Assert.assertFalse( searchSystem.isMatched( node, "verified", "invalid" ) );
    }

    @Test
    public void testIsMatchedMethodOnTicketJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree("{\n" +
                "    \"_id\": \"4d22436c-6c26-431b-9083-35ec8e86c57d\",\n" +
                "    \"created_at\": \"2016-04-11T04:56:30-10:00\",\n" +
                "    \"type\": \"incident\",\n" +
                "    \"subject\": \"A Nuisance in Tanzania\",\n" +
                "    \"tags\": [\n" +
                "      \"Alaska\",\n" +
                "      \"Maryland\",\n" +
                "      \"Iowa\",\n" +
                "      \"North Dakota\"\n" +
                "    ]\n" +
                "  }");


        SearchSystem searchSystem = new SearchSystem();
        searchSystem.initializeData();

        Assert.assertTrue( searchSystem.isMatched( node, "_id", "4d22436c-6c26-431b-9083-35ec8e86c57d" ) );
        Assert.assertTrue( searchSystem.isMatched( node, "subject", "nuisance" ) );
        Assert.assertTrue( searchSystem.isMatched( node, "created_at", "2016" ) );
        Assert.assertTrue( searchSystem.isMatched( node, "type", "Incident" ) );
        Assert.assertTrue( searchSystem.isMatched( node, "tags", "iowa" ) );

        Assert.assertFalse( searchSystem.isMatched( node, "_id", "77" ) );
        Assert.assertFalse( searchSystem.isMatched( node, "subject", "nuisancesspellingerror" ) );
        Assert.assertFalse( searchSystem.isMatched( node, "created_at", "2017" ) );
        Assert.assertFalse( searchSystem.isMatched( node, "type", "non_existing_type" ) );
        Assert.assertFalse( searchSystem.isMatched( node, "tags", "non_existing_tag" ) );
        Assert.assertFalse( searchSystem.isMatched( node, "assignee_id", "3" ) );
    }

    @Test
    public void testPerformSearchUponUserData() {
        SearchSystem searchSystem = new SearchSystem();
        searchSystem.initializeData();

        JsonNode result;

        result = searchSystem.performSearch("user", "_id", "70");
        Assert.assertTrue( result.size() > 0 );
        Assert.assertEquals(4, result.get(0).path("tickets").size());

        result = searchSystem.performSearch("user", "_id", "99");
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testPerformSearchUponTicketData() {
        SearchSystem searchSystem = new SearchSystem();
        searchSystem.initializeData();

        JsonNode result;

        result = searchSystem.performSearch("ticket", "type", "incident");
        Assert.assertEquals(35, result.size());

        result = searchSystem.performSearch("ticket", "tags", "maryland");
        Assert.assertEquals(14, result.size());

        result = searchSystem.performSearch("ticket", "subject", "non_existing");
        Assert.assertEquals(0, result.size());

        result = searchSystem.performSearch("ticket", "_id", "eba628f6-5c97-4f4e-b39d-fb78850661df");
        Assert.assertEquals("Nash Rivers", result.get(0).path("assignee_name").get(0).asText());
    }
}
