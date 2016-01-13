package hexenjagd;



import repast.simphony.context.Context;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;

public class Builder implements ContextBuilder<PotentialWitch> {

	public Context<PotentialWitch> build(Context<PotentialWitch> context) {
		
		
		NetworkBuilder<PotentialWitch> netBuilder = new NetworkBuilder<PotentialWitch>("hexenjagd", context, false);
		Network<PotentialWitch> network = netBuilder.buildNetwork();

		Parameters p = RunEnvironment.getInstance().getParameters();
		//getting all parameters by .getValue("param_name")
		int howMany = (Integer)p.getValue("people");
		double fearOfWitchesMin = (Double)p.getValue("fearWMin");
		double fearOfWitchesMax = (Double)p.getValue("fearWMax");
		double fearOfAccusationMin = (Double)p.getValue("fearAMin");
		double fearOfAccusationMax = (Double)p.getValue("fearAMax");
		double suggestibilityMin = (Double)p.getValue("suggMin");
		double suggestibilityMax = (Double)p.getValue("suggMax");
		double accBound = (Double) p.getValue("accBound");
		int accBound2 = (Integer) p.getValue("accBound2");
		double sentBound =  (Double) p.getValue("sentBound");
		double fearFactorMin = (Double) p.getValue("fearFMin");
		double fearFactorMax = (Double) p.getValue("fearFMax");
		int maxEdges= (Integer) p.getValue("maxE");
		double maxWeight = (Double) p.getValue("maxW");
		int endAt = (Integer) p.getValue("endAt");		//Fuer Batch Runs benoetigt
		RunEnvironment.getInstance().endAt(endAt);
		
		
		PotentialWitch w=null;
		//Erstelle Agenten
		for(int i=0; i<howMany; i++){
			double fearOfWitches = RandomHelper.nextDoubleFromTo(fearOfWitchesMin, fearOfWitchesMax);
			double fearOfAccusation = RandomHelper.nextDoubleFromTo(fearOfAccusationMin, fearOfAccusationMax);
			double suggestibility = RandomHelper.nextDoubleFromTo(suggestibilityMin, suggestibilityMax);
			double fearFactor =  RandomHelper.nextDoubleFromTo(fearFactorMin, fearFactorMax);
			w = new PotentialWitch(fearOfWitches, fearOfAccusation, suggestibility, fearFactor);
			context.add(w);
		}
		
		PotentialWitch.setAccusationBound(accBound);
		PotentialWitch.setAccusationBound2(accBound2);
		PotentialWitch.setSentenceBound(sentBound);
		PotentialWitch.setMaxWeight(maxWeight);
		PotentialWitch.setMaxEdges(maxEdges);
		PotentialWitch.setHowMany(howMany);
		//Erstelle Kanten
		for(Object obj : context.getObjects(PotentialWitch.class)){
			w = (PotentialWitch) obj;
			
		
			double outEdges = RandomHelper.nextIntFromTo(1, maxEdges); 
			
			for(int i=0; i<outEdges; i++){
				PotentialWitch acquaintance = (PotentialWitch) context.getRandomObject();
				//keine Kante zu sich selbst
				if(! acquaintance.equals(w) && acquaintance != null && w != null){
					
					double weight = RandomHelper.nextDoubleFromTo(1.0, maxWeight);
					network.addEdge(w, acquaintance, weight);
				}
				else{
					i--;
				}
			}
		}
		
		return context;
	}


}
