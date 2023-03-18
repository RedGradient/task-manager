package hexlet.code.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "statuses")
@AllArgsConstructor
@NoArgsConstructor
public class Status extends BaseModel {
    private String name;
}
