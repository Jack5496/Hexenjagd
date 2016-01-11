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
		
		int endAt = (Integer) p.getValue("endAt");		//Fuer Batch Runs benoetigt
		RunEnvironment.getInstance().endAt(endAt);
		
		PotentialWitch w=null;
		//Erstelle Agenten
		for(int i=0; i<howMany; i++){
			w = new PotentialWitch(0.7, 0.4, 0.5); //TODO: richtige Werte uebergeben
			context.add(w);
		}
		w.setAccusationBound(0.8);
		w.setSentenceBound(0.9);
		
		//Erstelle Kanten
		for(Object obj : context.getObjects(PotentialWitch.class)){
			w = (PotentialWitch) obj;
			
			double outEdges = RandomHelper.nextIntFromTo(1, 10); //TODO: wie viele Kanten?
			
			for(int i=0; i<outEdges; i++){
				PotentialWitch acquaintance = (PotentialWitch) context.getRandomObject();
				//keine Kante zu sich selbst
				if(! acquaintance.equals(w) && acquaintance != null && w != null){
					double weight = RandomHelper.nextDoubleFromTo(1.0, 15.0); //TODO: Anpassen Range?
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
