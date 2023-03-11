package hexlet.code.models;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.MappedSuperclass;

import org.hibernate.annotations.CreationTimestamp;

import static jakarta.persistence.GenerationType.AUTO;
import static jakarta.persistence.TemporalType.TIMESTAMP;

import java.util.Date;


@Getter
@Setter
@MappedSuperclass
public class BaseModel {
    @Id
    @GeneratedValue(strategy = AUTO)
    protected Long id;

    @CreationTimestamp
    @Temporal(TIMESTAMP)
    protected Date createdAt;

    private boolean active = true;
}
