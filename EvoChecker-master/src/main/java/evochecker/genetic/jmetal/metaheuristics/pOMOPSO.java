// pOMOPSO.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2013 Antonio J. Nebro
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package evochecker.genetic.jmetal.metaheuristics;

import jmetal.core.*;
import jmetal.encodings.solutionType.ArrayRealSolutionType;
import jmetal.operators.mutation.Mutation;
import jmetal.util.Distance;
import jmetal.util.JMException;
import jmetal.util.NonDominatedSolutionList;
import jmetal.util.PseudoRandom;
import jmetal.util.archive.CrowdingArchive;
import jmetal.util.comparators.CrowdingDistanceComparator;
import jmetal.util.comparators.DominanceComparator;
import jmetal.util.comparators.EpsilonDominanceComparator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import evochecker.genetic.jmetal.encoding.ArrayInt;
import evochecker.genetic.jmetal.encoding.ArrayReal;
import evochecker.genetic.jmetal.encoding.XReal;

/**
 * This class representing an asynchronous version of pOMOPSO algorithm
 */
public class pOMOPSO extends Algorithm {
	/**
	* Evaluates algorithm on model
	*/  
	IParallelEvaluator parallelEvaluator_ ;  				//NEWLINE CREATED: (RECORD OF LAST EDIT)
 
	/**
	 * Stores the number of particles_ used
	 */
	private int particlesSize_;
  
	/**
	 * Stores the maximum size for the archive
	 */
	private int archiveSize_;
  
	/**
	 * Stores the maximum number of iteration_
	 */
	private int maxIterations_;
  
	/**
	 * Stores the current number of iteration_
	 */
	private int iteration_;
  
	/**
	 * Stores the perturbation used by the non-uniform mutation
	 */
	private double perturbation_;
  
	/**
	 * Stores the particles
	 */
	private SolutionSet particles_;
  
	/**
	 * Stores the best_ solutions founds so far for each particles
	 */
	private Solution[] best_;
  
	/**
	 * Stores the leaders_
	 */
	private CrowdingArchive leaders_ ;
  
	/**
	 * Stores the epsilon-archive
	 */
	private NonDominatedSolutionList eArchive_;
  
	/**
	 * Stores the speed_ of each particle
	 */
	private double [][] speed_;  
  
	/**
	 * Stores a comparator for checking dominance
	 */
	private Comparator dominance_;
  
	/**
	 * Stores a comparator for crowding checking
	 */
	private Comparator crowdingDistanceComparator_;
  
	/**
	 * Stores a <code>Distance</code> object
	 */
	private Distance distance_;
  
	/**
	 * Stores a operator for uniform mutations
	 */
	private Operator uniformMutation_;
  
	/**
	 * Stores a operator for non uniform mutations
	 */ 
	private Operator nonUniformMutation_;
  
	/**
	 * eta_ value
	 */
	private double eta_ = 0.0075;

	/**
	 * Constructor
	 * @param problem Problem to solve
	 * @param evaluator Parallel evaluator
	 */  
	public pOMOPSO(Problem problem, IParallelEvaluator evaluator) {                
		super (problem) ;

		parallelEvaluator_ = evaluator ; 						//NEWLINE CREATED: (RECORD OF LAST EDIT)
	} // pOMOPSO
  
  /**
   * Initialize all parameter of the algorithm
   */
public void initParams(){
	particlesSize_ = ((Integer)getInputParameter("swarmSize")).intValue();
    archiveSize_   = ((Integer)getInputParameter("archiveSize")).intValue();
    maxIterations_ = ((Integer)getInputParameter("maxIterations")).intValue();

    
    particles_     = new SolutionSet(particlesSize_);        
    best_          = new Solution[particlesSize_];
    leaders_       = new CrowdingArchive(archiveSize_,problem_.getNumberOfObjectives());
    eArchive_      = new NonDominatedSolutionList(new EpsilonDominanceComparator(eta_));
    
    uniformMutation_ = (Mutation)operators_.get("uniformMutation") ;
    nonUniformMutation_ = (Mutation)operators_.get("nonUniformMutation") ;
    
    // Create the dominator for equadless and dominance
    dominance_          = new DominanceComparator();    
    crowdingDistanceComparator_ = new CrowdingDistanceComparator();
    distance_           = new Distance();
    
    // Create the speed_ vector
    speed_ = new double[particlesSize_][problem_.getNumberOfVariables()];
  } // initParams
           
  
  /**
   * Update the speed of each particle
   * @throws JMException 
   */
 	private void computeSpeed() throws JMException{        
 		double r1,r2,W,C1,C2; 
 		Variable[] bestGlobal;                                            
        
 		for (int i = 0; i < particlesSize_; i++){
 			Variable[] particle     = particles_.get(i).getDecisionVariables();
 			Variable[] bestParticle = best_[i].getDecisionVariables();                        
 			
 			//Select a global best_ for calculate the speed of particle i, bestGlobal
 			Solution one, two;
 			int pos1 = PseudoRandom.randInt(0,leaders_.size()-1);
 			int pos2 = PseudoRandom.randInt(0,leaders_.size()-1);
 			one = leaders_.get(pos1);
 			two = leaders_.get(pos2);

 			if (crowdingDistanceComparator_.compare(one,two) < 1)
 				bestGlobal = one.getDecisionVariables();
 			else
 				bestGlobal = two.getDecisionVariables();
 			//
            
 			//Params for velocity equation
 			r1 = PseudoRandom.randDouble();
 			r2 = PseudoRandom.randDouble();
 			C1 = PseudoRandom.randDouble(1.5,2.0);
 			C2 = PseudoRandom.randDouble(1.5,2.0);
 			W  = PseudoRandom.randDouble(0.1,0.5);            
 			//

 			for (int var = 0; var < particle.length; var++){                                     
 				//Computing the velocity of this particle
 				
// 				System.out.println("var = " + var + "\nBest particle = " + bestParticle); 					//NEWLINE CREATED: (RECORD OF LAST EDIT)
// 				System.out.println("Best particle[0] = " + bestParticle[0].getVariableType()); 	//ArrayReal
// 				System.out.println("Best particle[1] = " + bestParticle[1].getVariableType()); 	//ArrayInt
// 				System.out.println("Best particle[var] = " + bestParticle[var]); 							//NEWLINE CREATED: (RECORD OF LAST EDIT)
// 				System.out.println("Best particle[var] type = " + (bestParticle[var]).getClass().getName());//NEWLINE CREATED: (RECORD OF LAST EDIT)
// 				System.out.println("Best particle = " + bestParticle.length);
//				System.out.println("!!!" + myBestVar.getValue(0));
// 				System.out.println("!!!" + myParticleVar.getValue(0));
// 				System.out.println("speed_" + speed_.length);		//100
// 				System.out.println("speed_[0]" + speed_[0].length);	//9
 				
 				
 				if(var == 0) {
 					ArrayReal myBestVar = (ArrayReal) bestParticle[var];
 					ArrayReal myBestGlobal = (ArrayReal) bestGlobal[var];
 					ArrayReal myParticleVar = (ArrayReal) particle[var];
 	 				speed_[i][var] = W  * speed_[i][var] +
 	 						C1 * r1 * (myBestVar.getValue(var) -       
 	 								myParticleVar.getValue(var)) +
 	 						C2 * r2 * (myBestGlobal.getValue(var) - 
 	 								myParticleVar.getValue(var));
 				} else if(var == 1) {	
 					ArrayInt myBestVar = (ArrayInt) bestParticle[var];
 					ArrayInt myBestGlobal = (ArrayInt) bestGlobal[var];
 					ArrayInt myParticleVar = (ArrayInt) particle[var];
 	 				speed_[i][var] = W  * speed_[i][var] +
 	 						C1 * r1 * (myBestVar.getValue(var) -       
 	 								myParticleVar.getValue(var)) +
 	 						C2 * r2 * (myBestGlobal.getValue(var) - 
 	 								myParticleVar.getValue(var));
 				}else {
 					System.out.println("Error, not int or real");
 				}

 			}
                
 		}
 	} // computeSpeed
     
 	/**
 	 * Update the position of each particle
 	 * @throws JMException 
 	 */
 	private void computeNewPositions() throws JMException{
 		for (int i = 0; i < particlesSize_; i++){
 			Variable[] particle = particles_.get(i).getDecisionVariables();
 		    //particle.move(speed_[i]);
 		    for (int var = 0; var < particle.length; var++){
 		    	
// 				System.out.println("particle[var] type = " + (particle[var]).getClass().getName());//NEWLINE CREATED: (RECORD OF LAST EDIT)
// 				System.out.println("particle[var] = " + particle[var]);
// 				System.out.println("particles " + particle.length); //2 long but maybe only sometimes? Reals in one space and ints in another.
// 				System.out.println("particles_" + particle.);
// 				System.out.println("particle[0] = " + particle[0]);
// 				System.out.println("particle[1] = " + particle[1]);
// 				System.out.println("rounddown test = " + (int) Math.round(0.5));
// 				System.out.println("problem_.getLowerLimit(var)" + problem_.getLowerLimit(var));
// 				System.out.println("problem_.getUpperLimit(var)" + problem_.getUpperLimit(var));
// 				System.out.println("speed_[i][var]" + speed_[i][var]);
 				
 				if(var == 0) {
 					ArrayReal myParticle = (ArrayReal) particle[var];
 					myParticle.setValue(var, myParticle.getValue(var)+ speed_[i][var]);
	 		        if (myParticle.getValue(var) < problem_.getLowerLimit(var)){
	 		        	myParticle.setValue(var, problem_.getLowerLimit(var));                    
	 		        	speed_[i][var] = speed_[i][var] * -1.0;    
	 		        }
	 		        if (myParticle.getValue(var) > problem_.getUpperLimit(var)){
	 		        	myParticle.setValue(var, problem_.getUpperLimit(var));                    
	 		        	speed_[i][var] = speed_[i][var] * -1.0;
	 		        }
 				} else if(var == 1) {  				//Find out if this is relevant, after all we're altering an int aren't we? Is that okay to do
 					ArrayInt myParticle = (ArrayInt) particle[var];
 					myParticle.setValue(var, (int) Math.round(myParticle.getValue(var)+ speed_[i][var]));
 	 		        if (myParticle.getValue(var) < problem_.getLowerLimit(var)){
 	 		        	myParticle.setValue(var, (int) Math.round(problem_.getLowerLimit(var)));                    
 	 		        	speed_[i][var] = speed_[i][var] * -1.0;    
 	 		        }
 	 		        if (myParticle.getValue(var) > problem_.getUpperLimit(var)){
 	 		        	myParticle.setValue(var, (int) Math.round(problem_.getUpperLimit(var)));                    
 	 		        	speed_[i][var] = speed_[i][var] * -1.0;    
 	 		        }    
 				} else {
 					System.out.println("Error, not int or real");
 				}
// 		    	
//// 		        particle[var].setValue(particle[var].getValue()+ speed_[i][var]);
//// 		        if (particle[var].getValue() < problem_.getLowerLimit(var)){
//// 		        	particle[var].setValue(problem_.getLowerLimit(var));                    
//// 		        	speed_[i][var] = speed_[i][var] * -1.0;    
//// 		        }
//// 		        if (particle[var].getValue() > problem_.getUpperLimit(var)){
//// 		        	particle[var].setValue(problem_.getUpperLimit(var));                    
//// 		        	speed_[i][var] = speed_[i][var] * -1.0;    
//// 		        }                                             
 		    }
 		}
 	} // computeNewPositions
        
   
 	/**
 	 * Apply a mutation operator to all particles in the swarm
 	 * @throws JMException 
 	 */
  private void mopsoMutation(int actualIteration, int totalIterations) throws JMException{       
    //There are three groups of particles_, the ones that are mutated with
    //a non-uniform mutation operator, the ones that are mutated with a 
    //uniform mutation and the one that no are mutated
    nonUniformMutation_.setParameter("currentIteration",actualIteration);

    for (int i = 0; i < particles_.size();i++)    {
	    Double[] realPart = ((ArrayReal)particles_.get(i).getDecisionVariables()[0]).getArray();	
	    int[] intPart = ((ArrayInt)particles_.get(i).getDecisionVariables()[1]).getArray();
	    Double[] backupValuesReal = realPart.clone();
	    int[] backupValuesInt = intPart.clone();
    	
      if (i % 3 == 0) { //particles_ mutated with a non-uniform mutation
        nonUniformMutation_.execute(particles_.get(i));
        Double[] newRealPart = ((ArrayReal)particles_.get(i).getDecisionVariables()[0]).getArray();	
	    int[] newIntPart = ((ArrayInt)particles_.get(i).getDecisionVariables()[1]).getArray();
	    
    	if(CheckForMutation(backupValuesReal, newRealPart) || CheckForMutation(backupValuesInt, newIntPart)) {
    		System.out.println("!!!NONUNIFORM!!!!!!!!!!!!!!!///////////////MUTATION//////////!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    		System.out.println("REAL: Before: " + Arrays.toString(backupValuesReal) + "\n REAL: After: " + Arrays.toString(newRealPart));
    		System.out.println("INT: Before: " + Arrays.toString(backupValuesInt) + "\n INT: After: " + Arrays.toString(newIntPart) + "\n");
    	}
        
      } else if (i % 3 == 1) { //particles_ mutated with a uniform mutation operator
    	uniformMutation_.execute(particles_.get(i));
        Double[] newRealPart = ((ArrayReal) particles_.get(i).getDecisionVariables()[0]).array_;
	    int[] newIntPart = ((ArrayInt) particles_.get(i).getDecisionVariables()[1]).array_;
	    
    	if(CheckForMutation(backupValuesReal, newRealPart) || CheckForMutation(backupValuesInt, newIntPart)) {
    		System.out.println("!!!UNIFORM!!!!!!!!!!!!!!!///////////////MUTATION//////////!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    		System.out.println("REAL: Before: " + Arrays.toString(backupValuesReal) + "\nREAL: After: " + Arrays.toString(newRealPart));
    		System.out.println("INT: Before: " + Arrays.toString(backupValuesInt) + "\nINT: After: " + Arrays.toString(newIntPart) + "\n");
    	}
      } else //particles_ without mutation
          ;      }
  } // mopsoMutation
   
  
  public boolean CheckForMutation(Double[] Array1, Double[] Array2) { //For Double[] arrays
	  if(Array1.length != Array2.length) {
		  System.out.println("Arrays have different Lengths");
		  return false;
	  }
	  for(int i = 0; i<Array1.length; i++ ) {
		  if(!(Array1[i].equals(Array2[i]))) {
			  System.out.println("\nReal mutation at position = " + i + ": Where " + Array1[i] + " does not equal " + Array2[i]);
			  return true;
		  }
		  else {
		  }
	  }
	  return false;
  }
  
  public boolean CheckForMutation(int[] Array1, int[] Array2) { //For int[] arrays
	  if(Array1.length != Array2.length) {
		  System.out.println("Arrays have different Lengths");
		  return false;
	  }
	  for(int i = 0; i<Array1.length; i++ ) {
		  if(!(Array1[i] == Array2[i])) {
			  System.out.println("\nInt mutation at position = " + i + ": Where " + Array1[i] + " does not equal " + Array2[i]);
			  return true;
		  }
	  }
	  return false;
  }
    

  
  
  
  /**   
 	 * Runs of the pOMOPSO algorithm.
 	 * @return a <code>SolutionSet</code> that is a set of non dominated solutions
 	 * as a result of the algorithm execution  
 	 * @throws JMException 
 	 */  
 	public SolutionSet execute() throws JMException, ClassNotFoundException {
		Solution newSolution;			//NEWLINE CREATED: (RECORD OF LAST EDIT)
 		int numberOfThreads ; 											//NEWLINE CREATED: (RECORD OF LAST EDIT)
		initParams();

		parallelEvaluator_.startEvaluator(problem_) ; 					//NEWLINE CREATED: (RECORD OF LAST EDIT)

		//->Step 1 (and 3) Create the initial population and evaluate
		for (int i = 0; i < particlesSize_; i++){
			newSolution = new Solution(problem_);
			parallelEvaluator_.addSolutionForEvaluation(newSolution) ;   	           
		}
		List<Solution> solutionList = parallelEvaluator_.parallelEvaluation() ;		//NEWLINE CREATED: (RECORD OF LAST EDIT)
		for (Solution solution : solutionList) {						// For all (10 default) solutions, add to our solution list.
			particles_.add(solution) ;									//  This line should add "population" number of solutions which is also capacity. 
//			iteration_ ++ ;												//NEWLINE CREATED: (RECORD OF LAST EDIT)
		}	
	
        
		   //-> Step2. Initialize the speed_ of each particle to 0
	    for (int i = 0; i < particlesSize_; i++) {
	      for (int j = 0; j < problem_.getNumberOfVariables(); j++) {
	        speed_[i][j] = 0.0;
	      }
	    }
	    
	        
	    // Step4 and 5. Initialise the leaders
	    for (int i = 0; i < particles_.size(); i++){
	      Solution particle = new Solution(particles_.get(i));            
	      if (leaders_.add(particle)){ //Crowding Archive (utilises dominance so reduced set)
	        eArchive_.add(new Solution(particle)); 
	      }
	    }
	                
	    //-> Step 6. Initialice the memory of each particle
	    for (int i = 0; i < particles_.size(); i++){
	      Solution particle = new Solution(particles_.get(i));           
	      best_[i] = particle;
	    }
	        
	    //Crowding the leaders_
	    distance_.crowdingDistanceAssignment(leaders_,problem_.getNumberOfObjectives());        


		//-> Step 7. Iterations ..        
		while (iteration_ < maxIterations_){
			System.out.println("Evaluations:	" + iteration_);
			//Compute the speed_        
			computeSpeed();
            
			//Compute the new positions for the particles_            
			computeNewPositions();
			
			//Mutate the particles_          
			mopsoMutation(iteration_,maxIterations_);                       
            
			//Evaluate the new particles_ in new positions
			for (int i = 0; i < particles_.size(); i++){
				newSolution = particles_.get(i);
//		        problem_.evaluate(particle);                
//		        problem_.evaluateConstraints(particle);   
				parallelEvaluator_.addSolutionForEvaluation(newSolution) ; 
			}
			
			List<Solution> solutions = parallelEvaluator_.parallelEvaluation() ;		//NEWLINE CREATED: (RECORD OF LAST EDIT)
			for (Solution solution : solutions) {						//NEWLINE CREATED: (RECORD OF LAST EDIT)
				particles_.add(solution) ;									//NEWLINE CREATED: (RECORD OF LAST EDIT)
//				iteration_ ++ ;												//NEWLINE CREATED: (RECORD OF LAST EDIT)
			}	
			
			
            
			//Actualize the archive          
			for (int i = 0; i < particles_.size(); i++){
				Solution particle = new Solution(particles_.get(i));                
				if (leaders_.add(particle)){
					eArchive_.add(new Solution(particle));
				}                
			}
            
			//Actualize the memory of this particle
			for (int i = 0; i < particles_.size();i++){
				int flag = dominance_.compare(particles_.get(i),best_[i]);
				if (flag != 1) { // the new particle is best_ than the older remeber        
					Solution particle = new Solution(particles_.get(i));                    
					//this.best_.reemplace(i,particle);
					best_[i] = particle;
				}
			}       
            
			//Crowding the leaders_
			distance_.crowdingDistanceAssignment(leaders_, problem_.getNumberOfObjectives());            
			iteration_++;
		}
        
		return this.leaders_;
		//return eArchive_;
 	} // execute
    
 	/** 
 	 * Gets the leaders of the pOMOPSO algorithm
 	 */
 	public SolutionSet getLeader(){
 		return leaders_;
 	}  // getLeader 
} // pOMOPSO
