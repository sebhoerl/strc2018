package ch.ethz.matsim.strc18;

import ch.ethz.matsim.run_tools.framework.simulation.SimulationDescription;

public class STRCSimulationDescription implements SimulationDescription {
	public double crossingPenalty;
	public double flowCapacityFactor;
	
	public String chainId = null;
	public String simulationId = null;
	
	public double objective;	
}
