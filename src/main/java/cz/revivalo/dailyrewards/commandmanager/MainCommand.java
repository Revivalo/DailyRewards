package cz.revivalo.dailyrewards.commandmanager;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.stream.Collectors;

public abstract class MainCommand implements TabExecutor {
    protected final Set<SubCommand> subCommands = new HashSet<>();

    protected final String noPermMessage;
    protected final ArgumentMatcher argumentMatcher;

    public MainCommand(String noPermissionMessage, ArgumentMatcher argumentMatcher) {
        this.noPermMessage = noPermissionMessage;
        this.argumentMatcher = argumentMatcher;

        registerSubCommands();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            SubCommand defaultSyntax = getDefaultSyntax();

            if (defaultSyntax != null && sender.hasPermission(defaultSyntax.getPermission())) {
                Bukkit.getLogger().info(defaultSyntax.getSyntax());
                defaultSyntax.perform(sender, args);
                return true;
            }
            return false;
        }

        SubCommand subCommand = subCommands.stream().filter(sc -> sc.getName().equalsIgnoreCase(args[0])).findAny().orElse(getDefaultSyntax());

        if (subCommand == null)
            return false;

        if (sender.hasPermission(subCommand.getPermission()))
            subCommand.perform(sender, Arrays.copyOfRange(args, 1, args.length));
        else
            sender.sendMessage(noPermMessage);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 0)
            return null;

        if (args.length == 1) {
            List<String> subCommandsTC = subCommands.stream().filter(sc -> sender.hasPermission(sc.getPermission())).map(SubCommand::getName).collect(Collectors.toList());
            return getMatchingStrings(subCommandsTC, args[args.length - 1], argumentMatcher);
        }

        SubCommand subCommand = subCommands.stream().filter(sc -> sc.getName().equalsIgnoreCase(args[0])).findAny().orElse(null);

        if (subCommand == null)
            return null;

        List<String> subCommandTB = subCommand.getTabCompletion(sender, args.length - 2, args);

        return getMatchingStrings(subCommandTB, args[args.length - 1], argumentMatcher);
    }

    private static List<String> getMatchingStrings(List<String> tabCompletions, String arg, ArgumentMatcher argumentMatcher) {
        if (tabCompletions == null || arg == null)
            return null;

        List<String> result = argumentMatcher.filter(tabCompletions, arg);

        Collections.sort(result);

        return result;
    }

    public void registerMainCommand(JavaPlugin main, String cmdName) {
        PluginCommand cmd = main.getCommand(cmdName);

        cmd.setExecutor(this);
        cmd.setTabCompleter(this);
        cmd.setPermissionMessage(noPermMessage);
    }

    protected abstract void registerSubCommands();

    protected SubCommand getDefaultSyntax() {
        return subCommands.stream().filter(sc -> sc.getName().equalsIgnoreCase("default")).findAny().orElse(null);
    }

    public Set<SubCommand> getSubCommands () {
        return new HashSet<>(subCommands);
    }
}