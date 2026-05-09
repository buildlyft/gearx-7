package com.gearx7.app.web.rest;

import static com.gearx7.app.security.SecurityUtils.AUTHORITIES_KEY;
import static com.gearx7.app.security.SecurityUtils.JWT_ALGORITHM;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gearx7.app.service.interfaces.LoginCacheService;
import com.gearx7.app.service.interfaces.OtpService;
import com.gearx7.app.web.rest.errors.BadRequestAlertException;
import com.gearx7.app.web.rest.vm.LoginVM;
import com.gearx7.app.web.rest.vm.OtpVM;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    @Value("${jhipster.security.authentication.jwt.token-validity-in-seconds:0}")
    private long tokenValidityInSeconds;

    @Value("${jhipster.security.authentication.jwt.token-validity-in-seconds-for-remember-me:0}")
    private long tokenValidityInSecondsForRememberMe;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public AuthenticateController(
        JwtEncoder jwtEncoder,
        AuthenticationManagerBuilder authenticationManagerBuilder,
        OtpService otpService,
        LoginCacheService loginCacheService
    ) {
        this.jwtEncoder = jwtEncoder;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.otpService = otpService;
        this.loginCacheService = loginCacheService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authorize(@Valid @RequestBody LoginVM loginVM) {
        log.info("LOGIN_ATTEMPT | username={}", loginVM.getUsername());
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            loginVM.getUsername(),
            loginVM.getPassword()
        );

        // 1. authenticate
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        if (!loginCacheService.canSendOtp(loginVM.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please wait 60 seconds before requesting another OTP");
        }

        // 2. send OTP
        otpService.sendOtpToUserWhileLogin(loginVM.getUsername());

        // 3. store only if OTP success
        loginCacheService.store(loginVM.getUsername(), authentication);

        log.info("OTP_SENT | phone={}", loginVM.getUsername());

        return ResponseEntity.ok("OTP sent successfully");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<JWTToken> verifyOtp(@RequestBody OtpVM otpVM) {
        log.info("OTP_VERIFY_REQUEST | phone={}", otpVM.getPhoneNumber());

        //  Step 1: check OTP expiry FIRST
        Authentication authentication = loginCacheService.get(otpVM.getPhoneNumber());

        if (authentication == null) {
            log.error("OTP_EXPIRED | phone={}", otpVM.getPhoneNumber());
            throw new RuntimeException("OTP expired. Please login again.");
        }

        //  Step 2: verify OTP via MSG91
        boolean isValid = otpService.verifyOtpToLogin(otpVM.getPhoneNumber(), otpVM.getOtp());

        if (!isValid) {
            log.warn("OTP_INVALID | phone={}", otpVM.getPhoneNumber());
            throw new BadRequestAlertException("Invalid OTP. Please try again.", "Authentication", "InvalidOtp");
        }

        // Step 3: remove after use
        loginCacheService.remove(otpVM.getPhoneNumber());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Step 4: generate JWT
        String jwt = createToken(authentication, false);

        log.info("LOGIN_SUCCESS | phone={}", otpVM.getPhoneNumber());

        return ResponseEntity.ok(new JWTToken(jwt));
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
}
