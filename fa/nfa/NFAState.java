package fa.nfa;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

public class NFAState extends fa.State {

	private HashMap<Character, Set<NFAState>> delta;
	
	/**
	 * Starts a new nfa state
	 * @param String name the name of the state
	 */
	public NFAState(String name) {
		newState(name);
	}
	/**
	 * Starts a new nfa state and adds it to the set
	 * @param String name the name of the state
	 */
	private void newState(String name){
		this.name = name;
		delta = new HashMap<Character, Set<NFAState>>();
	}
	/**
	 * Adds a transition to existing states 
	 * @param NFAsstate toState the state is transitions to on symbol
	 * @param Char OnSymb the symbol to transition to the new state on.
	 */
	public void addTransition(char onSymb, NFAState toState){
		Set<NFAState> set = new LinkedHashSet<NFAState>();
		if (delta.get(onSymb) != null) {
			set = delta.get(onSymb);
		}
		set.add(toState);
		delta.put(onSymb, set);
	}
	/**
	 * Finds all the states the symbol transitions too. 
	 * @param Char symb
	 * @return Set NFAState a set of all states the symbol transtions to 
	 */
	public Set<NFAState> getTo(char symb){
		Set<NFAState> toStates = delta.get(symb);
		if(toStates != null) {
			return toStates;
		} else if (toStates == null && symb == 'e') {
			return null;
		} else {
			//System.out.println("No transition exists for this state on " + symb);
			return null;
		}
	}
	
}
