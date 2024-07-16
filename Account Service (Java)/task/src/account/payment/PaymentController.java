package account.payment;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.List;

@RestController()
@Validated
@AllArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/api/empl/payment")
    public Object getEmplPayment(@RequestParam(required = false) @DateTimeFormat(pattern = "MM-yyyy") Calendar period) {
        if(period==null){
            return paymentService.getPaymentsByCurrentEmployee();
        }else {
            return paymentService.getPaymentByCurrentEmployeeAndPeriod(period);
        }
    }

    @PostMapping("/api/acct/payments")
    public StatusDTO postAcctPayments(@RequestBody @UniqueElements List<@Valid Payment> paymentList) {
        return paymentService.addPayments(paymentList);
    }

    @PutMapping("/api/acct/payments")
    public StatusDTO putAcctPayments(@RequestBody @Valid Payment payment) {
        return paymentService.updatePayment(payment);
    }

}
