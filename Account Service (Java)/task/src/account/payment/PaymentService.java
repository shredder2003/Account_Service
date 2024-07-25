package account.payment;

import account.admin.AUTHORITY;
import account.admin.Role;
import account.admin.RoleRepository;
import account.admin.exception.AccessDeniedException;
import account.user.CurrentUser;
import account.user.User;
import account.user.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.YearMonth;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Slf4j
public class PaymentService {
    private PaymentRepository paymentRepository;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private CurrentUser currentUser;

    public void checkIsAdmin(){
        Role roleAdmin = roleRepository.findRoleByCode(AUTHORITY.ADMINISTRATOR).orElseThrow();
        if( ! currentUser.get().getUser().getUserRoles().contains(roleAdmin) ) {
            throw new AccessDeniedException();
        }
    }

    public void checkIsAccountant(){
        Role roleAccountant = roleRepository.findRoleByCode(AUTHORITY.ACCOUNTANT).orElseThrow();
        //Group groupUser = groupRepository.findGroupByCode(AUTHORITY.USER).orElseThrow();
        if(! currentUser.get().getUser().getUserRoles().contains(roleAccountant)
           //||currentUser.get().getUser().getUserGroups().contains(groupUser)
        ) {
            throw new AccessDeniedException();
        }
    }

    public void checkIsAccountantOrUser(){
        Role roleAccountant = roleRepository.findRoleByCode(AUTHORITY.ACCOUNTANT).orElseThrow();
        Role roleUser = roleRepository.findRoleByCode(AUTHORITY.USER).orElseThrow();
        if(!
            (
              currentUser.get().getUser().getUserRoles().contains(roleAccountant)
            ||currentUser.get().getUser().getUserRoles().contains(roleUser)
            )
        ) {
            throw new AccessDeniedException();
        }
    }

    @Transactional
    //@PreAuthorize("hasRole('ROLE_ACCOUNTANT')")
    public StatusDTO addPayments(List<Payment> payments) {
        checkIsAccountant();
        for (var payment : payments) {
            if (userRepository.findUserByUsernameIgnoreCase(payment.getEmployee()).isEmpty()) {
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
    //@PreAuthorize("hasRole('ROLE_ACCOUNTANT')")
    public StatusDTO updatePayment(Payment payment) {
        checkIsAccountant();
        Payment paymentDB = paymentRepository
                .findPaymentByEmployeeAndPeriod(payment.getEmployee(), payment.getPeriod())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Record not found"));
        paymentDB.setSalary(payment.getSalary());
        paymentRepository.save(paymentDB);
        return new StatusDTO("Updated successfully!");
    }

    @Transactional
    //@PreAuthorize("hasAnyRole('ROLE_ACCOUNTANT','ROLE_USER')")
    public Object getPaymentsByCurrentEmployee(){
        log.info("getPaymentsByCurrentEmployee(+)");
        checkIsAccountantOrUser();
        User user = currentUser.get().getUser();
        log.info("getPaymentsByCurrentEmployee user.getUserGroups().size="+user.getUserRoles().size()+" getUserGroups="+user.getUserRoles());
        List<Payment> paymentList = paymentRepository.findAllByEmployeeOrderByPeriodDesc(user.getUsername());
        log.info("getPaymentsByCurrentEmployee(-)");
        return paymentList.stream().map(p->PaymentDTO.builder()
                .name(user.getName())
                .lastname(user.getLastname())
                .period(p.getPeriodText())
                .salary(p.getSalaryText())
                .build()
        ).collect(Collectors.toList());
    }

    @Transactional
    //@PreAuthorize("hasAnyRole('ROLE_ACCOUNTANT','ROLE_USER')")
    public Object getPaymentByCurrentEmployeeAndPeriod(Calendar period){
        log.info("getPaymentByCurrentEmployeeAndPeriod(+)");
        checkIsAccountantOrUser();
        //User user = userRepository.findUserByUsername(currentUser.get().getUsername()).get();
        User user = currentUser.get().getUser();
        Payment payment = paymentRepository.findPaymentByEmployeeAndPeriod(user.getUsername(),calendarToYearMonth(period))
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Record with the specified period not found") );
        log.info("getPaymentByCurrentEmployeeAndPeriod(-)");
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
