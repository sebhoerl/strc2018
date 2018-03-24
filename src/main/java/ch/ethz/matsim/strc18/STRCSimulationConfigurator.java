package ch.ethz.matsim.strc18;

import java.io.OutputStreamWriter;
import java.util.Arrays;

import org.apache.commons.vfs2.AllFileSelector;
import org.apache.commons.vfs2.FileSystemException;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigReader;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.ConfigWriter;

import ch.ethz.matsim.av.framework.AVConfigGroup;
import ch.ethz.matsim.projects.astra.config.ASTRAConfigGroup;
import ch.ethz.matsim.run_tools.framework.run.RunDescription;
import ch.ethz.matsim.run_tools.framework.simulation.SimulationConfigurator;
import ch.ethz.matsim.run_tools.framework.simulation.SimulationDescription;
import ch.ethz.matsim.run_tools.framework.simulation.SimulationHandle;

public class STRCSimulationConfigurator implements SimulationConfigurator {
	final private STRCConfig strcConfig;

	public STRCSimulationConfigurator(STRCConfig strcConfig) {
		this.strcConfig = strcConfig;
	}

	@Override
	public void configureSimulation(String id, SimulationDescription description, SimulationHandle handle) {
		try {
			ASTRAConfigGroup astraConfig = new ASTRAConfigGroup();
			AVConfigGroup avConfig = new AVConfigGroup();

			Config config = ConfigUtils.createConfig(astraConfig, avConfig);
			new ConfigReader(config)
					.parse(handle.getDirectory().resolveFile("../strc_config.xml").getContent().getInputStream());

			handle.getDirectory().resolveFile("stadtkreis").copyFrom(handle.getDirectory().resolveFile("../stadtkreis"),
					new AllFileSelector());

			config.facilities().setInputFile("../" + config.facilities().getInputFile());
			config.network().setInputFile("../" + config.network().getInputFile());
			config.plans().setInputFile("../" + config.plans().getInputFile());
			config.plans().setInputPersonAttributeFile("../" + config.plans().getInputPersonAttributeFile());
			config.households().setInputFile("../" + config.households().getInputFile());
			config.households()
					.setInputHouseholdAttributesFile("../" + config.households().getInputHouseholdAttributesFile());
			config.transit().setTransitScheduleFile("../" + config.transit().getTransitScheduleFile());
			config.transit().setVehiclesFile("../" + config.transit().getVehiclesFile());
			config.controler().setOutputDirectory("output");

			astraConfig.setScoringParametersPath("../" + astraConfig.getScoringParametersPath());
			avConfig.setConfigPath("../" + avConfig.getConfigPath());

			STRCSimulationDescription simulationDescription = (STRCSimulationDescription) description;

			astraConfig.setCrossingPenality(simulationDescription.crossingPenalty);
			config.qsim().setFlowCapFactor(simulationDescription.flowCapacityFactor);

			config.controler().setWriteEventsInterval(strcConfig.intermediateObjectiveInterval);

			new ConfigWriter(config).writeStream(new OutputStreamWriter(
					handle.getDirectory().resolveFile("config.xml").getContent().getOutputStream()));
		} catch (FileSystemException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void configureRunner(String id, SimulationDescription description, RunDescription runDescription) {
		runDescription.classPath = Arrays.asList("astra-0.0.1-SNAPSHOT/libs/*",
				"astra-0.0.1-SNAPSHOT/astra-0.0.1-SNAPSHOT.jar");
		runDescription.entryPoint = "ch.ethz.matsim.projects.astra.RunASTRAScenario";
		runDescription.vmArguments.add("-Xmx100G");
		runDescription.arguments = Arrays.asList("config.xml", String.valueOf(strcConfig.numberOfGlobalThreads),
				String.valueOf(strcConfig.numberOfQSimThreads));
	}
}
