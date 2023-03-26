package hexlet.code.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.dto.UserDto;
import hexlet.code.models.Task;
import hexlet.code.models.User;
import hexlet.code.repositories.TaskRepository;
import hexlet.code.repositories.TaskStatusRepository;
import hexlet.code.repositories.UserRepository;
import hexlet.code.service.TaskService;
import hexlet.code.service.TaskStatusService;
import hexlet.code.service.UserService;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.controller.TaskController.ID;
import static hexlet.code.utils.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
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
    private TestUtils utils;


    @BeforeAll
    public void beforeAll() throws Exception {
        utils.tearDown();
    }
    @BeforeEach
    public void beforeEach() throws Exception {
        utils.tearDown();
        utils.regDefaultUser();
        userService.createNewUser(new UserDto(
                EXECUTOR_USERNAME,
                "Good",
                "Executor",
                "12345"
        ));
    }

    @AfterEach
    public void clear() {
        utils.tearDown();
    }

    @Test
    public void contextLoads() {
        assertNotNull(controller);
    }

    @Test
    public void createTask() throws Exception {
        var executor = utils.getUserByEmail(EXECUTOR_USERNAME);
        var taskStatus = statusService.createNewStatus(new TaskStatusDto("New"));
        var taskDto = new TaskDto(
                "Task",
                "Description",
                taskStatus.getId(),
                executor.getId()
        );

        var request = post(TASKS_CONTROLLER)
                .content(asJson(taskDto))
                .contentType(APPLICATION_JSON);
        var response = utils.perform(request, TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void getTasks() throws Exception {
        // create some tasks
        var author = utils.getUserByEmail(TEST_USERNAME);
        var executor = utils.getUserByEmail(EXECUTOR_USERNAME);
        var taskStatus = statusService.createNewStatus(new TaskStatusDto("New"));

        final var tasksCountBeforeAdding = taskService.getTasksCount();
        final var newTaskCount = 3;

        taskRepository.save(new Task("Task", "Descr", taskStatus, author, executor));
        taskRepository.save(new Task("Task", "Descr", taskStatus, author, executor));
        taskRepository.save(new Task("Task", "Descr", taskStatus, author, executor));
        // -------

        var response = utils.perform(get(TASKS_CONTROLLER))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        final List<Task> tasks = fromJson(
                response.getContentAsString(), new TypeReference<>() { }
        );

        assertEquals(newTaskCount + tasksCountBeforeAdding, tasks.size());
    }


    @Test
    public void getTaskById() throws Exception {
        var author = utils.getUserByEmail(TEST_USERNAME);
        var executor = utils.getUserByEmail(EXECUTOR_USERNAME);
        var taskStatus = statusService.createNewStatus(new TaskStatusDto("New"));
        var task = new Task(
                "Task",
                "Description",
                taskStatus,
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
    public void createTaskFails() throws Exception { }

    @Test
    public void updateTask() throws Exception { }

    @Test
    public void updateTaskFails() throws Exception { }

    @Test
    public void deleteTask() throws Exception { }

    @Test
    public void deleteTaskFails() throws Exception { }

}
