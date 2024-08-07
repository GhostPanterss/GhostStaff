package me.ghostpanters.ghoststaff;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;

    public class GhostStaff extends JavaPlugin {

        private FileConfiguration config;

        @Override
        public void onEnable() {
            Bukkit.getConsoleSender().sendMessage(" ");
            Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "[GhostStaff] " + ChatColor.WHITE + "Plugin has been " + ChatColor.GREEN + "enable");
            Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "[GhostStaff] " + ChatColor.WHITE + "https://GhostStudio.online");
            Bukkit.getConsoleSender().sendMessage(" ");
            this.saveDefaultConfig();
            config = this.getConfig();
            this.getCommand("sw").setExecutor(new StaffCommandExecutor());
            this.getCommand("ghoststaff").setExecutor(new ReloadCommandExecutor());
        }

        public void onDisable() {
            Bukkit.getConsoleSender().sendMessage(" ");
            Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "[GhostStaff] " + ChatColor.WHITE + "Plugin has been " + ChatColor.RED + "disable");
            Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "[GhostStaff] " + ChatColor.WHITE + "https://GhostStudio.online");
            Bukkit.getConsoleSender().sendMessage(" ");
        }

        public FileConfiguration getCustomConfig() {
            return this.getConfig();
        }

        private String formatMessage(String message) {
            String prefix = ChatColor.translateAlternateColorCodes('&', config.getString("messages.prefix"));
            return ChatColor.translateAlternateColorCodes('&', message.replace("%prefix%", prefix));
        }

        public class StaffCommandExecutor implements CommandExecutor {

            @Override
            public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("Команду может писать только игрок!");
                    return true;
                }

                Player player = (Player) sender;
                if (args.length != 1) {
                    return false;
                }

                String action = args[0].toLowerCase();
                String permission = "";
                String prefix = config.getString("messages.prefix");

                switch (action) {
                    case "on":
                        for (String group : config.getConfigurationSection("group").getKeys(false)) {
                            if (player.hasPermission(config.getString("group." + group + ".premission"))) {
                                permission = group;
                                break;
                            }
                        }

                        if (permission.isEmpty()) {
                            player.sendMessage(formatMessage(String.join("\n", config.getStringList("messages.hasnt_permission"))));
                            return true;
                        }

                        for (String cmd : config.getStringList("group." + permission + ".command_use_on")) {
                            cmd = cmd.replace("%player%", player.getName());
                            getServer().dispatchCommand(getServer().getConsoleSender(), cmd);
                        }

                        player.sendMessage(formatMessage(String.join("\n", config.getStringList("messages.message_on"))));
                        break;

                    case "off":
                        for (String group : config.getConfigurationSection("group").getKeys(false)) {
                            if (player.hasPermission(config.getString("group." + group + ".premission"))) {
                                permission = group;
                                break;
                            }
                        }

                        if (permission.isEmpty()) {
                            player.sendMessage(formatMessage(String.join("\n", config.getStringList("messages.hasnt_permission"))));
                            return true;
                        }

                        for (String cmd : config.getStringList("group." + permission + ".command_use_off")) {
                            cmd = cmd.replace("%player%", player.getName());
                            getServer().dispatchCommand(getServer().getConsoleSender(), cmd);
                        }

                        player.sendMessage(formatMessage(String.join("\n", config.getStringList("messages.message_off"))));
                        break;

                    default:
                        return false;
                }

                return true;
            }
        }

        public class ReloadCommandExecutor implements CommandExecutor {

            @Override
            public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
                if (sender.hasPermission("ghoststaff.reload")) {
                    reloadConfig();
                    sender.sendMessage(formatMessage("Configuration reloaded!"));
                    return true;
                } else {
                    sender.sendMessage(formatMessage(String.join("\n", config.getStringList("messages.hasnt_permission"))));
                    return true;
                }
            }
        }
    }
