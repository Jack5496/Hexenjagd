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
	private static int accusationBound2;
	private static double maxWeight;
	private static int maxEdges;
	private static int howMany;
	private int accused; //Number of People accusing this PotentialWitch 
	private int _accused;
	private boolean sentenced;
	private double fearOfWitches; //
	private double fearOfAccusation;
	private double suggestibility;
	private boolean _sentenced;
	private double fearFactor;
	
	public PotentialWitch(double fearOfWitches, double fearOfAccusation, double suggestibility, double fearFactor){
		
		setFearOfWitches(fearOfWitches);
		setFearOfAccusation(fearOfAccusation);
		this.suggestibility = suggestibility;
		sentenced = false;
		accused = 0;
		this.fearFactor = fearFactor;
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
		
		if(!isSentenced()){
			Context<PotentialWitch> context = (Context<PotentialWitch>)ContextUtils.getContext(this);

			calcAccusation(context); //accusation jeden Schritt zurücksetzen, aber beeinflusst nächste Runde.
			

		}
		
	}
	
	private void calcSentence() {
		/*int counter = 0;
		Context<PotentialWitch> context = (Context<PotentialWitch>)ContextUtils.getContext(this);
		Network<PotentialWitch> network = (Network<PotentialWitch>)context.getProjection("hexenjagd");
		int max = getMaxAccusations();
		Iterable<PotentialWitch> witches = network.getNodes();
		for(PotentialWitch w: witches){
			if(!w.isSentenced() && w.getAccused()> accusationBound2 && ((double)w.getAccused()/(double)max) > sentenceBound){
				w._sentenced = true;
				counter++;
			}
		}
		System.out.println(counter);
		return counter;*/
		int max = getMaxAccusations();
		if(!this.isSentenced() && this.getAccused()> accusationBound2 && ((double)this.getAccused()/(double)max) > sentenceBound){
				this._sentenced = true;
		}
	}

	public int getAccused() {
		return accused;
	}

	public void setAccused(int accused) {
		this.accused = accused;
	}

	private int getMaxAccusations(){
		int maxAccusations = 0;
		Context<PotentialWitch> context = (Context<PotentialWitch>)ContextUtils.getContext(this);
		Network<PotentialWitch> network = (Network<PotentialWitch>)context.getProjection("hexenjagd");
		Iterable<PotentialWitch> witches = network.getNodes();
		for(PotentialWitch w: witches){
			if(!w.isSentenced() && w.getAccused()>maxAccusations){
				maxAccusations = w.getAccused();
			}
		}
		return maxAccusations;
	}
	private int getAllAccusations(){
		int nb = 0;
		Context<PotentialWitch> context = (Context<PotentialWitch>)ContextUtils.getContext(this);
		Network<PotentialWitch> network = (Network<PotentialWitch>)context.getProjection("hexenjagd");
		Iterable<PotentialWitch> witches = network.getNodes();
		for(PotentialWitch w: witches){
			if(!w.isSentenced())
				nb+= w.accused();
		}
		return nb;
	}

	public void calcAccusation(Context<PotentialWitch> context){
		
		Network<PotentialWitch> network = (Network<PotentialWitch>)context.getProjection("hexenjagd");
		_accused = 0;
		if(network != null){
			for(Object obj: network.getAdjacent(this)){
				PotentialWitch possAcc = (PotentialWitch) obj;
				RepastEdge<PotentialWitch> edge = network.getEdge(possAcc, this);
				
				if(!possAcc.equals(this) && !possAcc.isSentenced()){
					int max = getMaxAccusations();
					//System.out.println(max);
					if(max < 1)
						max = 1;
					
					double alreadyAccused = ((accused/max)*possAcc.getSuggestibility());
					double accusation;
					if(alreadyAccused>0)
						accusation = (possAcc.getFearOfWitches()*2 + possAcc.getFearOfAccusation() + (1-(edge.getWeight()/maxWeight))*2 + alreadyAccused*2 ) /7 ; 
					else{	
						accusation = (possAcc.getFearOfWitches()*2 + possAcc.getFearOfAccusation() + (1-(edge.getWeight()/maxWeight))*2 ) /5 ;
					}
					if (accusation >= accusationBound){
						incrementAccusation();
					}
					
				}
			}
		}
	}
	
	public double getSuggestibility() {
		// TODO Auto-generated method stub
		return suggestibility;
	}

	public double getFearOfAccusation() {
		// TODO Auto-generated method stub
		return fearOfAccusation;
	}

	public double getFearOfWitches() {
		// TODO Auto-generated method stub
		return fearOfWitches;
	}
	
	public void setFearOfWitches(double fear ){
		if(fear > 1){
			fearOfWitches = 1;
		}
		else if (fear <0){
			fearOfWitches = 0;
		}
		else{
			fearOfWitches = fear;
		}
	}
	
	public void setFearOfAccusation(double fear ){
		if(fear > 1){
			fearOfAccusation = 1;
		}
		else if (fear <0){
			fearOfAccusation = 0;
		}
		else{
			fearOfAccusation = fear;
		}
	}

	@ScheduledMethod(start = 0.5, interval = 1.0)
	public void update(){
		if(!sentenced){
			accused = _accused;
			/*int cntS =*/ calcSentence();
			//calcFear(cntS, getAllAccusations());
			sentenced = _sentenced;
		}
	}
	
	private void calcFear(int sentences, int accusations) {
		
		if(sentences >0)
			setFearOfWitches(getFearOfWitches() - getFearOfWitches()*fearFactor);
		else
			setFearOfWitches(getFearOfWitches() + getFearOfWitches()*fearFactor);
		if(accusations >0)
			setFearOfAccusation(getFearOfAccusation() + getFearOfAccusation()*fearFactor);
		else
			setFearOfAccusation(getFearOfAccusation() - getFearOfAccusation()*fearFactor);
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
	
	public int accused(){
		if(accused>0){
			return 1;
		}
		return 0;
		
	}




	public static void setAccusationBound2(int accBound2) {
		accusationBound2 = accBound2;
		
	}




	public static void setMaxWeight(double maxWeight) {
		PotentialWitch.maxWeight = maxWeight;
		
	}




	public static void setMaxEdges(int maxEdges) {
		PotentialWitch.maxEdges = maxEdges;
		
	}




	public static void setHowMany(int howMany) {
		PotentialWitch.howMany = howMany;
		
	}
	
}
