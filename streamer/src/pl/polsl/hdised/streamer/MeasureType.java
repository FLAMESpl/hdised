package pl.polsl.hdised.streamer;

public enum MeasureType {

	TANK('T'),
	REFUEL('R'),
	NOZZLE('N');
	
	private char id;
	
	private MeasureType(char id) {
		
		this.id = id;
	}
	
	public char getId() {
		return id;
	}
}
