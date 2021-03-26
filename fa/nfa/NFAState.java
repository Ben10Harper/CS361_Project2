package fa.nfa;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

public class NFAState extends fa.State {

	private HashMap<Character, Set<NFAState>> delta;
	
	public NFAState(String name) {
		newState(name);
	}
	
	private void newState(String name){
		this.name = name;
		delta = new HashMap<Character, Set<NFAState>>();
	}
	
	public void addTransition(char onSymb, NFAState toState){
		Set<NFAState> set = new LinkedHashSet<NFAState>();
		if (delta.get(onSymb) != null) {
			set = delta.get(onSymb);
		}
		set.add(toState);
		delta.put(onSymb, set);
	}
	
	public Set<NFAState> getTo(char symb){
		Set<NFAState> toStates = delta.get(symb);
		if(toStates != null) {
			return toStates;
		} else {
			System.out.println("No transition exists for this state on " + symb);
			return null;
		}
	}
	
}
