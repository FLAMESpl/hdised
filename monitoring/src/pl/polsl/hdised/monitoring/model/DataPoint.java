package pl.polsl.hdised.monitoring.model;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DataPoint {

		private Map<Integer, Tank> tanks = new HashMap<>();
		private Calendar timestamp;
		
		public DataPoint(Calendar timestamp) {
			this.timestamp = timestamp;
		}
		
		public Map<Integer, Tank> getTanks() {
			return tanks;
		}
		
		public Calendar getTimestamp() {
			return timestamp;
		}
}
