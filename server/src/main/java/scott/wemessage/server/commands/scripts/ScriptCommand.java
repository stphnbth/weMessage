/*
 *  weMessage - iMessage for Android
 *  Copyright (C) 2018 Roman Scott
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package scott.wemessage.server.commands.scripts;

import java.util.ArrayList;
import java.util.List;

import scott.wemessage.commons.types.ReturnType;
import scott.wemessage.commons.utils.StringUtils;
import scott.wemessage.server.ServerLogger;
import scott.wemessage.server.commands.Command;
import scott.wemessage.server.commands.CommandManager;
import scott.wemessage.server.database.MessagesDatabase;
import scott.wemessage.server.messages.Message;
import scott.wemessage.server.messages.chat.GroupChat;
import scott.wemessage.server.scripts.AppleScriptExecutor;

public abstract class ScriptCommand extends Command {

    public ScriptCommand(CommandManager commandManager, String name, String description, String[] args){
        super(commandManager, name, description, args);
    }

    public AppleScriptExecutor getScriptExecutor(){
        return getCommandManager().getMessageServer().getScriptExecutor();
    }

    public MessagesDatabase getMessagesDatabase(){
        return getCommandManager().getMessageServer().getMessagesDatabase();
    }

    String processResult(Object result){
        String stringResult;
        if (result instanceof List) {
            List<String> returnTypeList = new ArrayList<>();

            for (ReturnType returnType : (List<ReturnType>) result) {
                returnTypeList.add(returnType.getReturnName());
            }

            stringResult = "{ " + StringUtils.join(returnTypeList, ", ", 2) + " }";
        } else if (result instanceof ReturnType) {
            stringResult = ((ReturnType) result).getReturnName();
        } else {
            throw new ClassCastException("The result returned from running the script is not a valid return type");
        }
        return stringResult;
    }

    void printChatOptions(List<GroupChat> groupChats){
        int i = 1;

        try {
            ServerLogger.log("Chat Option List");
            ServerLogger.emptyLine();

            for (GroupChat chat : groupChats) {
                Message lastMessage = getMessagesDatabase().getLastNotNullMessageFromChat(chat);
                String lastMessageText;

                if (lastMessage == null){
                    lastMessageText = "Null Message";
                }else {
                    lastMessageText = lastMessage.getText();
                }

                ServerLogger.log(i + ". " + chat.getDisplayName() + " - Last Message: " + lastMessageText);
                i++;
            }
        }catch (Exception ex){
            ServerLogger.error("An error occurred while fetching chat data from the database", ex);
        }
    }
}