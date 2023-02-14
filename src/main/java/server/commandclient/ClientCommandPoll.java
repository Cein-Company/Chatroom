package server.commandclient;

import client.models.ClientModel;
import common_models.poll.PollModel;
import files.MyPollsFile;
import server.models.ServerMessageMode;
import server.models.ServerMessageModel;

import java.util.ArrayList;

import static utils.ConsoleDetail.*;

public class ClientCommandPoll {
    protected static ServerMessageModel pollCommand(ClientModel sender, String[] commandTokens) {
        try {
            switch (commandTokens[1].replaceFirst("-","").trim())
            {
                case "join"->
                        {
                            String detail = commandTokens[2];
                            MyPollsFile.getPoll(detail);
                            if(commandTokens[3].equals("-v")) {
                                int optionIndex = Integer.parseInt(commandTokens[4]);
                                if(MyPollsFile.votePoll(sender.getClientId(),detail,optionIndex)) {
                                    return new ServerMessageModel(ServerMessageMode.FromSerer,RED+"Voted !!!"+RESET);
                                }else
                                    return new ServerMessageModel(ServerMessageMode.FromSerer,RED+"An error occurred during operation"+RESET);
                            }
                        }
                case "show" -> {
                    String detail = commandTokens[2];
                    PollModel poll = MyPollsFile.getPoll(detail);
                    if(poll == null)
                        return new ServerMessageModel(ServerMessageMode.FromSerer,RED_BRIGHT+"Couldn't find any poll with input information"+RESET);

                    return new ServerMessageModel(ServerMessageMode.FromSerer,poll.show());
                }
                case "show-all" -> {
                    ArrayList<PollModel> polls = MyPollsFile.allPolls();
                    if(polls.size() ==0 || polls == null)
                        return new ServerMessageModel(ServerMessageMode.FromSerer,RED_BRIGHT+"There isn't any poll"+RESET);
                    String pollsToString = "";
                    for (int i = 0; i < polls.size(); i++) {
                        pollsToString += polls.get(i).show();
                    }
                    return new ServerMessageModel(ServerMessageMode.FromSerer,pollsToString);
                }
                case "show-all-detail" -> {
                    ArrayList<PollModel> polls = MyPollsFile.allPolls();
                    if(polls.size() ==0 || polls == null)
                        return new ServerMessageModel(ServerMessageMode.FromSerer,RED_BRIGHT+"There isn't any poll"+RESET);
                    String details = "\n";
                    for (int i = 0; i < polls.size(); i++) {
                        details += GREEN_BOLD+"Unique Name : "+WHITE_BRIGHT+ polls.get(i).getUniqueName() + "\t\t"+GREEN_BOLD+"Poll Id : "+WHITE_BRIGHT+polls.get(i).getPollId()+"\n";
                    }
                    return new ServerMessageModel(ServerMessageMode.FromSerer,details);
                }
            }
        }catch (Exception exception)
        {
            return new ServerMessageModel(ServerMessageMode.FromSerer,RED + "Use command correctly !" + RESET);
        }
        return null;
    }
}
