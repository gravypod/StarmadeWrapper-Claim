package com.gravypod.claim;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.gravypod.starmadewrapper.Material;
import com.gravypod.starmadewrapper.User;
import com.gravypod.starmadewrapper.plugins.commands.Command;

public class ClaimCommand extends Command {
	
	private final Executor pool = Executors.newCachedThreadPool();
	
	private final Votes voteList;
	
	private final ReentrantLock lock = new ReentrantLock();
	
	private final String url;
	boolean allowMoney, allowBlocks;
	int blockMult, moneyMult;
	
	enum Reward {
		MONEY, ITEMS
	}
	
	public ClaimCommand(boolean allowMoney, boolean allowBlocks, int blockMult, int moneyMult, String apiKey, Votes votes) {
	
		this.allowBlocks = allowBlocks;
		this.allowMoney = allowBlocks;
		this.moneyMult = moneyMult;
		this.blockMult = blockMult;
		this.voteList = votes;
		url = "http://starmade-servers.com/api/?object=servers&element=voters&key=" + apiKey + "&month=current&format=json";
	}
	
	public ClaimCommand(Config config, Votes votes) {
	
		this(config.moneyRewardMultiplier != -1, config.blockRewardMultiplier != -1, config.blockRewardMultiplier, config.moneyRewardMultiplier, config.apiKey, votes);
	}
	
	@Override
	public void run(final String username, final User user, final String... args) {
	
		final Reward reward;
		
		if (allowMoney && args.length == 1 && args[0].equalsIgnoreCase("money")) { // Choose the reward
				reward = Reward.MONEY;
		} else {
			if (allowBlocks) {
				reward = Reward.ITEMS;
			} else {
				reward = Reward.MONEY;
			}
		}
		
		
		pool.execute(new Runnable() {
			
			@Override
			public void run() {
			
				int votes = -1;
				try {
					JSONArray data = (JSONArray) ((JSONObject) JSONValue.parse(new InputStreamReader(new URL(url).openStream()))).get("voters");
					for (Object o : data) {
						JSONObject object = (JSONObject) o;
						
						if (((String) object.get("nickname")).equalsIgnoreCase(username)) {
							votes = Integer.parseInt((String) object.get("votes"));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				if (votes == -1) {
					return;
				}
				
				lock.lock();
				if (!voteList.hasVoted(username, votes)) {
					pm(username, "Please vote to get rewards");
					return;
				}
				voteList.setClaimed(username, votes);
				lock.unlock();
				
				switch (reward) {
				
					case ITEMS:
						Material item = getRandomItemId();
						int ammount = blockMult * votes;
						give(user, item.getId(), ammount);
						pm(username, "You have been given " + ammount + " of " + item.name());
						break;
					case MONEY:
						int credits = moneyMult * votes;
						giveCredits(user, credits);
						pm(username, "You have been given " + credits + " credits.");
						break;
				
				}
			}
		});
		
	}
	
	public Material getRandomItemId() {
		
		Material[] mats = Material.values();
		int id = (int)(mats.length * Math.random());
		return mats[id];
	}
	
	@Override
	public String getHelp() {
		return "!claim (money [optional]): Claim a reward for voting on starmade-server. Rewards with blocks by default.";
	}
	
}
