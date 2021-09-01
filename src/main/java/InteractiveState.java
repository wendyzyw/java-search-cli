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
                    request.printSearchFields();
                    return End;
                default:
                    System.out.println("Input option not valid, please refer to the previous message for available options");
                    return this;
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
                    System.out.println("Input option not valid, please refer to the previous message for available options");
                    return this;
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
            boolean isSearchTermValid = request.validateSearchTerm();
            if ( isSearchTermValid ) {
                System.out.println("Enter search value");
                return DisplaySearchResult;
            } else {
                System.out.println("Search term not valid, Press 2 to view a list of searchable fields, or press 1 to restart your search");
                return PromptRequestType;
            }
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
    End {
        @Override
        public InteractiveState nextState( String input, SearchRequest request ) {
            System.out.println("End");
            return this;
        }
    };

    public abstract InteractiveState nextState( String input, SearchRequest request );
}
