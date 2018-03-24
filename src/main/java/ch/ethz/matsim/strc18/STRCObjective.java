package ch.ethz.matsim.strc18;

import java.io.IOException;
import java.util.Collection;
import java.util.zip.GZIPInputStream;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigReader;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.router.MainModeIdentifier;
import org.matsim.core.router.MainModeIdentifierImpl;
import org.matsim.core.router.StageActivityTypes;
import org.matsim.core.router.StageActivityTypesImpl;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.pt.PtConstants;

import ch.ethz.matsim.baseline_scenario.analysis.trips.TripItem;
import ch.ethz.matsim.baseline_scenario.analysis.trips.listeners.TripListener;
import ch.ethz.matsim.baseline_scenario.analysis.trips.utils.BaselineHomeActivityTypes;
import ch.ethz.matsim.baseline_scenario.analysis.trips.utils.HomeActivityTypes;
import ch.ethz.matsim.projects.astra.analysis.trips.ASTRATripListener;
import ch.ethz.matsim.run_tools.analysis.BinnedSampleCollector;
import ch.ethz.matsim.run_tools.analysis.distribution_distances.EarthMoversDistance;
import ch.ethz.matsim.run_tools.framework.simulation.SimulationHandle;
import ch.ethz.matsim.run_tools.spsa.SPSAObjective;

public class STRCObjective implements SPSAObjective {
	final private STRCConfig strcConfig;
	final private Coord center;

	public STRCObjective(STRCConfig strcConfig) {
		this.strcConfig = strcConfig;
		this.center = new Coord(strcConfig.center.get(0), strcConfig.center.get(1));
	}

	@Override
	public double getObjective(SimulationHandle handle) {
		return getIntermediateObjective(handle, strcConfig.numberOfIterations);
	}

	@Override
	public double getIntermediateObjective(SimulationHandle handle, int iteration) {
		try {
			Config config = ConfigUtils.createConfig();
			new ConfigReader(config)
					.parse(handle.getDirectory().resolveFile("config.xml").getContent().getInputStream());

			Network network = NetworkUtils.createNetwork();
			new MatsimNetworkReader(network).parse(new GZIPInputStream(
					handle.getDirectory().resolveFile(config.network().getInputFile()).getContent().getInputStream()));

			String eventsPath = "output/ITERS/it." + iteration + "/" + iteration + ".events.xml.gz";

			StageActivityTypes stageActivityTypes = new StageActivityTypesImpl(PtConstants.TRANSIT_ACTIVITY_TYPE);
			HomeActivityTypes homeActivityTypes = new BaselineHomeActivityTypes();
			MainModeIdentifier mainModeIdentifier = new MainModeIdentifierImpl();

			TripListener tripListener = new ASTRATripListener(network, stageActivityTypes, homeActivityTypes,
					mainModeIdentifier);

			EventsManager eventsManager = EventsUtils.createEventsManager();
			eventsManager.addHandler(tripListener);
			new MatsimEventsReader(eventsManager).readStream(
					new GZIPInputStream(handle.getDirectory().resolveFile(eventsPath).getContent().getInputStream()));
			Collection<TripItem> trips = tripListener.getTripItems();

			BinnedSampleCollector collector = new BinnedSampleCollector(strcConfig.bounds);

			for (TripItem item : trips) {
				if (item.mode.equals("car")) {
					if (CoordUtils.calcEuclideanDistance(center, item.origin) < strcConfig.radius
							&& CoordUtils.calcEuclideanDistance(center, item.destination) < strcConfig.radius)
						collector.addSample(item.travelTime);
				}
			}

			return new EarthMoversDistance().compute(strcConfig.reference, collector.buildRelativeFrequencies());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}