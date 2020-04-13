package ie.ucdconnect.sep;

import com.google.common.collect.ImmutableMultimap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/** This class is able to generate a random solution */
public class RandomGeneration implements SolutionGenerationStrategy {
    @Override
    public Solution generate(List<Project> projects, List<Student> students, double GPA_IMPORTANCE) {
        ImmutableMultimap.Builder<Project, Student> mapBuilder = ImmutableMultimap.builder();

        List<Student> studentsCopy = new ArrayList<>(students);
        Collections.shuffle(studentsCopy);

        for (Student student : studentsCopy) {
            int randomIndex = new Random().nextInt(projects.size());
            mapBuilder.put(projects.get(randomIndex), student);
        }

        return Solution.SolutionFactory.createAndEvaluate(mapBuilder.build(), GPA_IMPORTANCE);
    }
}
