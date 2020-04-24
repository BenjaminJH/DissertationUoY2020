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
import jmetal.encodings.solutionType.ArrayIntSolutionType;
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
import evochecker.genetic.jmetal.encoding.ArrayRealIntSolutionType;
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
	private int maxEvaluations_;
  
	/**
	 * Stores the current number of iteration_
	 */
	private int evaluations_;
  
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
    maxEvaluations_ = ((Integer)getInputParameter("maxIterations")).intValue();

    
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
			if((particles_.get(i).getType().getClass()).equals(ArrayRealIntSolutionType.class)){
 				int RealPartLength = ((ArrayReal)particle[0]).getLength();
 				int IntPartLength = ((ArrayInt)particle[1]).getLength();

				
				
//	 		    System.out.println("Speed Array Before: " + Arrays.toString(speed_[i]));
 				
 				for (int realVar = 0; realVar < RealPartLength; realVar++){
 					ArrayReal myBestVar = (ArrayReal) bestParticle[0];
 					ArrayReal myBestGlobal = (ArrayReal) bestGlobal[0];
 					ArrayReal myParticleVar = (ArrayReal) particle[0];
 	 				speed_[i][realVar] = W  * speed_[i][realVar] +
 	 						C1 * r1 * (myBestVar.getValue(realVar) -       
 	 								myParticleVar.getValue(realVar)) +
 	 						C2 * r2 * (myBestGlobal.getValue(realVar) - 
 	 								myParticleVar.getValue(realVar));
 				}
 				for (int intVar = 0; intVar < IntPartLength; intVar++){
 					ArrayInt myBestVar = (ArrayInt) bestParticle[1];
 					ArrayInt myBestGlobal = (ArrayInt) bestGlobal[1];
 					ArrayInt myParticleVar = (ArrayInt) particle[1];
 	 				speed_[i][intVar+RealPartLength] = W  * speed_[i][intVar+RealPartLength] +
 	 						C1 * r1 * (myBestVar.getValue(intVar) -       
 	 								myParticleVar.getValue(intVar)) +
 	 						C2 * r2 * (myBestGlobal.getValue(intVar) - 
 	 								myParticleVar.getValue(intVar));
 				}
// 				System.out.println("Speed Array After: " + Arrays.toString(speed_[i]) + "\n");
				
				
			} else if((particles_.get(i).getType().getClass()).equals(ArrayRealSolutionType.class)) {
 				int RealPartLength = ((ArrayReal)particle[0]).getLength();
 				
 				for (int realVar = 0; realVar < RealPartLength; realVar++){
 					ArrayReal myBestVar = (ArrayReal) bestParticle[0];
 					ArrayReal myBestGlobal = (ArrayReal) bestGlobal[0];
 					ArrayReal myParticleVar = (ArrayReal) particle[0];
 	 				speed_[i][realVar] = W  * speed_[i][realVar] +
 	 						C1 * r1 * (myBestVar.getValue(realVar) -       
 	 								myParticleVar.getValue(realVar)) +
 	 						C2 * r2 * (myBestGlobal.getValue(realVar) - 
 	 								myParticleVar.getValue(realVar));
 				}
			}
			
			
			else {
				for (int var = 0; var < particle.length; var++){                                     
				//Computing the velocity of this particle
				speed_[i][var] = W  * speed_[i][var] +
				             C1 * r1 * (bestParticle[var].getValue() - 
				                        particle[var].getValue()) +
				             C2 * r2 * (bestGlobal[var].getValue() - 
				                        particle[var].getValue());
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
 			if((particles_.get(i).getType().getClass()).equals(ArrayRealIntSolutionType.class)){
 				int RealPartLength = ((ArrayReal)particle[0]).getLength();
 				int IntPartLength = ((ArrayInt)particle[1]).getLength();
 				

				ArrayReal myRealParticle = (ArrayReal) particle[0];		
				ArrayInt myIntParticle = (ArrayInt) particle[1];
				
	 		    
 	 		    for (int realVar = 0; realVar < RealPartLength; realVar++){


// 	 		    	System.out.println("Speed Array = " + Arrays.toString(speed_[i]));
 					myRealParticle.setValue(realVar, myRealParticle.getValue(realVar)+ speed_[i][realVar]);
 					
 					
	 		        if (myRealParticle.getValue(realVar) < problem_.getLowerLimit(realVar)){
	 		        	myRealParticle.setValue(realVar, problem_.getLowerLimit(realVar));                    
	 		        	speed_[i][realVar] = speed_[i][realVar] * -1.0;    
	 		        }
	 		        if (myRealParticle.getValue(realVar) > problem_.getUpperLimit(realVar)){
	 		        	myRealParticle.setValue(realVar, problem_.getUpperLimit(realVar));                    
	 		        	speed_[i][realVar] = speed_[i][realVar] * -1.0;
	 		        }

 	 		    }

 				for (int intVar = 0; intVar < IntPartLength; intVar++){


					myIntParticle.setValue(intVar, (int) Math.round(myIntParticle.getValue(intVar)+ speed_[i][intVar]));
					
	 		        if (myIntParticle.getValue(intVar) < problem_.getLowerLimit(intVar+RealPartLength)){
	 		        	myIntParticle.setValue(intVar, (int) Math.round(problem_.getLowerLimit(intVar+RealPartLength)));  
	 		        	speed_[i][intVar+RealPartLength] = speed_[i][intVar+RealPartLength] * -1.0;    
	 		        }
	 		        if (myIntParticle.getValue(intVar) > problem_.getUpperLimit(intVar+RealPartLength)){
	 		        	myIntParticle.setValue(intVar, (int) Math.round(problem_.getUpperLimit(intVar+RealPartLength)));                    
	 		        	speed_[i][intVar+RealPartLength] = speed_[i][intVar+RealPartLength] * -1.0;    
	 		        }
 				}
// 		    	System.out.println("myRealParticle After: " + Arrays.toString(myRealParticle.getArray()));
// 		    	System.out.println("myIntParticle After: " + Arrays.toString(myIntParticle.getArray()) + "\n");
 				
 		} else if((particles_.get(i).getType().getClass()).equals(ArrayRealSolutionType.class)){
			int RealPartLength = ((ArrayReal)particle[0]).getLength();
				
			ArrayReal myRealParticle = (ArrayReal) particle[0];
			
// 		    System.out.println("myRealParticle Before: " + Arrays.toString(myRealParticle.getArray().clone()));
//			System.out.println("myIntParticle Before: " + Arrays.toString(myIntParticle.getArray().clone()));
 		    
 		    for (int realVar = 0; realVar < RealPartLength; realVar++){
				myRealParticle.setValue(realVar, myRealParticle.getValue(realVar)+ speed_[i][realVar]);
			
 		        if (myRealParticle.getValue(realVar) < problem_.getLowerLimit(realVar)){
 		        	myRealParticle.setValue(realVar, problem_.getLowerLimit(realVar));                    
 		        	speed_[i][realVar] = speed_[i][realVar] * -1.0;    
 		        }
 		        if (myRealParticle.getValue(realVar) > problem_.getUpperLimit(realVar)){
 		        	myRealParticle.setValue(realVar, problem_.getUpperLimit(realVar));                    
 		        	speed_[i][realVar] = speed_[i][realVar] * -1.0;
 		        }

 		    }
 			
 		}else { //OMOPSO default
 			for(int var = 0; var < particle.length; var++) {
 		        particle[var].setValue(particle[var].getValue()+ speed_[i][var]);
 		        if (particle[var].getValue() < problem_.getLowerLimit(var)){
 		        	particle[var].setValue(problem_.getLowerLimit(var));                    
 		        	speed_[i][var] = speed_[i][var] * -1.0;    
 		        }
 		        if (particle[var].getValue() > problem_.getUpperLimit(var)){
 		        	particle[var].setValue(problem_.getUpperLimit(var));                    
 		        	speed_[i][var] = speed_[i][var] * -1.0;    
 		        } 
 			}

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
//	    Double[] realPart = ((ArrayReal)particles_.get(i).getDecisionVariables()[0]).getArray();	
//	    int[] intPart = ((ArrayInt)particles_.get(i).getDecisionVariables()[1]).getArray();
//	    Double[] backupValuesReal = realPart.clone();
//	    int[] backupValuesInt = intPart.clone();
    	
      if (i % 3 == 0) { //particles_ mutated with a non-uniform mutation
        nonUniformMutation_.execute(particles_.get(i));
//        Double[] newRealPart = ((ArrayReal)particles_.get(i).getDecisionVariables()[0]).getArray();	
//	    int[] newIntPart = ((ArrayInt)particles_.get(i).getDecisionVariables()[1]).getArray();
//	    
//    	if(CheckForMutation(backupValuesReal, newRealPart) || CheckForMutation(backupValuesInt, newIntPart)) {
//    		System.out.println("!!!NONUNIFORM!!!!!!!!!!!!!!!///////////////MUTATION//////////!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//    		System.out.println("REAL: Before: " + Arrays.toString(backupValuesReal) + "\n REAL: After: " + Arrays.toString(newRealPart));
//    		System.out.println("INT: Before: " + Arrays.toString(backupValuesInt) + "\n INT: After: " + Arrays.toString(newIntPart) + "\n");
//    	}
        
      } else if (i % 3 == 1) { //particles_ mutated with a uniform mutation operator
    	uniformMutation_.execute(particles_.get(i));
//        Double[] newRealPart = ((ArrayReal) particles_.get(i).getDecisionVariables()[0]).array_;
//	    int[] newIntPart = ((ArrayInt) particles_.get(i).getDecisionVariables()[1]).array_;
//	    
//    	if(CheckForMutation(backupValuesReal, newRealPart) || CheckForMutation(backupValuesInt, newIntPart)) {
//    		System.out.println("!!!UNIFORM!!!!!!!!!!!!!!!///////////////MUTATION//////////!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//    		System.out.println("REAL: Before: " + Arrays.toString(backupValuesReal) + "\nREAL: After: " + Arrays.toString(newRealPart));
//    		System.out.println("INT: Before: " + Arrays.toString(backupValuesInt) + "\nINT: After: " + Arrays.toString(newIntPart) + "\n");
//    	}
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
			evaluations_ ++ ;												//NEWLINE CREATED: (RECORD OF LAST EDIT)
		}	
	
        
		   //-> Step2. Initialise the speed_ of each particle to 0
	    for (int i = 0; i < particlesSize_; i++) {
	      for (int j = 0; j < problem_.getNumberOfVariables(); j++) {
	        speed_[i][j] = 0.0;
	      }
	    }
	    
	        
	    // Step4 and 5. Initialise the leaders (This should be fine because of dominance)
	    for (int i = 0; i < particles_.size(); i++){
	      Solution particle = new Solution(particles_.get(i)); //add initial 10 new particles as new leaders and archive    
	      if (leaders_.add(particle)){ //Crowding Archive (utilises dominance so reduced set)
	        eArchive_.add(new Solution(particle)); 
	      }//TODO find out if we should add 10 evaluations here... (probably not because they're our initial particles)
	    }
	    
	    
	    //-> Step 6. Initialise the memory of each particle
	    for (int i = 0; i < particles_.size(); i++){
	      Solution particle = new Solution(particles_.get(i));           
	      best_[i] = particle;	//Record of our populationSize BEST particles (still from initial population for now)
	    }
	        
	    //Crowding the leaders_
	    distance_.crowdingDistanceAssignment(leaders_,problem_.getNumberOfObjectives());   //STOPPED HERE (temp)     


		//-> Step 7. Iterations ..        
		while (evaluations_ < maxEvaluations_){
			if((evaluations_ % 10) == 0) {
				System.out.println("Evaluations:	" + evaluations_);
//				System.out.println("List of particles: ");
				
			}
			
			//Compute the speed_       
//			System.out.println("Particle Real before speed, pod, mopso" + Arrays.toString(((ArrayReal)particles_.get(0).getDecisionVariables()[0]).getArray().clone()));
//			System.out.println("Particle Objectives = " + particles_.get(0).getObjective(0) + " " + particles_.get(0).getObjective(1) + " " + particles_.get(0).getObjective(2));
//			System.out.println("Particle Int before speed, pod, mopso" + Arrays.toString(((ArrayInt)particles_.get(0).getDecisionVariables()[1]).getArray().clone()));
			computeSpeed();
            
			//Compute the new positions for the particles_            
			computeNewPositions();
			
			//Mutate the particles_          
			mopsoMutation(evaluations_,maxEvaluations_);                       
//			System.out.println("Particle Real after speed, pod, mopso" + Arrays.toString(((ArrayReal)particles_.get(0).getDecisionVariables()[0]).getArray().clone()));
			
			//			System.out.println("Particle Int after speed, pod, mopso" + Arrays.toString(((ArrayInt)particles_.get(0).getDecisionVariables()[1]).getArray().clone()));
			
			//Evaluate the new particles_ in new positions
			for (int i = 0; i < particles_.size(); i++){ //Error maybe in here
				newSolution = particles_.get(i);
//		        problem_.evaluate(particle);                
//		        problem_.evaluateConstraints(particle);   
				parallelEvaluator_.addSolutionForEvaluation(newSolution) ; 
			}
			particles_.clear();//Comment out this single line to see array add error I mentioned
			/*
			 * This section of code below is causing the error Simos
			 */
			List<Solution> solutions = parallelEvaluator_.parallelEvaluation() ; //This needs to be fixed to get Correct Objective Fitness results
			for (Solution solution : solutions) {
				particles_.add(solution) ; //I think this should be cleared since we're only after the "leaders", once added to leaders this means nothing on subsequent runs
				evaluations_ ++ ;
			}	
			
//			System.out.println("Particle Objectives = " + particles_.get(0).getObjective(0) + " " +  particles_.get(0).getObjective(1) + " " + particles_.get(0).getObjective(2));
            
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
