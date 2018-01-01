package lowbrain.circadianrhythm;

import java.util.Calendar;
import lowbrain.library.config.YamlConfig;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin class
 * @author lowbrain
 *
 */
public class Main extends JavaPlugin {

    private static YamlConfig config;
	 
	/**
	 * called when the plugin is initially enabled
	 */
	@Override
    public void onEnable() {
		this.getLogger().info("Loading CircadianRhythm.jar");

		config = new YamlConfig("config.yml", this, true);

        String updateInterval = config.getString("updateInterval");
        Bukkit.getServer().getScheduler().runTaskTimer((Plugin)this, new Runnable(){
            @Override
            public void run() {
                Main.this.syncTime();
            }
        }, 0, Long.parseLong(updateInterval) * 20);
    }
   
    @Override
    public void onDisable() {
       
    }
    
    /**
	 * Called when the plugin receice a command
	 */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	if (cmd.getName().equalsIgnoreCase("circadianrhythm.reload") || cmd.getName().equalsIgnoreCase("cr.reload")) { 
			Bukkit.getServer().getScheduler().cancelTasks(this);
			
			config.reload();
			
			String updateInterval = config.getString("updateInterval");
	        Bukkit.getServer().getScheduler().runTaskTimer((Plugin)this, new Runnable(){

	            @Override
	            public void run() {
	                Main.this.syncTime();
	            }
	        }, 0, Long.parseLong(updateInterval) * 20);
			
			sender.sendMessage("CircadianRhythm reloaded !!");
			return true;
    	} 
    	return false;
    }
    
	/**
	 * sync the time with the current config
	 */
    public void syncTime() {
    	int hpd = config.getInt("hoursPerDay");
    	double timespeed = (24/hpd);
        Calendar d = Calendar.getInstance();
        int h = d.get(11) + config.getInt("hoursOffset");
        h = (int) (h * timespeed);
        int nbDays = (int) Math.floor(h / 24);
        h -= nbDays * 24;
        
        int m = d.get(12);
        m = (int) (m * timespeed);
        
        int nbHours = (int) Math.floor(m / 60);
        m -= nbHours * 60;
        h += nbHours;
        
        if(config.getBoolean("debug"))
        	this.getLogger().info("Time : " + h + "hrs " + m + " min");


        long ticks = 1000 * h + (m *= 16) + 18000;
        ((World)this.getServer().getWorlds().get(0)).setTime(ticks);
    }
}
