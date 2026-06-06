package com.gearx7.app.web.rest;

import static com.gearx7.app.security.SecurityUtils.AUTHORITIES_KEY;
import static com.gearx7.app.security.SecurityUtils.JWT_ALGORITHM;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gearx7.app.config.AppConstants;
import com.gearx7.app.domain.User;
import com.gearx7.app.repository.UserRepository;
import com.gearx7.app.security.AuthoritiesConstants;
import com.gearx7.app.service.UserService;
import com.gearx7.app.service.dto.AdminUserDTO;
import com.gearx7.app.service.dto.ApiResponse;
import com.gearx7.app.service.interfaces.LoginCacheService;
import com.gearx7.app.service.interfaces.OtpService;
import com.gearx7.app.web.rest.errors.NotFoundAlertException;
import com.gearx7.app.web.rest.vm.LoginVM;
import com.gearx7.app.web.rest.vm.OtpVM;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;

/**
 * Controller to authenticate users.
 */
@RestController
@RequestMapping("/api")
public class AuthenticateController {

    private final Logger log = LoggerFactory.getLogger(AuthenticateController.class);

    private final JwtEncoder jwtEncoder;

    private final OtpService otpService;

    private final LoginCacheService loginCacheService;

    private final UserRepository userRepository;

    private final UserService userService;

    @Value("${jhipster.security.authentication.jwt.token-validity-in-seconds:0}")
    private long tokenValidityInSeconds;

    @Value("${jhipster.security.authentication.jwt.token-validity-in-seconds-for-remember-me:0}")
    private long tokenValidityInSecondsForRememberMe;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public AuthenticateController(
        JwtEncoder jwtEncoder,
        AuthenticationManagerBuilder authenticationManagerBuilder,
        OtpService otpService,
        LoginCacheService loginCacheService,
        UserRepository userRepository,
        UserService userService
    ) {
        this.jwtEncoder = jwtEncoder;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.otpService = otpService;
        this.loginCacheService = loginCacheService;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<ApiResponse<Void>> authorize(@Valid @RequestBody LoginVM loginVM) {
        log.info("LOGIN_ATTEMPT | mobile={}", mask(loginVM.getUsername()));

        String phoneNumber = loginVM.getUsername();

        if (!loginCacheService.canSendOtp(phoneNumber)) {
            log.warn("OTP_ALREADY_SENT | phone={}", mask(phoneNumber));
            return ResponseEntity.ok(new ApiResponse<>(false, HttpStatus.OK.value(), "OTP_ALREADY_SENT", null));
        }

        otpService.sendOtpToUserWhileLogin(phoneNumber);
        log.info("OTP_SENT | phone={}", mask(phoneNumber));

        loginCacheService.store(phoneNumber, phoneNumber);

        return ResponseEntity.ok(
            new ApiResponse<>(true, HttpStatus.OK.value(), "OTP sent successfully. Please verify to complete login.", null)
        );
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<JWTToken>> verifyOtp(@Valid @RequestBody OtpVM otpVM) {
        log.info("OTP_VERIFY_REQUEST | phone={}", mask(otpVM.getPhoneNumber()));

        // Step 1: Check OTP expiry
        String login = loginCacheService.get(otpVM.getPhoneNumber());

        if (login == null) {
            log.info("OTP_EXPIRED | phone={}", mask(otpVM.getPhoneNumber()));

            return ResponseEntity.ok(new ApiResponse<>(false, HttpStatus.OK.value(), "OTP expired. Please login again.", null));
        }

        // Step 2: Verify OTP via MSG91
        boolean isValid = otpService.verifyOtpToLogin(otpVM.getPhoneNumber(), otpVM.getOtp());

        if (!isValid) {
            log.info("OTP_INVALID | phone={}", mask(otpVM.getPhoneNumber()));
            loginCacheService.remove(otpVM.getPhoneNumber());
            return ResponseEntity.ok(new ApiResponse<>(false, HttpStatus.OK.value(), "Invalid_OTP.", null));
        }

        if (
            otpVM.getAppType() == null ||
            (!AppConstants.CUSTOMER.equalsIgnoreCase(otpVM.getAppType()) && !AppConstants.PARTNER.equalsIgnoreCase(otpVM.getAppType()))
        ) {
            log.warn("INVALID_APP_TYPE | phone={} | appType={}", mask(otpVM.getPhoneNumber()), otpVM.getAppType());
            loginCacheService.remove(otpVM.getPhoneNumber());
            return ResponseEntity.ok(new ApiResponse<>(false, HttpStatus.OK.value(), "Invalid app type specified.", null));
        }

        // Step 3: Load user
        Optional<User> userOpt = userRepository.findOneWithAuthoritiesByPhone(otpVM.getPhoneNumber());

        User user;
        if (userOpt.isPresent()) {
            user = userOpt.orElseThrow(() -> new NotFoundAlertException("User Not Found ", "verify OTP", "userNotFound"));
            log.info("EXISTING_USER_LOGIN | phone={}", mask(otpVM.getPhoneNumber()));

            boolean isPartner = user.getAuthorities().stream().anyMatch(a -> AuthoritiesConstants.PARTNER.equals(a.getName()));

            // Partner App Login
            if (AppConstants.PARTNER.equalsIgnoreCase(otpVM.getAppType()) && !isPartner) {
                log.warn("PARTNER_ACCESS_DENIED | phone={}", mask(otpVM.getPhoneNumber()));
                loginCacheService.remove(otpVM.getPhoneNumber());
                return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, HttpStatus.FORBIDDEN.value(), "Your not eligible to login to partner app.", null));
            }
        } else {
            AdminUserDTO userDTO = new AdminUserDTO();

            userDTO.setLogin(otpVM.getPhoneNumber());
            userDTO.setPhone(otpVM.getPhoneNumber());

            String authority = AppConstants.PARTNER.equalsIgnoreCase(otpVM.getAppType())
                ? AuthoritiesConstants.PARTNER
                : AuthoritiesConstants.USER;

            userDTO.setAuthorities(Set.of(authority));

            log.info("AUTO_USER_REGISTRATION | phone={} | role={}", mask(otpVM.getPhoneNumber()), authority);

            user = userService.createUser(userDTO);
        }
        // Step 4: Create Authentication
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            user.getLogin(),
            null,
            user.getAuthorities().stream().map(authority -> new SimpleGrantedAuthority(authority.getName())).collect(Collectors.toList())
        );

        // Step 5: Remove cache entry
        loginCacheService.remove(otpVM.getPhoneNumber());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Step 6: Generate JWT
        String jwt = createToken(authentication, false);
        log.info("JWT_GENERATED | phone={}", mask(otpVM.getPhoneNumber()));

        log.info("LOGIN_SUCCESS | phone={}", mask(otpVM.getPhoneNumber()));

        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(), "Login successful.", new JWTToken(jwt)));
    }

    /**
     * {@code GET /authenticate} : check if the user is authenticated, and return its login.
     *
     * @param request the HTTP request.
     * @return the login if the user is authenticated.
     */
    @GetMapping("/authenticate")
    public String isAuthenticated(HttpServletRequest request) {
        log.debug("REST request to check if the current user is authenticated");
        return request.getRemoteUser();
    }

    public String createToken(Authentication authentication, boolean rememberMe) {
        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(" "));

        Instant now = Instant.now();
        Instant validity;
        if (rememberMe) {
            validity = now.plus(this.tokenValidityInSecondsForRememberMe, ChronoUnit.SECONDS);
        } else {
            validity = now.plus(this.tokenValidityInSeconds, ChronoUnit.SECONDS);
        }

        // @formatter:off
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuedAt(now)
            .expiresAt(validity)
            .subject(authentication.getName())
            .claim(AUTHORITIES_KEY, authorities)
            .build();

        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    /**
     * Object to return as body in JWT Authentication.
     */
    static class JWTToken {

        private String idToken;

        JWTToken(String idToken) {
            this.idToken = idToken;
        }

        @JsonProperty("id_token")
        String getIdToken() {
            return idToken;
        }

        void setIdToken(String idToken) {
            this.idToken = idToken;
        }
    }

    private String mask(String phone) {
        if (phone == null || phone.length() != 10) {
            return "INVALID GIVEN PHONE NUMBER";
        }

        return phone.substring(0, 2)
            + "******"
            + phone.substring(8);
    }
}
