# Gurobi - Java Implementation of the ILP model for the median string proposed by:

HAYASHIDA, Morihiro y KOYANO, Hitoshi. 
Integer Linear Programming Approach to Median and Center Strings for a Probability Distribution on a Set of Strings.
Portugal: SCITEPRESS - Science and Technology Publications, Lda. 2016.p. 35--41.


#### Dependencies:
    commons-cli-1.4.jar
    gurobi.jar (Gurobi 810)

### Run example:
    java -Xmx4G -cp ./dist/*:./lib/* optimization.ILP_StrMean -in bench1_6_2_6.txt -om bench1_6_2_6.lp -ops bench1_6_2_6.out -opm -verbose


#### Command line options:

in : Set the input file (in challenge format). 

    Syntax: -in input_file

opm : Optimize the model. 

    Syntax: -opm

om : Output the model. 

    Syntax: -out model_output_file_path

ops : Output partial solutions in a file with format Obj:11.0-time:0.9537200927734375-sol:1 2 2 1 2 1 
      
	Requires opm.
	Syntax: -ops partial_solutions_file_path

verbose : Output additional information. 
        
    Syntax: -verbose