package me.zort.containr;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Timer;
import java.util.concurrent.TimeUnit;

public abstract class AnimatedGUI extends GUI {

    @Setter
    @Getter
    private long period;

    private Timer timer;
    @Nullable
    private Player currentPlayer;

    public AnimatedGUI(String title, int rows, int period, TimeUnit unit) {
        super(title, rows);
        this.period = unit.toMillis(period);
        this.currentPlayer = null;
    }

    @Override
    public void open(@NotNull Player p, boolean update) {
        super.open(p, update);
        if(timer != null) {
            timer.cancel();
            timer.purge();
        }
        this.currentPlayer = p;
        new BukkitRunnable() {
            @Override
            public void run() {
                GUI gui = GUIRepository.OPENED_GUIS.getOrDefault(currentPlayer.getName(), null);
                if(gui == null || !gui.equals(AnimatedGUI.this) || !currentPlayer.isOnline() || !GUIRepository.OPENED_GUIS.containsKey(p.getName())) {
                    cancel();
                    return;
                }
                tickAnimatedElements(currentPlayer);
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugins()[0], 0L, period / (1000 / 20));
    }

    public void onPreTick(Player player) {}

    public void tickAnimatedElements(Player player) {
        onPreTick(player);
        update(player, AnimatedSuppliedElement.class, AnimatedElement.class);
        //update(player, AnimatedElement.class);
    }

}
