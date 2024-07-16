package account.payment;

import account.user.CurrentUser;
import account.user.User;
import account.user.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.YearMonth;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class PaymentService {
    private PaymentRepository paymentRepository;
    private UserRepository userRepository;
    private CurrentUser currentUser;
    //Logger logger = LoggerFactory.getLogger(this.getClass());

    @Transactional
    @PreAuthorize("hasRole('ROLE_ACCOUNTANT')")
    public StatusDTO addPayments(List<Payment> payments) {
        for (var payment : payments) {
            if (userRepository.findUserByUsername(payment.getEmployee()).isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee with specified email not found");
            }
            if (paymentRepository.findPaymentByEmployeeAndPeriod(payment.getEmployee(), payment.getPeriod()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Record already created");
            }
            paymentRepository.save(payment);
        }
        return new StatusDTO("Added successfully!");
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_ACCOUNTANT')")
    public StatusDTO updatePayment(Payment payment) {
        Payment paymentDB = paymentRepository
                .findPaymentByEmployeeAndPeriod(payment.getEmployee(), payment.getPeriod())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Record not found"));
        paymentDB.setSalary(payment.getSalary());
        paymentRepository.save(paymentDB);
        return new StatusDTO("Updated successfully!");
    }

    @PreAuthorize("hasAnyRole('ROLE_ACCOUNTANT','ROLE_USER')")
    public Object getPaymentsByCurrentEmployee(){
        User user = currentUser.get().getUser();
        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("user.getUserGroups().size="+user.getUserGroups().size()+" getUserGroups="+user.getUserGroups());
        List<Payment> paymentList = paymentRepository.findAllByEmployeeOrderByPeriodDesc(user.getUsername());
        return paymentList.stream().map(p->PaymentDTO.builder()
                .name(user.getName())
                .lastname(user.getLastname())
                .period(p.getPeriodText())
                .salary(p.getSalaryText())
                .build()
        ).collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyRole('ROLE_ACCOUNTANT','ROLE_USER')")
    public Object getPaymentByCurrentEmployeeAndPeriod(Calendar period){
        //User user = userRepository.findUserByUsername(currentUser.get().getUsername()).get();
        User user = currentUser.get().getUser();
        Payment payment = paymentRepository.findPaymentByEmployeeAndPeriod(user.getUsername(),calendarToYearMonth(period))
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Record with the specified period not found") );
        return PaymentDTO.builder()
                .name(user.getName())
                .lastname(user.getLastname())
                .period(payment.getPeriodText())
                .salary(payment.getSalaryText())
                .build()
                ;
    }

    private YearMonth calendarToYearMonth(Calendar calendar) {
        return YearMonth.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);
    }

}
