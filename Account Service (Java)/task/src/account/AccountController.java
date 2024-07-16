package account;

import account.user.DTOmapper;
import account.user.UserRepository;
import account.payment.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
public class AccountController {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DTOmapper dTOmapper;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountController(DTOmapper dTOmapper
            , UserRepository userRepository
            , PaymentRepository paymentRepository
            , PasswordEncoder passwordEncoder) {
        this.dTOmapper = dTOmapper;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
        this.passwordEncoder = passwordEncoder;
        if(account.AccountServiceApplication.RUN_TYPE.equals("clearDB")) {
            userRepository.deleteAll();
            paymentRepository.deleteAll();
        }
    }

}
