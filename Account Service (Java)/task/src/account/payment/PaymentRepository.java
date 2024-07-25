package account.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
        Optional<Payment> findPaymentByEmployeeAndPeriod(String employee, YearMonth period);
        List<Payment> findAllByEmployeeAndPeriod(String employee, YearMonth period);
        List<Payment> findAllByEmployee(String employee);
        List<Payment> findAllByEmployeeOrderByPeriodDesc(String employee);
}

