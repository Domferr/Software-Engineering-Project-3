package ie.ucdconnect.sep.gui;

import ie.ucdconnect.sep.Project;
import ie.ucdconnect.sep.Solution;
import ie.ucdconnect.sep.SolutionGenerationStrategy;
import ie.ucdconnect.sep.Student;
import javafx.concurrent.Task;

import java.util.List;

public class GeneratorTask extends Task<Solution> {

	private SolutionGenerationStrategy generationStrategy;
	private List<Project> projects;
	private List<Student> students;

	public GeneratorTask(SolutionGenerationStrategy generationStrategy, List<Project> projects, List<Student> students) {
		this.generationStrategy = generationStrategy;
		this.projects = projects;
		this.students = students;
	}

	@Override
	protected Solution call() throws Exception {
		return generationStrategy.generate(projects, students);
	}
}
