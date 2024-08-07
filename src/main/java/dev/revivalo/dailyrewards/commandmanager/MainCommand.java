package dev.revivalo.dailyrewards.commandmanager;

import dev.revivalo.dailyrewards.configuration.file.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public abstract class MainCommand implements TabExecutor {
    protected final Set<SubCommand> subCommands = new HashSet<>();

    protected final Lang noPermMessage;
    protected final ArgumentMatcher argumentMatcher;

    public MainCommand(Lang noPermissionMessage, ArgumentMatcher argumentMatcher) {
        this.noPermMessage = noPermissionMessage;
        this.argumentMatcher = argumentMatcher;

        registerSubCommands();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            perform(sender);
            return true;
        }

        SubCommand subCommand = subCommands.stream().filter(sc -> sc.getName().equalsIgnoreCase(args[0])).findAny().orElse(getDefaultSyntax());

        if (subCommand == null)
            return false;

        if (subCommand.getPermission() == null || sender.hasPermission(subCommand.getPermission()))
            subCommand.perform(sender, Arrays.copyOfRange(args, 1, args.length));
        else
            sender.sendMessage(noPermMessage.asColoredString());

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 0)
            return null;

        if (args.length == 1) {
            List<String> subCommandsTC = subCommands.stream().filter(sc -> sc.getPermission() == null || sender.hasPermission(sc.getPermission())).map(SubCommand::getName).collect(Collectors.toList());
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

    public void registerMainCommand(JavaPlugin plugin, String cmdName) {
        PluginCommand cmd = plugin.getCommand(cmdName);

        cmd.setExecutor(this);
        cmd.setTabCompleter(this);
        cmd.setPermissionMessage(noPermMessage.asColoredString());
    }

    protected abstract void registerSubCommands();

    protected abstract void perform(CommandSender sender);

    protected SubCommand getDefaultSyntax() {
        return subCommands.stream().filter(sc -> sc.getName().equalsIgnoreCase("default")).findAny().orElse(null);
    }
}