package lowbrain.main;

import java.io.File;
import java.util.Calendar;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	private boolean debug = false;
	 
	@Override
    public void onEnable() {
		this.getLogger().info("Loading CircadianRhythm.jar");
        try {
            File file;
            if (!this.getDataFolder().exists()) {
                this.getDataFolder().mkdirs();
            }
            if (!(file = new File(this.getDataFolder(), "config.yml")).exists()) {
                this.getLogger().info("Config.yml not found! Creating new one ...");
                FileConfiguration config = this.getConfig();
                config.addDefault("updateInterval", (Object)300);
                config.addDefault("hoursPerDay", (Object)12);
                config.addDefault("debug", (Object)false);
                config.addDefault("hoursOffset", (Object)0);
                config.options().copyDefaults(true);
                this.saveConfig();
            } else {
                this.getLogger().info("Config.yml found, loading saved data!"); 
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        String updateInterval = this.getConfig().getString("updateInterval");
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
    
    public void syncTime() {
    	int hpd = this.getConfig().getInt("hoursPerDay");
    	double timespeed = (24/hpd);
        Calendar d = Calendar.getInstance();
        int h = d.get(11) + this.getConfig().getInt("hoursOffset");
        h = (int) (h * timespeed);
        if(h > 24)h -= 24;
        int m = d.get(12);
        m = (int) (m * timespeed);
        if(m > 60){
        	m -= 60;
        	h += 1;
        }
        if(this.getConfig().getBoolean("debug")){
        	this.getLogger().info("Time : " + h + "hrs " + m + " min");
        }
        long ticks = 1000 * h + (m *= 16) + 18000;
        ((World)this.getServer().getWorlds().get(0)).setTime(ticks);
    }
}
