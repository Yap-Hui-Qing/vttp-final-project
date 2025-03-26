package vttpb_final_project.travelplanner.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import vttpb_final_project.travelplanner.jwt.JwtUtils;
import vttpb_final_project.travelplanner.models.UserEntity;
import vttpb_final_project.travelplanner.models.Request.AuthRequest;
import vttpb_final_project.travelplanner.models.Response.LoginResponse;
import vttpb_final_project.travelplanner.repositories.UserRepository;
import vttpb_final_project.travelplanner.services.UserService;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final Logger logger = Logger.getLogger(AuthController.class.getName());
    
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserService userSvc;

    @Autowired
    private UserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<?> postLogin(@RequestBody AuthRequest loginRequest) {
        System.out.printf(">>> Login: ", loginRequest);

        Authentication authentication;
        try {
            // to throw username not found
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (UsernameNotFoundException ex){
            JsonObject error = Json.createObjectBuilder()
                .add("message", "Username not found")
                .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error.toString());
        } catch (BadCredentialsException ex) {
            JsonObject error = Json.createObjectBuilder()
                .add("message", "Incorrect username or password")
                .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error.toString());
        } 

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);

        LoginResponse response = new LoginResponse(userDetails.getUsername(), jwtToken);
        System.out.println(response);

        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/register")
    public ResponseEntity<?> postRegister(@RequestBody AuthRequest registerRequest){

        System.out.printf(">>> registration: ", registerRequest);
        if (userRepo.existsByUsername(registerRequest.getUsername())){
            JsonObject error = Json.createObjectBuilder()
                .add("message", "User already exists")
                .build();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error.toString()); // Use 409 for conflictMap<String, String> errorResponse = new HashMap<>();
        }

        UserEntity user = new UserEntity(
            registerRequest.getUsername(),
            new BCryptPasswordEncoder().encode(registerRequest.getPassword())
        );
        userSvc.registerUser(user);
        return new ResponseEntity<>(user, HttpStatus.OK);

    }
}
