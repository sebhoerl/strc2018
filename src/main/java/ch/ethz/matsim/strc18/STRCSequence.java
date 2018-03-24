package ch.ethz.matsim.strc18;

import ch.ethz.matsim.run_tools.spsa.SPSASequence;

public class STRCSequence implements SPSASequence {
	final private SPSAParameters parameters;

	public STRCSequence(SPSAParameters parameters) {
		this.parameters = parameters;
	}

	@Override
	public double getGradientFactor(int n) {
		return parameters.gradientPrefactor / Math.pow(n, parameters.gradientExponent);
	}

	@Override
	public double getPerturbationFactor(int n) {
		return parameters.perturbationPrefactor / Math.pow(n, parameters.perturbationExponent);
	}
}
