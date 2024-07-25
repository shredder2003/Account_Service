package account.admin;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="ROLES")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_generator")
    @SequenceGenerator(name = "role_generator", sequenceName = "role_seq", allocationSize = 1)
    private Integer role_id;
    //@Id
    @Column(unique = true)
    private AUTHORITY code;

    public Role(AUTHORITY code){
        this.code = code;
    }

    public String getName(){
        return code.name();
    }
    public String getFullName(){
        return code.roleName();
    }
}
