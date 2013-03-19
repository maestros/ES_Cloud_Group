package model;

import java.util.HashMap;
import java.util.Map;

public class Cloud {
	
	private Map<Long,Blade> blades;
	
	public void setBlade(Long id, Blade blade) {
		blades.put(id, blade);
	}

	public Cloud() {
		this.blades = new HashMap<Long,Blade>();
	}
	
	public Map<Long, Blade> getBlades() {
		return blades;
	}
	
}