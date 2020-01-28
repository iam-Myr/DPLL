import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

/*
 * An implementation of the DPLL algorithm 
 * that reads a problem from a file
 * and outputs True if it is solvable 
 * and False if it is not.
 * Author: Myriam Kapon 
 * 23/6/2019
 */

public class Main {
	
	private static final int M = 800;
	private static final String filename = "800.txt";
	
	public static void main(String[] args) {
		
		ArrayList<ArrayList> Problem = new ArrayList();
		Problem = parse(filename);
		
		long start = System.nanoTime();   
		boolean solutionFound = DPLL(Problem);
		float  elapsedTime = System.nanoTime() - start;

		if (solutionFound)
			System.out.println("SOLUTION FOUND in " + elapsedTime/1000000000 + " seconds");
		else
			System.out.println("NO SOLUTION in " + elapsedTime/1000000000 + " seconds");
		
	}
	
	/* A parser that takes as input a file and outputs an ArrayList<ArrayList> 
	 * that corresponds to a set of clauses. Each clause is an ArrayList.
	 */
	static ArrayList<ArrayList> parse(String filename){
	
		ArrayList<ArrayList> Prob = new ArrayList();
		
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(filename));
		} catch (FileNotFoundException e) {
			System.out.println("Couldn't read file. Exiting..");
			System.exit(0);
			e.printStackTrace();
		}  
		
		scanner.nextLine();
		for(int i=0; i<M; i++) {  
			ArrayList<Integer> clause = new ArrayList();
			String[] str = scanner.nextLine().split(" ");
			for(int j=0; j < str.length; j++)
				clause.add(Integer.parseInt(str[j]));
			Prob.add(clause);
		}	
		return Prob;
	}
	
	
	/* Input: An ArrayList<ArrayList>
	 * Output: A truth value
	 * A DPLL recursive algorithm. It has two stages:
	 * 1) Unit Propagation 
	 * 2) Value Assignment
	 */
	static boolean DPLL(ArrayList<ArrayList> Prob) {
		
		//System.out.println("DPLL has been called. Size of Problem: " + Prob.size());
		ArrayList<ArrayList> Problem = new ArrayList(); //Creates a DEEP COPY of the ArrayList that gets passed
		for (ArrayList clause:Prob)
			Problem.add((ArrayList) clause.clone());
		
		int unitClause = findUnitClause(Problem);
		while (unitClause != 0) {
			BCP(Problem, unitClause); //Perform Propagation for every unit clause that exists
			unitClause = findUnitClause(Problem);
		}
				
		if (Problem.size() == 0) //If the set is empty
			return true;
		
		for (ArrayList clause:Problem) //If the set contains an empty clause
			if (clause.size() == 0) {
				System.out.println("FAILED to find solution. Backtracking....");
				return false;
			}
	
		int literal = findUnassignedLiteral(Problem); 
		//Create two new sets, each containing one assigned value for the chosen literal
		ArrayList<Integer> literalClause1 = new ArrayList();
		literalClause1.add(literal);
		ArrayList<ArrayList> Problem1 = new ArrayList(Problem);
		Problem1.add(0,literalClause1);
	
		ArrayList<Integer> literalClause2 = new ArrayList();
		literalClause2.add(-literal);
		ArrayList<ArrayList> Problem2 = new ArrayList(Problem);
		Problem2.add(0, literalClause2);
		
		return (DPLL(Problem1) || DPLL(Problem2));
	}
	
	/* The Boolean Constraint Propagation.
	 */
	static void BCP(ArrayList<ArrayList>Problem, int unitClause) {
		for (Iterator<ArrayList> iterator = Problem.iterator(); iterator.hasNext();) {
			ArrayList clause = iterator.next();		
			for (Iterator<Integer> iterator2 = clause.iterator(); iterator2.hasNext();) {
				Integer l = iterator2.next();
				if (l == unitClause)   //If the clause contains the unit clause literal
					iterator.remove();  //remove the clause
				else if(l == -unitClause) //If the clause contains the NOT(literal)
					iterator2.remove();	//remove the NOT(literal)
				}
			}
	}
	
	/* Finds the first unassigned literal
	 */
	static int findUnassignedLiteral(ArrayList<ArrayList> Problem) {	
		for(ArrayList clause:Problem) 
			if (clause.size() > 1)
				return (int) clause.get(0);
		return 0;	
	}
	
	/* Finds the first unit clause, removes it and returns it.
	 */
	static Integer findUnitClause(ArrayList<ArrayList> Problem) {
		for (Iterator<ArrayList> iterator = Problem.iterator(); iterator.hasNext();) {
			ArrayList clause = iterator.next();
			if (clause.size() == 1) {
				int unitClause = (int) clause.get(0);
				//System.out.println("Found unit clause: " + unitClause);
				iterator.remove();
				return unitClause;
			}
		}
		return 0;
	}	
}
