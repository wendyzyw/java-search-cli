/**
 * This class is the representation of each state inside the Finite State Machine
 * each state will determines what's the next state upon user input, and updates the request object and provide feedbacks
 * */
public enum InteractiveState {
    PromptRequestType {
        @Override
        public InteractiveState nextState( String input, SearchRequest request ) {
            if ( (input == null) || input.equals("quit") ) {
                return End;
            }
            try {
                int requestType = Integer.parseInt(input);
                switch (requestType) {
                    case 1:
                        System.out.println("Select 1) Users or 2) Tickets");
                        return PromptSearchOption;
                    case 2:
                        request.printSearchFields();
                        System.out.println("Press 1 to start another round of searching, or press 2 to view a list of searchable fields, or type 'quit' to exit");
                        return PromptRequestType;
                    default:
                        System.out.println("Input option not valid, please refer to the previous message for available options");
                        return this;
                }
            } catch ( NumberFormatException e ) {
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
            try {
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
            } catch ( NumberFormatException e ) {
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
            System.out.println("--------------End of Search--------------");
            System.out.println("Press 1 to start another round of searching, or press 2 to view a list of searchable fields, or type 'quit' to exit");
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
