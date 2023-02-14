package server.commandserver;

import common_models.poll.PollStatus;
import files.MyPollsFile;
import common_models.poll.PollModel;
import common_models.poll.PollOptionModel;
import server.models.ServerMessageMode;
import server.models.ServerMessageModel;

import java.util.ArrayList;
import java.util.List;

import static utils.ConsoleDetail.*;

public class ServerCommandPoll {

    protected static ServerMessageModel messageCommand(String[] commandTokens) {
        try{
        switch (commandTokens[1].replaceFirst("-", "")) {
            case "create" -> {
                String title = "",uniqueName = null;
                List<PollOptionModel> options = null;
                for (int i = 0; i < commandTokens.length; i++) {
                    String c = commandTokens[i];
                    if (c.charAt(0) == '-') {
                        if (c.equals("-t")) {
                            if (commandTokens[i + 1].charAt(0) == '-')
                                continue;
                            title = "";
                            for (int j = i + 1; j < commandTokens.length; j++) {
                                if (commandTokens[j].charAt(0) == '-')
                                    break;
                                title += commandTokens[j] + " ";
                            }
                        } else if (c.equals("-o")) {
                            options = new ArrayList<>();
                            for (int j = i + 1; j < commandTokens.length; j++) {
                                if (commandTokens[j].charAt(0) == '-')
                                    break;
                                options.add(PollOptionModel.factory(commandTokens[j],options.size()));
                            }
                        }else if(c.equals("-uname")) {
                            uniqueName = commandTokens[i+1];
                        }
                    }
                }
                if (title == null)
                    return new ServerMessageModel(ServerMessageMode.ToAdminister,RED + "Title can't be empty" + RESET);
                if (options == null)
                    return new ServerMessageModel(ServerMessageMode.ToAdminister,RED + "Options can't be empty" + RESET);
                if(uniqueName == null)
                    return new ServerMessageModel(ServerMessageMode.ToAdminister,RED + "Unique name can't be empty" + RESET);
                PollModel poll = new PollModel(title, options,uniqueName);
                MyPollsFile.addPoll(poll);
                return new ServerMessageModel(ServerMessageMode.ToAdminister,poll.show());

            }
            case "show" -> {
                String detail = commandTokens[2];
                PollModel poll = MyPollsFile.getPoll(detail);
                if(poll == null)
                    return new ServerMessageModel(ServerMessageMode.ToAdminister,RED_BRIGHT+"Couldn't find any poll with input information"+RESET);

                return new ServerMessageModel(ServerMessageMode.ToAdminister,poll.show());

            }
            case "show-all" -> {
                ArrayList<PollModel> polls = MyPollsFile.allPolls();
                if(polls.size() ==0 || polls == null)
                    return new ServerMessageModel(ServerMessageMode.ToAdminister,RED_BRIGHT+"There isn't any poll"+RESET);
                String pollsToString = "";
                for (int i = 0; i < polls.size(); i++) {
                    pollsToString += polls.get(i).show();
                }
                return new ServerMessageModel(ServerMessageMode.ToAdminister,pollsToString);

            }
            case "show-all-detail" -> {
                ArrayList<PollModel> polls = MyPollsFile.allPolls();
                if(polls.size() ==0 || polls == null)
                    return new ServerMessageModel(ServerMessageMode.ToAdminister,RED_BRIGHT+"There isn't any poll"+RESET);
                String details = "\n";
                for (int i = 0; i < polls.size(); i++) {
                    details += GREEN_BOLD+"Unique Name : "+WHITE_BRIGHT+ polls.get(i).getUniqueName() + "\t\t"+GREEN_BOLD+"Poll Id : "+WHITE_BRIGHT+polls.get(i).getPollId()+"\n";
                }
                return new ServerMessageModel(ServerMessageMode.ToAdminister,details);
            }
            case "end-poll" -> {
                String detail = commandTokens[2];
                boolean result = MyPollsFile.changePollStatus(PollStatus.End,detail);
                return new ServerMessageModel(ServerMessageMode.ToAdminister,((result)?GREEN_BRIGHT+"Poll : "+detail + " status changed to end "+RESET:RED_BRIGHT+"FAILURE"+RESET));
            }
            default -> {return new ServerMessageModel(ServerMessageMode.ToAdminister,RED + "Invalid Argument !" + RESET);}
        }
        }catch (IndexOutOfBoundsException ex) {
            return new ServerMessageModel(ServerMessageMode.ToAdminister,RED + "Use command correctly !" + RESET);
        }
    }

}