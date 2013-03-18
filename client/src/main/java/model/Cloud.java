package model;

import java.util.Map;

public class Cloud {
	
	private Map<Long,Blade> blades;
	
	public void setBlades(Map<Long, Blade> blades) {
		this.blades = blades;
	}

	public Cloud(Map<Long,Blade> blades) {
		this.blades = blades;
	}
	
	public Map<Long, Blade> getBlades() {
		return blades;
	}
	
}