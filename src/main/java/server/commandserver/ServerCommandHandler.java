package server.commandserver;

public class ServerCommandHandler {
    public static String commandHandler(String serverMessage) {
        String[] commandTokens = serverMessage.split("\\s+");

        switch (commandTokens[0]) {
            case "/help":                                                                                  //     /help
                // Help
            case "/log":                                                                                    //     /log
                // Log
            case "/members":                                                                            //     /members
                // Members
            case "/kick":                                                                       //     /kick clientName
                // Kick
            case "/ban":                                                                        //     /band clientName
                // Ban
            case "/config":
                // Config
            case "/poll":                          //     /poll -q 'question sentence' -o 'option one, option two, ...'
                // Poll
            default:
                return "Invalid Command";
        }
    }
}
