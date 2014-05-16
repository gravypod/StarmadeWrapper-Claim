package com.gravypod.claim;

import com.gravypod.starmadewrapper.ConfigurationFile;


public class Config implements ConfigurationFile<Config> {
	
	public String apiKey;
	public int blockRewardMultiplier;
	public int moneyRewardMultiplier;
	
	@Override
	public void setDefault() {
		apiKey = "APIKEY";
		blockRewardMultiplier = 100;
		moneyRewardMultiplier = 1000;
	}

	@Override
	public void set(Config t) {
		this.apiKey = t.apiKey;
		this.blockRewardMultiplier = t.blockRewardMultiplier;
		this.moneyRewardMultiplier = t.moneyRewardMultiplier;
	}
	
}
