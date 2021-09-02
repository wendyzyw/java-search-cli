import org.junit.Assert;
import org.junit.jupiter.api.Test;

/**
 * This class aims to test the correct transition between states upon various user inputs
 * */
public class InteractiveStateTest {
    @Test
    public void testStateTransitionFromPromptRequestType() {

        InteractiveState state = InteractiveState.PromptRequestType;
        SearchRequest request = new SearchRequest();

        InteractiveState nextState;

        nextState = state.nextState( "1", request );
        Assert.assertEquals( nextState, InteractiveState.PromptSearchOption );

        nextState = state.nextState( "2", request );
        Assert.assertEquals( nextState, InteractiveState.PromptRequestType );

        nextState = state.nextState( "quit", request );
        Assert.assertEquals( nextState, InteractiveState.End );

        nextState = state.nextState( "3", request );
        Assert.assertEquals( nextState, InteractiveState.PromptRequestType );
    }

    @Test
    public void testStateTransitionFromPromptSearchOption() {

        InteractiveState state = InteractiveState.PromptSearchOption;
        SearchRequest request = new SearchRequest();

        InteractiveState nextState;

        nextState = state.nextState( "1", request );
        Assert.assertEquals( nextState, InteractiveState.PromptSearchTerm );

        nextState = state.nextState( "2", request );
        Assert.assertEquals( nextState, InteractiveState.PromptSearchTerm );

        nextState = state.nextState( "3", request );
        Assert.assertEquals( nextState, InteractiveState.PromptSearchOption );

        nextState = state.nextState( "quit", request );
        Assert.assertEquals( nextState, InteractiveState.End );

        nextState = state.nextState( "non_existing_option", request );
        Assert.assertEquals( nextState, InteractiveState.PromptSearchOption );
    }

    @Test
    public void testStateTransitionFromPromptSearchTerm() {
        InteractiveState state = InteractiveState.PromptSearchTerm;
        SearchRequest request = new SearchRequest();

        InteractiveState nextState;

        nextState = state.nextState( "_id", request );
        Assert.assertEquals( nextState, InteractiveState.DisplaySearchResult );

        nextState = state.nextState( "non_existing_term", request );
        Assert.assertEquals( nextState, InteractiveState.PromptSearchTerm );

        nextState = state.nextState( "quit", request );
        Assert.assertEquals( nextState, InteractiveState.End );
    }

    @Test
    public void testStateTransitionFromDisplaySearchResult() {
        InteractiveState state = InteractiveState.DisplaySearchResult;
        InteractiveState nextState;

        SearchRequest request = new SearchRequest(true, "_id", "" );

        nextState = state.nextState( "wrong_value_format", request );
        Assert.assertEquals( nextState, InteractiveState.PromptRequestType );

        nextState = state.nextState( "34", request );
        Assert.assertEquals( nextState, InteractiveState.PromptRequestType );

        nextState = state.nextState( "quit", request );
        Assert.assertEquals( nextState, InteractiveState.End );
    }
}
