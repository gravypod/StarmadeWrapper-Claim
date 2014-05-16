package com.gravypod.claim;

import java.util.HashMap;

import com.gravypod.starmadewrapper.ConfigurationFile;

public class Votes implements ConfigurationFile<Votes> {
	
	public HashMap<String, String> voteStats;
	
	public void setClaimed(String name, int votes) {
		voteStats.put(name, votes + "");
	}
	
	public boolean hasVoted(String name, int votes) {
		
		if (!voteStats.containsKey(name)) {
			return true;
		}
		
		
		return Integer.parseInt(voteStats.get(name)) != votes;
	}

	@Override
	public void setDefault() {
		voteStats = new HashMap<String, String>();
	}

	@Override
	public void set(Votes t) {
		voteStats  = new HashMap<String, String>(t.voteStats);
	}
	
}
