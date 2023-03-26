package hexlet.code.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@Setter
@Entity
@Table(name = "statuses")
@AllArgsConstructor
@NoArgsConstructor
public class Task extends BaseModel {

    @NotBlank
    private String name;
    @Lob
    private String description;
    @ManyToOne
    @JoinColumn(name = "task_status_id")
    @NotNull
    private TaskStatus taskStatus;
    @ManyToOne
    @JoinColumn(name = "author_id")
    @NotNull
    private User author;
    @ManyToOne
    @JoinColumn(name = "executor_id")
    private User executor;
}
