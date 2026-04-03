package lending.platform.lendingplatform.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.domain.Auditable;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity(name = "users")
public class User extends AuditClass {
    @Id
    @GeneratedValue
    private Long id;
    private String username;
    private String password;
}
