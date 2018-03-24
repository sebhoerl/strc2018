package ch.ethz.matsim.strc18;

import java.util.Arrays;
import java.util.List;

import ch.ethz.matsim.run_tools.spsa.projection.SPSAProjection;

public class STRCProjection implements SPSAProjection {
	final private SPSAParameters parameters;

	public STRCProjection(SPSAParameters parameters) {
		this.parameters = parameters;
	}

	@Override
	public List<Double> projectGradientCandidate(double c, List<Double> perturbation, List<Double> candidate) {
		double criticalFlowCapacityFactor = candidate.get(0) - Math.abs(c * perturbation.get(0));
		double flowCapacityFactorOffset = 0.0;

		if (criticalFlowCapacityFactor < parameters.minimumFlowCapacityFactor) {
			flowCapacityFactorOffset = parameters.minimumFlowCapacityFactor - criticalFlowCapacityFactor;
		}

		double criticalCrossingPenalty = candidate.get(1) - Math.abs(c * perturbation.get(1));
		double crossingPenaltyOffset = 0.0;

		if (criticalCrossingPenalty < parameters.minimumCrossingPenalty) {
			crossingPenaltyOffset = parameters.minimumCrossingPenalty - criticalCrossingPenalty;
		}

		return Arrays.asList(candidate.get(0) + flowCapacityFactorOffset, candidate.get(1) + crossingPenaltyOffset);
	}

	@Override
	public List<Double> projectObjectiveCandidate(List<Double> candidate) {
		return Arrays.asList(Math.max(parameters.minimumFlowCapacityFactor, candidate.get(0)),
				Math.max(parameters.minimumCrossingPenalty, candidate.get(1)));
	}
}
