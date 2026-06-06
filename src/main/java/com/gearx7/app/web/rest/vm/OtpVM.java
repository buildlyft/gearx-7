package com.gearx7.app.web.rest.vm;

import jakarta.mail.Message;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class OtpVM {

    @NotBlank(message = "Please enter a valid 10-digit mobile number")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must have 10 digits")
    private String phoneNumber;

    @NotBlank(message = "Please enter received OTP")
    private String otp;

    @NotBlank(message = "Please specify the app type")
    private String appType;

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
