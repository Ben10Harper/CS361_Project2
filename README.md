# CS361 Project 2: NFA To DFA

* Author: Andrew Haddon
* Class: CS361 Section 01
* Semester: Spring 2021

* Author: Ben Harper
* Class: CS361 Section 01
* Semester: Spring 2021


## Overview
This program reads in input about a nondeterministic finite automata and converts to a deterministic furniture automata. 

When using a Linux based system compile the driver method from the top level folder using javac fa/dfa/NFADriver.java you can then use java fa.dfa.NFADriver ./tests/p1tc(test number).txt. The most complex step is writing the plain text files.
An example follows.

Final states  			B
Start States			A
Q states 			B A
Transitions in Paris 		a0a a1b bea
Test Srings			0
1
00
101
e

## Discussion


Everything went well other then getDFA and getEclosure, there was an issue with equivalent states like BA AB were being counted as separate state when they should have been the same. 
Looking at the NFA driver and implementing as it was required well holding off on getDFA and eclosure tell the end.
Most of the research was about Queues and linked hash sets as we used a different one to solve problems for the first project.
Again, errors when the start state was also the final state, had to make a helper to solve that issue. Another issue where ben was breaking out of a loop early. 
The tricky parts was eClosure and getDFA the main issue was the depth vs breadth first search. 
With get DFA once Ben understood how queues work it was a lot easer to complete. 
More testing could never go amiss, or maybe clean up and helper methods. 
It always good to just know what is possible in code and experience reading though others code never goes amiss.

## Testing

We primary tested with tests given comparing it to the expected output. We also tried other custom test files to catch errors like the duplicate states for start and final state

## Sources used

Used to figure out how to add data to a set that's inside a map.
https://stackoverflow.com/questions/15797446/how-do-i-add-values-to-a-set-inside-a-map/15797532

Used to better understand LinkedHashSets, since we used a different set in Project 1.
https://docs.oracle.com/javase/7/docs/api/java/util/LinkedHashSet.html

Used to figure out how to add all of the element of one set to another all at once.
https://stackoverflow.com/questions/8032579/adding-elements-from-two-sets

Used to figure out how to initialize and use queues in Java.
https://www.geeksforgeeks.org/queue-interface-java/

Used after I found a problem with breaking my loop, realized I needed a continue so I only skipped that one iteration.
https://www.w3schools.com/java/java_break.asp


----------
