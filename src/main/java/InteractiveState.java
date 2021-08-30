public enum InteractiveState {
    PromptRequestType {
        @Override
        public InteractiveState nextState( String input ) {
            if ((input == null) || input.equals("quit")) {
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
        public InteractiveState nextState( String input ) {
            if ((input == null) || input.equals("quit")) {
                return End;
            }
            int searchOption = Integer.parseInt(input);
            switch (searchOption) {
                case 1:
                case 2:
                    System.out.println("Enter search term");
                    return PromptSearchTerm;
                default:
                    return DisplayInvalidInputMessage;
            }
        }
    },
    PromptSearchTerm {
        @Override
        public InteractiveState nextState( String input ) {
            System.out.println("Enter search value");
            return DisplaySearchResult;
        }
    },
    DisplayInvalidInputMessage {
        @Override
        public InteractiveState nextState(String input) {
            System.out.println("Invalid input");
            return PromptRequestType;
        }
    },
    DisplaySearchResult {
        @Override
        public InteractiveState nextState(String input) {
            System.out.println("Searching ... ");
            return End;
        }
    },
    End {
        @Override
        public InteractiveState nextState( String input ) {
            System.out.println("End");
            return this;
        }
    };

    public abstract InteractiveState nextState( String input );
}
