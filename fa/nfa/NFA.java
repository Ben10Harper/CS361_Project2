package fa.nfa;

import java.util.LinkedHashSet;
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

	@Override
	public void addStartState(String name) {
		NFAState state = alreadyExists(name);
		if (state == null) {
			state = new NFAState(name);
			Q.add(state);
		}
		q0 = state;
	}

	@Override
	public void addState(String name) {
		NFAState state = alreadyExists(name);
		if (state == null) {
			state = new NFAState(name);
			Q.add(state);
		}
	}

	@Override
	public void addFinalState(String name) {
		NFAState state = alreadyExists(name);
		if (state == null) {
			state = new NFAState(name);
			Q.add(state);
			F.add(state);
		}
	}

	@Override
	public void addTransition(String fromState, char onSymb, String toState) {
		NFAState from = alreadyExists(fromState);
		NFAState to = alreadyExists(toState);
		if (from == null || to == null) {
			System.out.println("One of those states doesn't exist!");
			return;
		}
		from.addTransition(onSymb, to);
		if(!sigma.contains(onSymb)){
			sigma.add(onSymb);
		}
	}

	@Override
	public Set<? extends State> getStates() {
		return Q;
	}

	@Override
	public Set<? extends State> getFinalStates() {
		return F;
	}

	@Override
	public State getStartState() {
		return q0;
	}

	@Override
	public Set<Character> getABC() {
		return sigma;
	}

	@Override
	public DFA getDFA() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<NFAState> getToState(NFAState from, char onSymb) {
		return from.getTo(onSymb);
	}

	@Override
	public Set<NFAState> eClosure(NFAState s) {
		Set<NFAState> eStates = new LinkedHashSet<NFAState>();
		Set<NFAState> visited = new LinkedHashSet<NFAState>();
		eStates.add(s);
		visited.add(s);
		Set<NFAState> setOfStates = s.getTo('e');
		if (setOfStates != null) {
			for (NFAState state : setOfStates) {
				eStates.addAll(eClosure(state, visited));
			}
		}
		System.out.println(eStates);
		return eStates;
	}
	
	public Set<NFAState> eClosure(NFAState s, Set<NFAState> visited) {
		Set<NFAState> eStates = new LinkedHashSet<NFAState>();
		eStates.add(s);
		visited.add(s);
		Set<NFAState> setOfStates = s.getTo('e');
		if (setOfStates != null) {
			for (NFAState state : setOfStates) {
				if (!visited.contains(state)) {
					eStates.addAll(eClosure(state, visited));
				}
			}
		}
		return eStates;
	}
	
	private NFAState alreadyExists(String name) {
		NFAState existing = null;
		for(NFAState state : Q) {
			if(state.getName().equals(name)) {
				existing = state;
				break;
			}
		}
		return existing;
	}

}
