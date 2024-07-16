package account.payment;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

@Entity
//@Table(name="payment")
@Table(
        name="payment",
        uniqueConstraints=
        @UniqueConstraint(columnNames={"employee", "period"})
)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_generator")
    @SequenceGenerator(name = "user_generator", sequenceName = "user_SEQ", allocationSize = 1)
    private Integer id;
    @NotBlank
    private String employee;
    @NotNull
    @JsonFormat(pattern = "MM-yyyy")
    @Convert(
            converter = YearMonthDateAttributeConverter.class
    )
    private YearMonth period;
    @Positive
    private Integer salary;

    public String getSalaryText(){
        int dollars = salary/100;
        int cents   = salary%100;
        return String.format("%d dollar(s) %d cent(s)",dollars,cents);
    }

    public String getPeriodText(){
        return period.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()) + "-" + period.getYear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment that = (Payment) o;
        return getEmployee().equals(that.getEmployee()) && getPeriod().equals(that.getPeriod());
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (employee == null ? 0 : employee.hashCode());
        result = 31 * result + (period == null ? 0 : period.hashCode());
        return result;
    }
}
