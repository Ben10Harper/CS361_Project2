package fa.nfa;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import fa.State;
import fa.dfa.DFA;
import fa.dfa.DFAState;

/**
 * March 25, 2021
 * The class stores all the information read
 * from the input file, and creates the states
 * in order to simulate the given NFA, then
 * converts the given NFA into a DFA
 * @author Ben Harper and Andrew Haddon
 */
public class NFA implements NFAInterface {
	private Set<NFAState> Q;
	private Set<NFAState> F;
	private Set<Character> sigma;
	private NFAState q0;
	
	public NFA() {
		Q = new LinkedHashSet<NFAState>();
		F = new LinkedHashSet<NFAState>();
		sigma = new LinkedHashSet<Character>();
	}

	/**
	 * Adds the start state as well as adding it to the main list
	 * @param STRING name - name of the state typically its alphabetical symbol

	 */
	@Override
	public void addStartState(String name) {
		//passes the name to check if state exists
		NFAState state = alreadyExists(name);
		//checks if the state is a new state
		if (state == null) {
			//creates and adds the new state
			state = new NFAState(name);
			Q.add(state);
		}
		//sets state as start state
		q0 = state;
	}

	/**
	 * Adds a state to the main list
	 * @param STRING name - name of the state typically its alphabetical symbol
	 */
	@Override
	public void addState(String name) {
		//passes the name to check if state exists
		NFAState state = alreadyExists(name);
		//checks if the state is a new state
		if (state == null) {
			//creates and adds new state
			state = new NFAState(name);
			Q.add(state);
		}
	}
	
	/**
	 * Adds a state to list of final states and all states 
	 * @param STRING name - name of the state typically its alphabetical symbol
	 */
	@Override
	public void addFinalState(String name) {
		//passes the name to check if state exists
		NFAState state = alreadyExists(name);
		//checks if the state is a new state
		if (state == null) {
			//creates and adds new state to main state list and final state list
			state = new NFAState(name);
			Q.add(state);
			F.add(state);
		}
	}
	
	/**
	 * Adds transition to the state's delta map, with the symbol as the
	 * key and the toState as the value 
	 * @param STRING fromState - name of the state the transition is from
	 * @param CHAR onSymb - the symbol that the transition happens on
	 * @param STRING toState - name of the state transition ends on
	 */
	@Override
	public void addTransition(String fromState, char onSymb, String toState) {
		//checks that states exists
		NFAState from = alreadyExists(fromState);
		NFAState to = alreadyExists(toState);
		if (from == null || to == null) {
			System.out.println("One of those states doesn't exist!");
			return;
		}
		//adds to the fromState's delta map
		from.addTransition(onSymb, to);
		//adds to the list of symbols used in the machine
		if(!sigma.contains(onSymb)){
			sigma.add(onSymb);
		}
	}
	
	/**
	 * Getter for Q (All States)
	 * @return Set<NFAState> - a set of all states that FA has
	 */
	@Override
	public Set<? extends State> getStates() {
		return Q;
	}
	
	/**
	 * Getter for F (Final States)
	 * @return Set<NFAState> - a set of final states that FA has
	 */
	@Override
	public Set<? extends State> getFinalStates() {
		return F;
	}

	/**
	 * Getter for q0 (the start state)
	 * @return State - returns the start state
	 */
	@Override
	public State getStartState() {
		return q0;
	}
	
	/**
	 * Getter for sigma (set of alphabet symbols)
	 * @return Set<Character> - a set of transition symbols 
	 */
	@Override
	public Set<Character> getABC() {
		return sigma;
	}

	/**
	 * Converts our NFA into a DFA
	 * @return DFA - the new DFA converted from our NFA
	 */
	@Override
	public DFA getDFA() {
		//creates a new DFA
		DFA dfa = new DFA();
		//creates the queue for BFS and set of visited state so we don't have repeated states
		Queue<Set<NFAState>> queue = new LinkedList<Set<NFAState>>();
		Set<Set<NFAState>> visited = new LinkedHashSet<Set<NFAState>>();
		//gets the new start state for the DFA
		Set<NFAState> startState = eClosure((NFAState) getStartState());
		//checks if start state is a final state
		if (isFinal(startState)) {
			dfa.addFinalState(startState.toString());
		}
		//sets DFA's start state and adds it to the queue and the visited set
		dfa.addStartState(startState.toString());
		queue.add(startState);
		visited.add(startState);
		//goes though the queue set to add all elements from NFA to DFA
		while (!queue.isEmpty()) {
			//takes oldest state from the queue
			Set<NFAState> currStates = queue.remove();
			//goes though each transition symbol 
			for (Character c : sigma) {
				//skips e transitions
				if (c == 'e') {
					continue;
				}
				//starts the transition state that is used for the rest of this method
				Set<NFAState> tranState = new LinkedHashSet<NFAState>();
				//goes through each individual state in the set from the queue
				for (NFAState state : currStates) {
					//checks if the state has a transition on this char
					if (state.getTo(c) != null) {
						//adds all the possible states it could go to
						tranState.addAll(state.getTo(c));
						//loops through tranState and runs eclosure on each state to add free transitions
						for (NFAState s : tranState) {
							//adds the resulting states back into tranState
							tranState.addAll(eClosure(s));
						}
					}
				}
				//checks if the same state has been visited, but the states are in different orders
				Set<NFAState> equivilantState = equalState(tranState, visited);
				if (equivilantState != null) {
					//change the new state to the order of the old equivalent one
					tranState = equivilantState;
				}
				//checks if state has already been visited
				if (!visited.contains(tranState)) {
					//if not, add to the queue then add to visisted
					queue.add(tranState);
					visited.add(tranState);
				}
				//checks if the state is already in the DFA
				if (!isDFAState(tranState.toString(), dfa)) {
					//if not, check if it's final or not and add appropriately
					if (isFinal(tranState)) {
						dfa.addFinalState(tranState.toString());
					} else {
						dfa.addState(tranState.toString());
					}
				}
				//add the dfa transition
				dfa.addTransition(currStates.toString(), c, tranState.toString());
			}
		}
		//returns the newly made dfa
		return dfa;
	}
	
	/**
	 * Getter for the state the given state transitions to on the given symbol
	 * @param NFAState from - The state we want the transition from
	 * @param CHAR onSymb - the symbol that the transition happens on
	 * @return Set<NFAState> - returns a NFA state that the symbol leads to
	 */
	@Override
	public Set<NFAState> getToState(NFAState from, char onSymb) {
		return from.getTo(onSymb);
	}
	
	/**
	 * Gets all the states you can get to on a e transition 
	 * @param NFAState from - The state we want to trace the e Transitions 
	 * @return Set<NFAState> - returns a Set of nfa states that you can visit off e transition
	 */
	@Override
	public Set<NFAState> eClosure(NFAState s) {
		//creates sets to keep track of where we have been and where we can get to
		Set<NFAState> eStates = new LinkedHashSet<NFAState>();
		Set<NFAState> visited = new LinkedHashSet<NFAState>();
		//adds the given state to both
		eStates.add(s);
		visited.add(s);
		//gets all states s can get to using e transitions
		Set<NFAState> setOfStates = s.getTo('e');
		//checks if there are any states returned
		if (setOfStates != null) {
			//loops through each state gotten from the e transitions
			for (NFAState state : setOfStates) {
				//recursively calls eclosure on each new state and adds to eStates
				//in the end this leaves us with all possible states that can be visited
				//for free from the given state s
				eStates.addAll(eClosure(state, visited));
			}
		}
		//System.out.println(eStates);
		return eStates;
	}
	
	/**
	 * Gets all the states you can get to on a e transition 
	 * (same as above but includes the visited list)
	 * @param NFAState s - The state we are e tracing
	 * @param Set<NFAState> visited - Set of visited states
	 * @return Set<NFAState> - returns a set of eStates
	 */
	public Set<NFAState> eClosure(NFAState s, Set<NFAState> visited) {
		//creates a new list of estates
		Set<NFAState> eStates = new LinkedHashSet<NFAState>();
		eStates.add(s);
		visited.add(s);
		//gets any transitions on e
		Set<NFAState> setOfStates = s.getTo('e');
		//makes sure those states exist
		if (setOfStates != null) {
			//loops through all the states
			for (NFAState state : setOfStates) {
				//checks if we've already done the eClosure on that state
				if (!visited.contains(state)) {
					//recursively calls eClosure on each new state
					eStates.addAll(eClosure(state, visited));
				}
			}
		}
		return eStates;
	}
	
	/**
	 * Utility that checks if a state already exits in Q
	 * @param String name - the name of the state
	 * @return NFAState - returns the state if it already exists, or null if it does not.
	 */
	private NFAState alreadyExists(String name) {
		NFAState existing = null;
		//loops though all states in Q to check if the passed name is already a state
		for(NFAState state : Q) {
			if(state.getName().equals(name)) {
				existing = state;
				break;
			}
		}
		return existing;
	}
	
	/**
	 * Utility that checks if the state is a final state
	 * @param Set<NFAState> states - A set of states to test
	 * @return Boolean - if it contains a final state returns true else false
	 */
	private boolean isFinal(Set<NFAState> states) {
		boolean fin = false;
		//loops though all states in Q 
		for (NFAState state : states) {
			if (F.contains(state)) {
				fin = true;
			}
		}
		return fin;
	}
	
	/**
	 * Utility that checks a state is already in the DFA
	 * @param STRING - nstate the name of a state
	 * @param DFA dfa - the dfa to check for states
	 * @return Boolean - returns true or false if the state is in the DFA or not
	 */
	private boolean isDFAState(String nstate, DFA dfa) {
		boolean exists = false;
		//creates the set to check against
		Set<DFAState> dfaStates = dfa.getStates();
		//loops though to check all states against name
		for (DFAState dstate : dfaStates) {
			if(nstate.equals(dstate.getName())) {
				exists = true;
				break;
			}
		}
		return exists;
	}
	
	/**
	 * Utility that checks if two sates are the same but in different orders
	 * @param Set<NFAState> testState - the first state
	 * @parm Set<Set<NFAState>> visited - set of sets to test against
	 * @return Set<NFAState> the equivalent NFAstate, or null if one doesn't exist
	 */
	private Set<NFAState> equalState(Set<NFAState> testState, Set<Set<NFAState>> visited) {
		Set<NFAState> equivilantState = null;
		//loops through all sets of states in visited
		for (Set<NFAState> state : visited) {
			//checks if they're equal
			if(testState.equals(state)) {
				//if so, return the visited state
				equivilantState = state;
				break;
			}
		}
		return equivilantState;
	}

}
