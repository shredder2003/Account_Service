package account.controller;

import account.DTOmapper;
import account.UserRepository;
import account.payment.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import static account.AccountServiceApplication.RUN_TYPE;

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



    @PutMapping("/api/admin/user/role")
    public String putAdminUserRole() {
        return "Y";
    }

    @DeleteMapping("/api/admin/user")
    public String deleteAdminUser() {
        return "Y";
    }

    @GetMapping("/api/admin/user")
    public String getAdminUser() {
        return "Y";
    }

}
