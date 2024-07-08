package account.payment;

import account.DTOmapper;
import account.User;
import account.UserRepository;
import account.dto.StatusDTO;
import account.exception.BreachedPasswordException;
import account.exception.DuplicateEntryInPaymentListException;
import account.exception.PeriodWrongException;
import account.exception.SalaryNegativeException;
import account.validation.UniqueList;
import jakarta.validation.Valid;
import org.hibernate.validator.constraints.UniqueElements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@RestController()
public class PaymentController {
    private final DTOmapper dTOmapper;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    public PaymentController(
            DTOmapper dTOmapper
            , PaymentRepository paymentRepository
            , UserRepository    userRepository
    ) {
        this.dTOmapper = dTOmapper;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/api/empl/payment")
    public Object getEmplPayment(@RequestParam(required = false) @DateTimeFormat(pattern = "MM-yyyy") Calendar period, Authentication auth) {
        UserDetails details = (UserDetails) auth.getPrincipal();
        User user = userRepository.findUserByUsername(details.getUsername()).get();
        if(period==null){
            List<PaymentDTO> paymentDTOList = new ArrayList<>();
            List<Payment> paymentList;
            paymentList = paymentRepository.findAllByEmployeeOrderByPeriodDesc(details.getUsername());
            paymentList.forEach(payment -> {
                PaymentDTO paymentDTO = new PaymentDTO(user.getName(), user.getLastname(), payment.getPeriodText(), payment.getSalaryText() );
                paymentDTOList.add(paymentDTO);
            });
            return paymentDTOList;
        }else {
            /*try {
                YearMonth yearMonth = YearMonth.parse(period, DateTimeFormatter.ofPattern("MM-yyyy"));
            } catch (Exception e) {
                throw new PeriodWrongException();
            }*/
            Payment payment = paymentRepository.findPaymentByEmployeeAndPeriod(details.getUsername(),calendarToYearMonth(period)).get();
            PaymentDTO paymentDTO = new PaymentDTO(user.getName(), user.getLastname(), payment.getPeriodText(), payment.getSalaryText() );
            return paymentDTO;
        }
    }


    @Transactional
    @PostMapping("/api/acct/payments")
    public StatusDTO postAcctPayments(@RequestBody @Valid @UniqueList List<Payment> paymentList) {
        paymentList.forEach( payment -> {
                    /*if (payment.getSalary() <= 0) {
                        throw new SalaryNegativeException();
                    }
                    try {
                        YearMonth yearMonth = YearMonth.parse(payment.getPeriod(), DateTimeFormatter.ofPattern("MM-yyyy"));
                    } catch (Exception e) {
                        throw new PeriodWrongException();
                    }*/
                    if(
                       paymentList.stream().distinct().count() != (long) paymentList.size()
                      )
                    {
                        throw new DuplicateEntryInPaymentListException();
                    }
                }
        );
        //paymentList.forEach(paymentRepository::save);
        paymentRepository.saveAll(paymentList);
        return new StatusDTO("Added successfully!");
    }

    @PutMapping("/api/acct/payments")
    public StatusDTO putAcctPayments(@Valid @RequestBody Payment payment, Authentication auth) {
        /*if (payment.getSalary() <= 0) {
            throw new SalaryNegativeException();
        }
        try {
            YearMonth yearMonth = YearMonth.parse(payment.getPeriod(), DateTimeFormatter.ofPattern("MM-yyyy"));
        } catch (Exception e) {
            throw new PeriodWrongException();
        }*/
        //UserDetails details = (UserDetails) auth.getPrincipal();
        //User user = userRepository.findUserByUsername(details.getUsername()).get();
        //user.getUsername()
        Optional<Payment> dbPayment = paymentRepository.findPaymentByEmployeeAndPeriod(payment.getEmployee(),payment.getPeriod());
        Payment updatedPayment;
        if(dbPayment.isPresent()) {
            updatedPayment = dbPayment.get();
            updatedPayment.setSalary(payment.getSalary());
        }else{
            updatedPayment = payment;
        }
        paymentRepository.save(updatedPayment);
        return new StatusDTO("Updated successfully!");
    }

    private YearMonth calendarToYearMonth(Calendar calendar) {
        return YearMonth.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);
    }
}
