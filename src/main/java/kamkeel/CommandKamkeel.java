package kamkeel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import noppes.npcs.scripted.CustomNPCsException;

public class CommandKamkeel extends CommandBase{
	
	public Map<String, CommandKamkeelBase> map = new HashMap<String, CommandKamkeelBase>();
	public HelpCommand help = new HelpCommand(this);
	
	public CommandKamkeel(){
		registerCommand(help);
		registerCommand(new ScriptCommand());
		registerCommand(new SlayCommand());
		registerCommand(new QuestCommand());
		registerCommand(new DialogCommand());
		registerCommand(new FactionCommand());
		registerCommand(new NpcCommand());
		registerCommand(new CloneCommand());
		registerCommand(new ConfigCommand());
	}
	
	public void registerCommand(CommandKamkeelBase command){
		String name = command.getCommandName().toLowerCase();
		if(map.containsKey(name))
			throw new CustomNPCsException("Already a subcommand with the name: " + name);
		map.put(name, command);
	}
	
	@Override
	public String getCommandName() {
		return "kamkeel";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "Use as /kamkeel subcommand";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if(args.length == 0){
			help.processCommand(sender, args);
			return;
		}
		
		CommandKamkeelBase command = getCommand(args);
		if(command == null)
			throw new CommandException("Unknown command " + args[0]);
		
		args = Arrays.copyOfRange(args, 1, args.length);
		if(command.subcommands.isEmpty() || !command.runSubCommands()){
			if(!sender.canCommandSenderUseCommand(command.getRequiredPermissionLevel(), getCommandPermission(command.getCommandName())))
				throw new CommandException("You are not allowed to use this command: " + command);
			command.canRun(sender, command.getUsage(), args);
			command.processCommand(sender, args);
			return;
		}
		
		if(args.length == 0){
			help.processCommand(sender, new String[]{command.getCommandName()});
			return;
		}
		
		command.processSubCommand(sender, args[0], Arrays.copyOfRange(args, 1, args.length));
	}

	@Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args){
    	if(args.length == 1)
    		return new ArrayList<String>(map.keySet());
    	CommandKamkeelBase command = getCommand(args);
		if(command == null)
			return null;
    	if(args.length == 2 && command.runSubCommands())
    		return new ArrayList<String>(command.subcommands.keySet());
		return command.addTabCompletionOptions(sender, Arrays.copyOfRange(args, 1, args.length));
    }
    
    public CommandKamkeelBase getCommand(String[] args){
    	if(args.length == 0)
    		return null;
    	return map.get(args[0].toLowerCase());
    }

    @Override
    public int getRequiredPermissionLevel(){
        return 2;
    }

	public static String getCommandPermission(String command){
		return "cnpc.kamkeel." + command.toLowerCase();
	}
}
