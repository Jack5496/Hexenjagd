package hexenjagd;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.util.ContextUtils;

public class PotentialWitch {

	
	private int accused; //Number of People accusing this PotentialWitch 
	private int _accused;
	private double fearOfWitches; //
	private double fearOfAccusation;
	
	public PotentialWitch(double fearOfWitches, double fearOfAccusation){
		this.fearOfWitches = fearOfWitches;
		this.fearOfAccusation = fearOfAccusation;
		accused = 0;
	}
	
	@ScheduledMethod(start = 1.0, interval = 1.0)
	public void step(){
		//jeden Schritt was tun?
		Context<PotentialWitch> context = (Context<PotentialWitch>)ContextUtils.getContext(this);
		//TODO: Accusation jeden Schritt zurücksetzen oder behalten?
		calcAccusation(context);
		
		//TODO: ersetze 10 durch Schwellwert
		//Oder abhängig davon, wie viele Hexen angeklagt wurden?
		//Nur Hexe mit meisten Anklagen?
		if(accused > 10){
			//context.remove(this); //Agent 'dies'
		}
		
	}
	
	public void calcAccusation(Context<PotentialWitch> context){
		
		Network<PotentialWitch> network = (Network<PotentialWitch>)context.getProjection("hexenjagd");
		
		if(network != null){
		for(Object obj: network.getInEdges(this)){
			RepastEdge<PotentialWitch> edge = (RepastEdge<PotentialWitch>) obj;
			PotentialWitch possibleAccuser = (PotentialWitch) edge.getSource();
			
			if(!possibleAccuser.equals(this)){
				//TODO: Berechnung Wahrscheinlichkeit anklage
				//ersetze dann Math.random() durch Wahrscheinlichkeit
				//und 0.8 durch Schwellwert
				// Wenn Accused nicht zurückgesetzt wird, mit einbeziehen wie viele schon?
				if(Math.random()>0.8){
					incrementAccusation();
				}
			}
		}}
	}
	
	@ScheduledMethod(start = 0.5, interval = 1.0)
	public void update(){
		accused = _accused;
	}
	
	public void incrementAccusation(){
		_accused++;
	}
	
	public int nbAccusations(){
		return accused;
	}
}
