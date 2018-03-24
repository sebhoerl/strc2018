package ch.ethz.matsim.strc18;

public class SPSAParameters {
	public double perturbationPrefactor = 0.5 * 0.1;
	public double perturbationExponent = 0.602;
	
	public double gradientPrefactor = 0.5 * 0.01;
	public double gradientExponent = 0.101;
	
	public double minimumFlowCapacityFactor = 0.04;
	public double minimumCrossingPenalty = 0.0;
}
