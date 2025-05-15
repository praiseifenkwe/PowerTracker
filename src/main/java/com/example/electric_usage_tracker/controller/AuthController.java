package com.example.electric_usage_tracker.controller;

import com.example.electric_usage_tracker.model.User;
import com.example.electric_usage_tracker.service.OtpService;
import com.example.electric_usage_tracker.service.UserService;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@SessionAttributes({"pendingUser", "signupEmail"})
public class AuthController {

    private final UserService userService;
    private final OtpService otpService;

    public AuthController(UserService userService, OtpService otpService) {
        this.userService = userService;
        this.otpService = otpService;
    }

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String processSignup(@ModelAttribute User user, Model model, RedirectAttributes redirectAttributes) {
        // Validate email exists
        if (userService.emailExists(user.getEmail())) {
            model.addAttribute("emailError", "Email already in use. Please use a different email or log in.");
            return "signup";
        }
        
        // Validate email format
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!user.getEmail().matches(emailRegex)) {
            model.addAttribute("emailError", "Please enter a valid email address.");
            return "signup";
        }
        
        // Validate password strength
        String password = user.getPassword();
        boolean hasUpperCase = !password.equals(password.toLowerCase());
        boolean hasLowerCase = !password.equals(password.toUpperCase());
        boolean hasNumbers = password.matches(".*\\d.*");
        boolean hasSpecialChars = password.matches(".*[!@#$%^&*(),.?\":\\{\\}|<>].*");
        int strengthFactors = (hasUpperCase ? 1 : 0) + (hasLowerCase ? 1 : 0) + 
                             (hasNumbers ? 1 : 0) + (hasSpecialChars ? 1 : 0);
        
        if (password.length() < 8 || strengthFactors < 3) {
            model.addAttribute("passwordError", "Please use a stronger password (at least 8 characters with a mix of uppercase, lowercase, numbers, and special characters).");
            return "signup";
        }
        
        // For Nigerian version - bypass email verification
        // Mark email as already verified and save user directly
        user.setEmailVerified(true);
        userService.saveUser(user);
        
        redirectAttributes.addFlashAttribute("message", "Account created successfully. Please log in.");
        return "redirect:/login";
    }
    
    @GetMapping("/verify-email")
    public String showVerifyEmailForm(HttpSession session) {
        // Ensure we have an email in the session
        if (session.getAttribute("signupEmail") == null) {
            return "redirect:/signup";
        }
        
        return "verify-email";
    }
    
    @PostMapping("/verify-email")
    public String verifyEmail(@RequestParam("otp") String otp,
                              @RequestParam(value = "resend", required = false) String resend,
                              Model model,
                              HttpSession session,
                              SessionStatus sessionStatus,
                              RedirectAttributes redirectAttributes) {
        
        String email = (String) session.getAttribute("signupEmail");
        User pendingUser = (User) session.getAttribute("pendingUser");
        
        if (email == null || pendingUser == null) {
            return "redirect:/signup";
        }
        
        // Handle resend request
        if (resend != null) {
            User tempUser = new User(pendingUser.getName(), email, "temporary");
            otpService.createOtpForEmailVerification(tempUser);
            redirectAttributes.addFlashAttribute("message", "A new verification code has been sent to your email.");
            return "redirect:/verify-email";
        }
        
        // Temporary user for OTP verification
        User tempUser = new User(pendingUser.getName(), email, "temporary");
        boolean isValid = otpService.verifyOtp(tempUser, otp, OtpService.PURPOSE_EMAIL_VERIFICATION);
        
        if (!isValid) {
            model.addAttribute("error", "Invalid or expired verification code.");
            return "verify-email";
        }
        
        // OTP is valid, save the user
        pendingUser.setEmailVerified(true);
        userService.saveUser(pendingUser);
        
        // Clean up session
        sessionStatus.setComplete();
        
        redirectAttributes.addFlashAttribute("message", "Email verified successfully. Please log in.");
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
}