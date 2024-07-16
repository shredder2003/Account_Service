package account.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(13);
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(
                        matcherRegistry -> matcherRegistry
//                                .requestMatchers(HttpMethod.PUT, "api/admin/user/role").hasRole("ADMIN")
//                                .requestMatchers(HttpMethod.DELETE, "api/admin/user").hasRole("ADMIN")
//                                .requestMatchers(HttpMethod.GET, "api/admin/user").hasRole("ADMIN")
//                                .requestMatchers(HttpMethod.PUT, "api/acct/payments").hasRole("ACCOUNTANT")
//                                .requestMatchers(HttpMethod.POST, "api/acct/payments").hasRole("ACCOUNTANT")
                                .requestMatchers(HttpMethod.GET, "/api/empl/payment").authenticated()
                                .requestMatchers(HttpMethod.POST, "/api/auth/changepass").authenticated()
                                .requestMatchers(HttpMethod.POST, "/api/auth/signup").permitAll()
                                .requestMatchers(HttpMethod.GET, "/*").permitAll()
                                .requestMatchers("/**").permitAll()
                                .anyRequest().permitAll()
                )
                .httpBasic(Customizer.withDefaults())
                .csrf(csrfConfigurer -> csrfConfigurer.disable())  // for POST requests via Postman
                .headers(headers -> headers.frameOptions().disable()) // For the H2 console
                .sessionManagement(sessions -> sessions
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // no session
                )
                .build();
    }

}