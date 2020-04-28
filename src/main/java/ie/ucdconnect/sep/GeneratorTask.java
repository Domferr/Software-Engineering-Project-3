package ie.ucdconnect.sep;

import ie.ucdconnect.sep.controllers.Controller;
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
