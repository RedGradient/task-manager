package hexlet.code.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "labels")
@AllArgsConstructor
@NoArgsConstructor
public class Label extends BaseModel {
    @NotNull
    @NotBlank
    @Column(unique = true)
    private String name;

//    @ManyToMany
//    private List<Task> tasks;
//
//    public Label(String name) {
//        this.name = name;
//    }
}
