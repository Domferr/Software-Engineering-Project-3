# Software-Engineering-Project-3
Team JF
Martynas Jagutis 17424866
Domenico Ferraro 19203549
Alex Ng 17409754

TASK 4
We added a gpa field to the student which acts as a soft constraint for the evaluation of the energy and fitness functions. It is
generated as a random value between 0 and 4.2. In the solution class we added an evaluate method this function calculates the
fitness and energy for a given solution. We assign constraint violated penalties if the solution happens to break these constraints.
The fitness is calculated by (10 - i) + (10 - i) * gpaWeight where i is the preference a student got where 0 is the first preference 
and gpaWeight is the gpa of a given student times the gpaImportance the user decides to give to the gpa constraint. The energy
is calculated by i + i + gpaWeight. The higher the fitness the better the solution and contrarily the higher the energy the worse the solution.
We decided to create a solution factory as we figures that we will not need to always call the evaluate method when instantiating solution. 
As an extra feature of this assignment we implements the simulated annealing algorithm to constantly make changes to the solution
until it finds the best solution. The resulting fitness and energy values are printed to the console.
We implemented an interface called default which contains the gpa importance value so we do not have to redefine it at the top of multiple classes.

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
