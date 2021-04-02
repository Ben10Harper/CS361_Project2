package fa.nfa;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * March 25, 2021
 * The class stores all the information
 * for each state, such as its name
 * and the map to find it's next state.
 * It also contains methods to set and
 * get the state's information.
 * @author Ben Harper and Andrew Haddon
 */
public class NFAState extends fa.State {

	//HashMap to store transitions
	private HashMap<Character, Set<NFAState>> delta;
	
	/**
	 * Starts a new nfa state
	 * @param String name - the name of the state
	 */
	public NFAState(String name) {
		newState(name);
	}
	
	/**
	 * Creates the new state, giving it a name and new delta map
	 * @param String name the name of the state
	 */
	private void newState(String name){
		this.name = name;
		delta = new HashMap<Character, Set<NFAState>>();
	}
	
	/**
	 * Adds a transition to existing states 
	 * @param Char onSymb - the symbol it transitions on
	 * @param NFAState toState - the state that it goes to on the given symbol
	 */
	public void addTransition(char onSymb, NFAState toState){
		//creates a new set of NFAState's
		Set<NFAState> set = new LinkedHashSet<NFAState>();
		//checks if it already has a set of states on that transition
		if (delta.get(onSymb) != null) {
			//sets the new set to the contents of the old set
			set = delta.get(onSymb);
		}
		//adds the new state to the set
		set.add(toState);
		//puts the set back
		delta.put(onSymb, set);
	}
	
	/**
	 * Finds all the states this state transitions to on the given symbol 
	 * @param Char symb - the symbol to be used as the key in the map
	 * @return Set<NFAState> - a set of all states this state transitions to on the symbol 
	 */
	public Set<NFAState> getTo(char symb){
		//gets the set of states
		Set<NFAState> toStates = delta.get(symb);
		//checks if it exists
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
