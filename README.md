# Software-Engineering-Project-3
Team JF
Martynas Jagutis 17424866
Domenico Ferraro 19203549
Alex Ng 17409754

TASK 5
We added the method CreateByMutating in the SolutionFactory class. This method creates a solution by mutating a given solution
passed as argument. We decided to use the Strategy design pattern for the implementation of the SimulatedAnnealing and 
Genetic algorithms. Each algorithm represents a strategy for the generation of a solution. The interface SolutionGeneratorStrategy
(which has the generate method) is implemented by the SimulatedAnnealing class. Its generate method performs the SimulatedAnnealing
algorithm. The class GeneticAlgorithm implements the same interface but its generate method performs the Genetic algorithm.
The two classes are stateless. With this approach, the algorithm is not implemented directly but it's decided during runtime.
For the Simulated Annealing algorithm the cooling rate and the minimum temperature as constants. We calculate the maximum
energy delta in 100 random solution. The temperature is higher or lower than this value, based on a constant.
For each iteration we create a new solution by mutating the solution of the previous iteration. If this new solution
has a lower energy than the previous solution we accept the changes. If it is worse we accept the changes according to 
the Boltzman Distribution probability which is defined by Math.exp(-deltaEnergy/temperature). Thus we generate a random number
between 0 and 1 to see whether or not it is bigger and less than this value and accept or decline the worse energy according to this.
The temperature is decreased exponentially: the higher the temperature is, the more it is decreased. The temperature at time n
is equal to startingTemperature * (Math.pow(1-COOLING_RATE, Math.log(n))). The best solution is returned at the end of the method.
The unit tests check whether the energy of a simulated annealing solution is lower than a randomly generated solution.
As an extra feature we implemented the genetic algorithm, to find the best fitness of the solution.

TASK 4
We added a gpa field to the student which acts as a soft constraint for the evaluation of the energy and fitness functions. It is
generated as a random value between 0 and 4.2. In the solution class we added an evaluate method which calculates the
fitness and energy of the solution. We assign constraint violated penalties if the solution happens to break these constraints.
The fitness is calculated by (10 - i) + (10 - i) * gpaWeight where i is the preference a student got where 0 is the first preference 
and gpaWeight is the gpa of a given student times the gpaImportance which is the importance given to the gpa constraint. The energy
is calculated by i + i + gpaWeight. The higher the fitness the better the solution and contrarily the higher the energy the worse the solution.
The evaluate method can be high consuming in terms of computation so we decided to left the opportunity to instantiate a Solution object
with or without calling the evaluate function. To do so, we implemented a solution factory. We also implemented the SolutionAcceptor class
which permits to compare two solutions or an entire list of solutions in terms of energy and fitness. 
As an extra feature of this assignment we implements the Simulated Annealing algorithm. The resulting fitness and energy 
values are printed to the console. We implemented an interface called Default which contains the gpa importance value 
so we do not have to redefine it at the top of multiple classes.

TASK 3
To achieve the random generation of solutions we created two classes called SolutionGeneration and Solution with unit tests
to test both of these classes. The solution class creates a map of project to student so only one project is mapped to one 
student. This class also contains methods to modify this map safely. This is done through the create random method which takes the list of projects and students as its parameters.
This method choose a random student from the list and a random preference from that student and maps it if the project
has not yet been taken by another student. This is checked by the safe map method which makes sure that the project 
has not been taken. It then goes to the next preference if it has been taken and loops back around until it finds one
which has not been taken. If all projects in the preference list have been take a random project is assigned to that student.
The solution generator a solution for all the test cases we generated in previous assignments. The results are saved to .csv files named solutionFor<no_of_students>Students.csv. 

TASK 2
We have created 3 classes to store the data called Project, StaffMember and Student. Project contains the title,
the staff member that proposed it and the stream it targets. Student contains general student info, their stream (focus),
and their project preferences. StaffMember consists of their name, research activities, research areas, their proposed projects
and whether or not they are special focus. We created the respective unit tests for each of these class aiming for high 
coverage. Each of the classes consists of fromCSV and fromCSVRow methods which reads data from disk and creates their objects.
These methods are tested in the unit test classes provided. Project offers a validate method which makes sures that the supervisors
that propose these projects are consistent with their focus.


TASK 1
How Test Sets are generated:
Students are generated from a list of names. They are assigned a random unique student number and a focus which has 
a 60% chance of being CS and 40% chance of being DS. They are then assigned 10 difference preferences of projects
according to a Gaussian distribution i.e. some projects are more popular than others.
Projects are generated by staff members where each staff proposes an average of 3 projects. The number of staff members
depends on the number of students they are proposing the projects for i.e. 2 students : 1 staff member. The students choose
the projects according to the staff members focus area. 
