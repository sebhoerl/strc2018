package ch.ethz.matsim.strc18;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.ethz.matsim.run_tools.framework.run.LocalLinuxRunEnvironment;
import ch.ethz.matsim.run_tools.framework.run.RunDescription;
import ch.ethz.matsim.run_tools.framework.simulation.SimulationConfigurator;
import ch.ethz.matsim.run_tools.framework.simulation.SimulationEnvironment;
import ch.ethz.matsim.run_tools.spsa.SPSADescriptionFactory;
import ch.ethz.matsim.run_tools.spsa.SPSAEnvironment;
import ch.ethz.matsim.run_tools.spsa.SPSAObjective;
import ch.ethz.matsim.run_tools.spsa.SPSASequence;
import ch.ethz.matsim.run_tools.spsa.projection.SPSAProjection;
import ch.ethz.matsim.run_tools.spsa.sampler.RademacherSampler;
import ch.ethz.matsim.run_tools.spsa.sampler.SPSASampler;

public class RunSTRC {
	static public void main(String[] args)
			throws JsonParseException, JsonMappingException, IOException, InterruptedException {
		STRCConfig strcConfig = new ObjectMapper().readValue(new File(args[0]), STRCConfig.class);

		// Part 1: Set up the simulation environment
		File rootDirectory = new File(strcConfig.environmentPath);
		LocalLinuxRunEnvironment runEnvironment = new LocalLinuxRunEnvironment(strcConfig.environmentStateFile,
				rootDirectory);

		// Part 2: Set up the simulation templates

		SimulationConfigurator configurator = new STRCSimulationConfigurator(strcConfig);
		SimulationEnvironment simulationEnvironment = new SimulationEnvironment(new RunDescription(), runEnvironment,
				configurator);

		// Part 3: Set up SPSA

		SPSADescriptionFactory descriptionFactory = new STRCDescriptionFactory();
		SPSAObjective objective = new STRCObjective(strcConfig);
		SPSASampler sampler = new RademacherSampler(new Random());
		SPSAProjection projection = new STRCProjection(strcConfig.spsa);
		SPSASequence sequence = new STRCSequence(strcConfig.spsa);

		SPSAEnvironment spsa = new SPSAEnvironment(runEnvironment, simulationEnvironment, objective, sampler,
				projection, descriptionFactory, sequence, strcConfig.spsaName, strcConfig.initialCandidate,
				strcConfig.numberOfIterations, strcConfig.intermediateObjectiveInterval);

		while (true) {
			spsa.update();
			Thread.sleep(1000);
		}
	}
}
