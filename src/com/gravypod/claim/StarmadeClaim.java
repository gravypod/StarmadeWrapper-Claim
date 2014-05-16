package com.gravypod.claim;

import java.io.File;
import java.io.IOException;

import com.gravypod.starmadewrapper.Configuration;
import com.gravypod.starmadewrapper.Server;
import com.gravypod.starmadewrapper.plugins.Plugin;

public class StarmadeClaim extends Plugin {
	private Votes votes;
	private Config config;
	@Override
	public void onEnable(final Server server) {
		
		try {
			votes = new Votes();
			Configuration.load(getVotesFile(), votes);
			config = new Config();
			if (!Configuration.load(getConfigFile(), config)) {
				Configuration.save(getConfigFile(), config);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		getAssistant().getPluginManager().getCommandManager().registerCommand("claim", new ClaimCommand(config, votes));
		
		
		
	}
	
	@Override
	public void onDisable(final Server server) {
		try {
			Configuration.save(getVotesFile(), votes);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public File getVotesFile() {
		return new File(getDataDir(), "claimed.yml");
	}
	public File getConfigFile() {
		return new File(getDataDir(), "config.yml");
	}
}
