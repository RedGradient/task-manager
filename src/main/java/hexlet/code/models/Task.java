package hexlet.code.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Builder
@Getter
@Setter
@Entity
@Table(name = "tasks")
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

    @ManyToMany
    @Builder.Default
    private Set<Label> labels = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "author_id")
    @NotNull
    private User author;
    @ManyToOne
    @JoinColumn(name = "executor_id")
    private User executor;
}
