package hexenjagd;

import java.util.Iterator;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.util.ContextUtils;

public class PotentialWitch {

	private static double accusationBound;
	private static double sentenceBound;
	private int accused; //Number of People accusing this PotentialWitch 
	private int _accused;
	private boolean sentenced;
	private double fearOfWitches; //
	private double fearOfAccusation;
	private double suggestibility;
	private boolean _sentenced;
	
	public PotentialWitch(double fearOfWitches, double fearOfAccusation, double suggestibility){
		
		this.fearOfWitches = fearOfWitches;
		this.fearOfAccusation = fearOfAccusation;
		this.suggestibility = suggestibility;
		sentenced = false;
		accused = 0;
	}
	
	
	

	public static double getAccusationBound() {
		return accusationBound;
	}




	public static void setAccusationBound(double accusationBound) {
		PotentialWitch.accusationBound = accusationBound;
	}




	public static double getSentenceBound() {
		return sentenceBound;
	}




	public static void setSentenceBound(double sentenceBound) {
		PotentialWitch.sentenceBound = sentenceBound;
	}




	@ScheduledMethod(start = 1.0, interval = 1.0)
	public void step(){
		fearOfWitches *= 1.02;   //TODO: Parameter!!!
		fearOfAccusation *= 1.02;
		if(!isSentenced()){
			Context<PotentialWitch> context = (Context<PotentialWitch>)ContextUtils.getContext(this);

			calcAccusation(context); //accusation jeden Schritt zurücksetzen, aber beeinflusst nächste Runde.
			
			//TODO: Angst neu berechnen
			//abhaengig machen davon wie viele Hexen angeklagt worden sind.

		}
		
	}
	
	private void calcSentences() {
		Context<PotentialWitch> context = (Context<PotentialWitch>)ContextUtils.getContext(this);
		Network<PotentialWitch> network = (Network<PotentialWitch>)context.getProjection("hexenjagd");
		int max = getMaxAccusations();
		Iterable<PotentialWitch> witches = network.getNodes();
		for(PotentialWitch w: witches){
			if(((double)w.getAccused()/(double)max) > sentenceBound)
				w._sentenced = true;
		}
	}

	public int getAccused() {
		return accused;
	}

	public void setAccused(int accused) {
		this.accused = accused;
	}

	private int getMaxAccusations(){
		int max = 0;
		Context<PotentialWitch> context = (Context<PotentialWitch>)ContextUtils.getContext(this);
		Network<PotentialWitch> network = (Network<PotentialWitch>)context.getProjection("hexenjagd");
		Iterable<PotentialWitch> witches = network.getNodes();
		for(PotentialWitch w: witches){
			if(!w.isSentenced() && w.getAccused()>max)
				max = w.getAccused();
		}
		return max;
	}

	public void calcAccusation(Context<PotentialWitch> context){
		
		Network<PotentialWitch> network = (Network<PotentialWitch>)context.getProjection("hexenjagd");
		_accused = 0;
		if(network != null){
			for(Object obj: network.getAdjacent(this)){
				PotentialWitch possAcc = (PotentialWitch) obj;
				RepastEdge<PotentialWitch> edge = network.getEdge(possAcc, this);
				
				if(!possAcc.equals(this) && !possAcc.isSentenced()){
					double accusation = (possAcc.getFearOfWitches() + possAcc.getFearOfAccusation() + (1-(edge.getWeight()/15)) + (accused*possAcc.getSuggestibility()) ) /4 ; //TODO: 15 anpassen?
					if (accusation >= accusationBound){
						incrementAccusation();
					}
					
				}
			}
		}
	}
	
	private double getSuggestibility() {
		// TODO Auto-generated method stub
		return suggestibility;
	}

	private double getFearOfAccusation() {
		// TODO Auto-generated method stub
		return fearOfAccusation;
	}

	private double getFearOfWitches() {
		// TODO Auto-generated method stub
		return fearOfWitches;
	}

	@ScheduledMethod(start = 0.5, interval = 1.0)
	public void update(){
		if(!sentenced){
			accused = _accused;
			calcSentences();
			sentenced = _sentenced;
		}
	}
	
	public void incrementAccusation(){
		_accused++;
	}
	
	public int nbAccusations(){
		return accused;
	}
	
	public boolean isSentenced(){
		return sentenced;
	}
	
	public int sentenced(){
		
		return (sentenced? 1:0);
	}
	
	
}
