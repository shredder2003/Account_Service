package account.admin;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="group")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Group{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "group_generator")
    @SequenceGenerator(name = "group_generator", sequenceName = "group_SEQ", allocationSize = 1)
    private Integer group_id;
    private AUTHORITY code;
    private String type;

    public Group(AUTHORITY code){
        this.code = code;
    }

    public String getName(){
        return code.name();
    }
    public String getRoleName(){
        return code.roleName();
    }
}
