package ch.ethz.matsim.strc18;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class STRCConfig {
	public String environmentStateFile = "strc.json";
	public String environmentPath = "/nas/shoerl/strc18";
	public String spsaName = "strc";

	public int numberOfIterations = 100;
	public int intermediateObjectiveInterval = 5;
	public List<Double> initialCandidate = Arrays.asList(0.1, 2.5);

	public int numberOfGlobalThreads = 12;
	public int numberOfQSimThreads = 12;

	public List<Double> center = Arrays.asList(2683253.0, 1246745.0);
	public double radius = 30000.0 - 1000.0;

	public SPSAParameters spsa = new SPSAParameters();

	public List<Double> bounds = Arrays.asList(120.0, 240.0, 360.0, 480.0, 600.0, 720.0, 840.0, 960.0, 1080.0, 1200.0,
			1320.0, 1440.0, 1560.0, 1680.0, 1800.0);

	public List<Double> reference = Arrays.asList(0.04304099, 0.11960636, 0.15237374, 0.11949822, 0.0984103, 0.09202985,
			0.08035038, 0.06683249, 0.05093544, 0.03979669, 0.03536282, 0.0285498, 0.02573808, 0.02552179, 0.02195307);

	static public void main(String args[]) throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		objectMapper.writeValue(new File(args[0]), new STRCConfig());
	}
}
