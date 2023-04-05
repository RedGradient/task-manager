package hexlet.code.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.LabelDto;
import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.dto.UserDto;
import hexlet.code.models.Label;
import hexlet.code.models.Task;
import hexlet.code.repositories.TaskRepository;
import hexlet.code.repositories.TaskStatusRepository;
import hexlet.code.repositories.UserRepository;
import hexlet.code.service.LabelService;
import hexlet.code.service.TaskService;
import hexlet.code.service.TaskStatusService;
import hexlet.code.service.UserService;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.List;

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.controller.TaskController.ID;
import static hexlet.code.utils.TestUtils.TEST_USERNAME;
import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.fromJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)
public class TaskControllerIT {

    private static final String TASKS_CONTROLLER = "/api/tasks";
    private static final String EXECUTOR_USERNAME = "executor@mail.com";


    @Autowired
    private TaskController controller;
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TaskStatusService statusService;
    @Autowired
    private TaskStatusRepository statusRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LabelService labelService;
    @Autowired
    private TestUtils utils;

    @BeforeAll
    public void beforeAll() throws Exception {
        utils.regDefaultUser();
        userService.createNewUser(new UserDto(
                EXECUTOR_USERNAME,
                "Good",
                "Executor",
                "12345"
        ));
    }
    @BeforeEach
    public void beforeEach() throws Exception {
    }

    @AfterEach
    public void clear() {
        taskRepository.deleteAll();
        statusRepository.deleteAll();
    }

    @Test
    public void contextLoads() {
        assertNotNull(controller);
    }

    @Test
    public void createTask() throws Exception {
        var executor = utils.getUserByEmail(EXECUTOR_USERNAME);
        var taskStatus = statusService.createNewStatus(new TaskStatusDto("New"));
        var label = labelService.createLabel(new LabelDto("Feature"));
        var labels = new HashSet<Long>();
        labels.add(label.getId());
        var taskDto = new TaskDto(
                "Task",
                "Description",
                taskStatus.getId(),
                labels,
                executor.getId()
        );

        var request = post(TASKS_CONTROLLER)
                .content(asJson(taskDto))
                .contentType(APPLICATION_JSON);
        var response = utils.perform(request, TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        final Task task = fromJson(
                response.getContentAsString(), new TypeReference<>() { }
        );

        assertTrue(taskRepository.findById(task.getId()).isPresent());
    }

    @Test
    public void getTasks() throws Exception {
        // create some tasks
        var author = utils.getUserByEmail(TEST_USERNAME);
        var executor = utils.getUserByEmail(EXECUTOR_USERNAME);
        var taskStatus = statusService.createNewStatus(new TaskStatusDto("New"));

        var labels = new HashSet<Label>();
        labels.add(labelService.createLabel(new LabelDto("Feature")));

        final var newTaskCount = 3;

        taskRepository.save(new Task("Task 1", "Descr", taskStatus, labels, author, executor));
        taskRepository.save(new Task("Task 2", "Descr", taskStatus, labels, author, executor));
        taskRepository.save(new Task("Task 3", "Descr", taskStatus, labels, author, executor));
        // -------

        var response = utils.perform(get(TASKS_CONTROLLER))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        final List<Task> tasks = fromJson(
                response.getContentAsString(), new TypeReference<>() { }
        );

        assertEquals(newTaskCount, tasks.size());
    }

    @Test
    public void getTaskById() throws Exception {
        var author = utils.getUserByEmail(TEST_USERNAME);
        var executor = utils.getUserByEmail(EXECUTOR_USERNAME);
        var taskStatus = statusService.createNewStatus(new TaskStatusDto("New"));
        var labels = new HashSet<Label>();
        labels.add(labelService.createLabel(new LabelDto("Feature")));
        var task = new Task(
                "Task",
                "Description",
                taskStatus,
                labels,
                author,
                executor
        );
        taskRepository.save(task);

        var response = utils.perform(get(TASKS_CONTROLLER + ID, task.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final Task recievedTask = fromJson(
                response.getContentAsString(), new TypeReference<>() { }
        );

        assertEquals(task.getName(), recievedTask.getName());
    }

    @Test
    public void createTaskFails() throws Exception {
        var executor = utils.getUserByEmail(EXECUTOR_USERNAME);
        var taskStatus = statusService.createNewStatus(new TaskStatusDto("New"));
        var labels = new HashSet<Long>();
        labels.add(labelService.createLabel(new LabelDto("Feature")).getId());
        var taskDto = new TaskDto(
                "Task",
                "Description",
                taskStatus.getId(),
                labels,
                executor.getId()
        );

        var request = post(TASKS_CONTROLLER)
                .content(asJson(taskDto))
                .contentType(APPLICATION_JSON);
        utils.perform(request)
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse();

    }

    @Test
    public void updateTask() throws Exception {
        var author = utils.getUserByEmail(TEST_USERNAME);
        var executor = utils.getUserByEmail(EXECUTOR_USERNAME);
        var taskStatus = statusService.createNewStatus(new TaskStatusDto("New"));
        var taskName = "Task";
        var oldDescr = "Old Description";
        var labels = new HashSet<Label>();
        labels.add(labelService.createLabel(new LabelDto("Feature")));
        var task = new Task(
                taskName,
                oldDescr,
                taskStatus,
                labels,
                author,
                executor
        );
        taskRepository.save(task);

        assertEquals(taskRepository.findById(task.getId()).get().getDescription(), oldDescr);

        var newDescr = "Updated description";
        var labelIds = new HashSet<Long>();
        labelIds.add(labelService.createLabel(new LabelDto("Feature")).getId());
        var newTaskDto = new TaskDto(
                taskName,
                newDescr,
                taskStatus.getId(),
                labelIds,
                executor.getId()
        );
        var request = put(TASKS_CONTROLLER + ID, task.getId())
                .content(asJson(newTaskDto))
                .contentType(APPLICATION_JSON);

        utils.perform(request, TEST_USERNAME).andExpect(status().isOk());

        assertEquals(taskRepository.findById(task.getId()).get().getDescription(), newDescr);
    }

    @Test
    public void updateTaskFails() throws Exception {
        var author = utils.getUserByEmail(TEST_USERNAME);
        var executor = utils.getUserByEmail(EXECUTOR_USERNAME);
        var taskStatus = statusService.createNewStatus(new TaskStatusDto("New"));
        var taskName = "Task";
        var oldDescr = "Old Description";
        var labels = new HashSet<Label>();
        labels.add(labelService.createLabel(new LabelDto("Feature")));
        var task = new Task(
                taskName,
                oldDescr,
                taskStatus,
                labels,
                author,
                executor
        );
        taskRepository.save(task);

        assertEquals(taskRepository.findById(task.getId()).get().getDescription(), oldDescr);

        var newDescr = "Updated description";
        var labelIds = new HashSet<Long>();
        labelIds.add(labelService.createLabel(new LabelDto("Feature")).getId());
        var newTaskDto = new TaskDto(
                taskName,
                newDescr,
                taskStatus.getId(),
                labelIds,
                executor.getId()
        );
        var request = put(TASKS_CONTROLLER + ID, task.getId())
                .content(asJson(newTaskDto))
                .contentType(APPLICATION_JSON);

        utils.perform(request).andExpect(status().isForbidden());

        assertNotEquals(taskRepository.findById(task.getId()).get().getDescription(), newDescr);
    }

    @Test
    public void deleteTask() throws Exception {
        var author = utils.getUserByEmail(TEST_USERNAME);
        var executor = utils.getUserByEmail(EXECUTOR_USERNAME);
        var taskStatus = statusService.createNewStatus(new TaskStatusDto("New"));
        var labels = new HashSet<Label>();
        labels.add(labelService.createLabel(new LabelDto("Feature")));
        var task = taskRepository.save(new Task(
                "Task 1", "Descr", taskStatus, labels, author, executor));

        assertEquals(1, taskRepository.count());

        utils.perform(delete(TASKS_CONTROLLER + ID, task.getId()), TEST_USERNAME)
                .andExpect(status().isOk());

        assertEquals(0, taskRepository.count());
    }

    @Test
    public void deleteTaskFails() throws Exception {
        var author = utils.getUserByEmail(TEST_USERNAME);
        var executor = utils.getUserByEmail(EXECUTOR_USERNAME);
        var taskStatus = statusService.createNewStatus(new TaskStatusDto("New"));
        var labels = new HashSet<Label>();
        labels.add(labelService.createLabel(new LabelDto("Feature")));
        var task = taskRepository.save(new Task(
                "Task 1", "Descr", taskStatus, labels, author, executor));

        assertEquals(1, taskRepository.count());

        utils.perform(delete(TASKS_CONTROLLER + ID, task.getId()))
                .andExpect(status().isForbidden());

        assertEquals(1, taskRepository.count());
    }

}
