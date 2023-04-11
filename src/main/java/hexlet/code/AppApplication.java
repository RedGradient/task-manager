package hexlet.code;

//import io.swagger.v3.oas.annotations.OpenAPIDefinition;
//import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import springfox.documentation.oas.annotations.EnableOpenApi;


@SpringBootApplication
//@EnableOpenApi
//@OpenAPIDefinition(
//    info = @Info(
//        title = "Task Manager API",
//        version = "1.0",
//        description = "CRUD API for all entities of the Task Manager"
//    )
//)
public class AppApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }

}
