The program takes standard input.
Before executing: type  javac Main.java &java Main.java to compile and run.
The format of the input is as follows:

first int = # of Dices
second int = # of Sides
third int = Lower Target Value
fourth int = Upper Target Value 
fifth int = number of Games 
last double = hyper parameter M 

Example:
2 3 6 7 100000 100.0

corresponds to 
2 = # of Dices
3 = # of Sides
6= Lower Target Value
7= Upper Target Value 
100000 = number of Games 
100.0 = hyper parameter M 

It turns out my output is slightly different from the example output. The first row and the column in my output is flipped. I was not able to figure out why, but the rest seems fine (especially for those "meaningful results").