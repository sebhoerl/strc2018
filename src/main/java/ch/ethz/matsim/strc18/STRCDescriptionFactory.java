package ch.ethz.matsim.strc18;

import java.util.List;

import ch.ethz.matsim.run_tools.framework.simulation.SimulationDescription;
import ch.ethz.matsim.run_tools.spsa.SPSADescriptionFactory;

public class STRCDescriptionFactory implements SPSADescriptionFactory {
	@Override
	public SimulationDescription create(List<Double> candidate) {
		STRCSimulationDescription description = new STRCSimulationDescription();
		description.flowCapacityFactor = candidate.get(0);
		description.crossingPenalty = candidate.get(1);
		return description;
	}
}