import com.fasterxml.jackson.databind.JsonNode;

public enum InteractiveState {
    PromptRequestType {
        @Override
        public InteractiveState nextState( String input, SearchRequest request ) {
            if ( (input == null) || input.equals("quit") ) {
                return End;
            }
            int requestType = Integer.parseInt(input);
            switch (requestType) {
                case 1:
                    System.out.println("Select 1) Users or 2) Tickets");
                    return PromptSearchOption;
                case 2:
                    System.out.println("Fields");
                    return End;
                default:
                    return DisplayInvalidInputMessage;
            }
        }
    },
    PromptSearchOption {
        @Override
        public InteractiveState nextState( String input, SearchRequest request ) {
            if ( (input == null) || input.equals("quit") ) {
                return End;
            }
            int searchOption = Integer.parseInt(input);
            switch (searchOption) {
                case 1:
                    request.setSearchUsers(true);
                    System.out.println("Enter search term");
                    return PromptSearchTerm;
                case 2:
                    request.setSearchUsers(false);
                    System.out.println("Enter search term");
                    return PromptSearchTerm;
                default:
                    return DisplayInvalidInputMessage;
            }
        }
    },
    PromptSearchTerm {
        @Override
        public InteractiveState nextState( String input, SearchRequest request ) {
            if ( (input == null) || input.equals("quit") ) {
                return End;
            }
            request.setSearchTerm(input);
            System.out.println("Enter search value");
            return DisplaySearchResult;
        }
    },
    DisplaySearchResult {
        @Override
        public InteractiveState nextState(String input, SearchRequest request) {
            if ( (input == null) || input.equals("quit") ) {
                return End;
            }
            request.startSearchProcess(input);
            return End;
        }
    },
    DisplayInvalidInputMessage {
        @Override
        public InteractiveState nextState(String input, SearchRequest request) {
            System.out.println("Invalid input");
            return PromptRequestType;
        }
    },
    End {
        @Override
        public InteractiveState nextState( String input, SearchRequest request ) {
            System.out.println("End");
            return this;
        }
    };

    public abstract InteractiveState nextState( String input, SearchRequest request );
}
