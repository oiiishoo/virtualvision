
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class vw extends JavaPlugin implements Listener, CommandExecutor {

    private final Map<UUID, Integer> playerDimensions = new HashMap<>();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("dim").setExecutor(this);
    }

    @Override
    public void onDisable() {
        playerDimensions.clear();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player=(Player)sender;
        if(player==null)return false;
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cТолько игроки могут использовать эту команду.");
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("dim")) {
            if (args.length != 1) {
                player.sendMessage("§eИспользование: /dim <номер>");
                return true;
            }

            try {
                int dimension = Integer.parseInt(args[0]);
                playerDimensions.put(player.getUniqueId(), dimension);
                player.sendMessage("§aТы перешёл в измерение §b#" + dimension);
                updateVisibility();
            } catch (NumberFormatException e) {
                player.sendMessage("§cАргумент должен быть числом.");
            }

            return true;
        }

        return false;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        playerDimensions.putIfAbsent(player.getUniqueId(), 0);
        Bukkit.getScheduler().runTaskLater(this, this::updateVisibility, 5L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        playerDimensions.remove(event.getPlayer().getUniqueId());
        Bukkit.getScheduler().runTaskLater(this, this::updateVisibility, 2L);
    }

    private void updateVisibility() {
        for (Player p1 : Bukkit.getOnlinePlayers()) {
            int dim1 = playerDimensions.getOrDefault(p1.getUniqueId(), 0);

            for (Player p2 : Bukkit.getOnlinePlayers()) {
                if (p1.equals(p2)) continue;

                int dim2 = playerDimensions.getOrDefault(p2.getUniqueId(), 0);

                if (dim1 == dim2) {
                    p1.showPlayer(this, p2);
                } else {
                    p1.hidePlayer(this, p2);
                }
            }
        }
    }
}

