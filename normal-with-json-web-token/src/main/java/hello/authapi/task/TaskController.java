package hello.authapi.task;

import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tasks")
@Slf4j
public class TaskController {

	private TaskRepository taskRepository;

	public TaskController(TaskRepository taskRepository) {
		this.taskRepository = taskRepository;
	}

	@PostMapping
	public void addTask(@RequestBody Task task) {
		
		log.info("add task:" + task);
		taskRepository.save(task);
	}

	@GetMapping
	public List<Task> getTasks() {
		
		log.info("get tasks:" + taskRepository.findAll().size());
		return taskRepository.findAll();
	}

	@PutMapping("/{id}")
	public void editTask(@PathVariable long id, @RequestBody Task task) {
		
		Optional<Task> optionalTask = taskRepository.findById(id);
		
		Task existingTask = optionalTask.get();
		Assert.notNull(existingTask, "Task not found");
		existingTask.setDescription(task.getDescription());
		taskRepository.save(existingTask);
	}

	@DeleteMapping("/{id}")
	public void deleteTask(@PathVariable long id) {
		
		taskRepository.deleteById(id);
		
	}
}
