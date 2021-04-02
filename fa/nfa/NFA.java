package fa.nfa;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import fa.State;
import fa.dfa.DFA;
import fa.dfa.DFAState;

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
	 * @param STRING name of the state typical its alphabetical symbol

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
	 * @param STRING name of the state typical its alphabetical symbol
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
	 * Adds a state to list of end states and 
	 * @param STRING name of the state typical its alphabetical symbol
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
	 * Adds transition, symbol my symbol recursively 
	 * @param STRING fromState name of the state the transition is from
	 * @param CHAR onSymb the symbol that the transition happens on
	 * @param STRING toState name of the state transition ends on
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
		//recursion 
		from.addTransition(onSymb, to);
		//makes a list of Symbols already used on a state
		if(!sigma.contains(onSymb)){
			sigma.add(onSymb);
		}
	}
	/**
	 * Getter for Q
	 * @return SET a set of states that FA has
	 */
	@Override
	public Set<? extends State> getStates() {
		return Q;
	}
	/**
	 * Getter for F
	 * @return SET a set of final states that FA has
	 */
	@Override
	public Set<? extends State> getFinalStates() {
		return F;
	}

	/**
	 * Getter for Q
	 * @return State retuns the start state
	 */
	@Override
	public State getStartState() {
		return q0;
	}
	/**
	 * Getter for Q
	 * @return Set a set of transition symbols 
	 */
	@Override
	public Set<Character> getABC() {
		return sigma;
	}

	/**
	 * Converts a NFA to DFA
	 * @param none/the whole program
	 * @return DFA
	 */
	@Override
	public DFA getDFA() {
		//starts a new DFA
		DFA dfa = new DFA();
		Queue<Set<NFAState>> queue = new LinkedList<Set<NFAState>>();
		Set<Set<NFAState>> visited = new LinkedHashSet<Set<NFAState>>();
		Set<NFAState> startState = eClosure((NFAState) getStartState());
		//checks if start and final state are the same
		if (isFinal(startState)) {
			dfa.addFinalState(startState.toString());
		}
		//sets start state and adds to to que and visted
		dfa.addStartState(startState.toString());
		queue.add(startState);
		visited.add(startState);
		//goes though the queue set to add all elements from NFA to DFA
		while (!queue.isEmpty()) {
			Set<NFAState> currStates = queue.remove();
			//goes though each transition symbol 
			for (Character c : sigma) {
				//skips e transitions
				if (c == 'e') {
					continue;
				}
				//starts the transition state that is used for the rest of this method
				Set<NFAState> tranState = new LinkedHashSet<NFAState>();
				//
				for (NFAState state : currStates) {
					if (state.getTo(c) != null) {
						tranState.addAll(state.getTo(c));
						for (NFAState s : tranState) {
							tranState.addAll(eClosure(s));
						}
					}
				}
				Set<NFAState> equivilantState = equalState(tranState, visited);
				if (equivilantState != null) {
					tranState = equivilantState;
				}
				if (!visited.contains(tranState)) {
					queue.add(tranState);
					visited.add(tranState);
				}
				if (!isDFAState(tranState.toString(), dfa)) {
					if (isFinal(tranState)) {
						dfa.addFinalState(tranState.toString());
					} else {
						dfa.addState(tranState.toString());
					}
				}
				dfa.addTransition(currStates.toString(), c, tranState.toString());
			}
		}
		return dfa;
	}
	/**
	 * Getter for the state transtion to from a state on a symbol
	 * @param NFASTATE from The state we want the transition on
	 * @param CHAR onSymb the symbol that the transition happens on
	 * @return SET returns a NFA state that the symbol leads to
	 */
	@Override
	public Set<NFAState> getToState(NFAState from, char onSymb) {
		return from.getTo(onSymb);
	}
	/**
	 * Gets all the states you can get to on a e transition 
	 * @param NFASTATE from The state we want to trace the e Transitions 
	 * @return SET NFASTATES returns a Set of nfa states that you can visit off e transition
	 */
	@Override
	public Set<NFAState> eClosure(NFAState s) {
		//creates sets to keep track of where we have been
		Set<NFAState> eStates = new LinkedHashSet<NFAState>();
		Set<NFAState> visited = new LinkedHashSet<NFAState>();
		eStates.add(s);
		visited.add(s);
		//gets any transitions on e
		Set<NFAState> setOfStates = s.getTo('e');
		//adds to the list of visted to avoid looping
		if (setOfStates != null) {
			for (NFAState state : setOfStates) {
				eStates.addAll(eClosure(state, visited));
			}
		}
		//System.out.println(eStates);
		return eStates;
	}
	/**
	 * Getter for the state transition to from a state on a symbol
	 * @param NFASTATES from The state we are e tracing
	 * @param SET NFASTATES Set of visited states
	 * @return SET NFASTATES returns a set of eStates
	 */
	public Set<NFAState> eClosure(NFAState s, Set<NFAState> visited) {
		//creates a new list of estates
		Set<NFAState> eStates = new LinkedHashSet<NFAState>();
		eStates.add(s);
		visited.add(s);
		//gets any transitions on e
		Set<NFAState> setOfStates = s.getTo('e');
		if (setOfStates != null) {
			//if the state is not visted add the state to both the visted and e state list
			for (NFAState state : setOfStates) {
				if (!visited.contains(state)) {
					eStates.addAll(eClosure(state, visited));
				}
			}
		}
		return eStates;
	}
	/**
	 * Utility that checks if a state already exits
	 * @param String the name of the state
	 * @return NFASTATE returns the state if it already exists, or null if it does not.
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
	 * @param SET NFASTATES A set of states
	 * @return Boolean if it contains a final state returns true else false
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
	 * Utility that checks if a name is a state
	 * @param STRING nstate the name of a state
	 * @parm dfa the dfa to check if for states
	 * @return DFA Boolean true false
	 */
	private boolean isDFAState(String nstate, DFA dfa) {
		boolean exists = false;
		//creates the set to check against
		Set<DFAState> dfaStates = dfa.getStates();
		//loops though to check all states agaist name
		for (DFAState dstate : dfaStates) {
			if(nstate.equals(dstate.getName())) {
				exists = true;
				break;
			}
		}
		return exists;
	}
	/**
	 * Utility that checks if two sates are the same
	 * @param SET NFASTATES testState the first state
	 * @parm SET NFASTATES of visted NFA states
	 * @return SET NFASTATES the equivalent NFAstate
	 */
	private Set<NFAState> equalState(Set<NFAState> testState, Set<Set<NFAState>> visited) {
		Set<NFAState> equivilantState = null;
		for (Set<NFAState> state : visited) {
			if(testState.equals(state)) {
				equivilantState = state;
				break;
			}
		}
		return equivilantState;
	}

}
