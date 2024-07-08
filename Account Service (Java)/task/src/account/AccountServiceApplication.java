package account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.modelmapper.ModelMapper;

@SpringBootApplication
public class AccountServiceApplication {
    public static String RUN_TYPE = "-";

    public static void main(String[] args) {
        if(args.length>0) RUN_TYPE = args[0];
        SpringApplication.run(AccountServiceApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}

