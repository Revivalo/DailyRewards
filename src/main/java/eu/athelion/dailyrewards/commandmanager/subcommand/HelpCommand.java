package eu.athelion.dailyrewards.commandmanager.subcommand;

import eu.athelion.dailyrewards.commandmanager.MainCommand;
import eu.athelion.dailyrewards.commandmanager.SubCommand;
import eu.athelion.dailyrewards.configuration.file.Config;
import eu.athelion.dailyrewards.configuration.file.Lang;
import eu.athelion.dailyrewards.util.PermissionUtil;
import eu.athelion.dailyrewards.util.TextUtil;
import eu.athelion.dailyrewards.util.VersionUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;

import java.util.List;

public class HelpCommand implements SubCommand {
    private final List<SubCommand> subCommands;

    public HelpCommand(MainCommand mainCommand) {
        this.subCommands = mainCommand.getSubCommands();
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public Lang getDescription() {
        return Lang.HELP_COMMAND_DESCRIPTION;
    }

    @Override
    public String getSyntax() {
        return "/reward help";
    }

    @Override
    public String getPermission() {
        return PermissionUtil.Permission.HELP.get();
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, int index, String[] args) {
        return null;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        sender.sendMessage(TextUtil.colorize(Config.HELP_HEADER.asString()));

        for (SubCommand subCommand : subCommands) {
            if (subCommand.getPermission() != null && !sender.hasPermission(subCommand.getPermission())) {
                continue;
            }

            BaseComponent[] msg = TextComponent.fromLegacyText(
                    TextUtil.colorize(Config.HELP_MESSAGE_FORMAT.asString()
                            .replace("%syntax%", subCommand.getSyntax())
                            .replace("%description%", subCommand.getDescription().asColoredString())
                    )
            );

            for (BaseComponent bc : msg) {
                if (!VersionUtil.isLegacyVersion())
                    bc.setHoverEvent(
                            new HoverEvent(
                                    HoverEvent.Action.SHOW_TEXT,
                                    TextComponent.fromLegacyText(
                                            Lang.CLICK_TO_PERFORM.asColoredString()
                                                    .replace("%command%", subCommand.getName())
                                    )
                            )
                    );

                bc.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, subCommand.getSyntax()));
            }

            sender.spigot().sendMessage(msg);
        }

        sender.sendMessage(TextUtil.colorize(Config.HELP_FOOTER.asString()));
    }
}