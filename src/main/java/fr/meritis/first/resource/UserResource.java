package fr.meritis.first.resource;

import fr.meritis.first.domain.HttpResponse;
import fr.meritis.first.domain.User;
import fr.meritis.first.domain.UserPrincipal;
import fr.meritis.first.dto.UserDTO;
import fr.meritis.first.exception.ApiException;
import fr.meritis.first.form.LoginForm;
import fr.meritis.first.provider.TokenProvider;
import fr.meritis.first.service.RoleService;
import fr.meritis.first.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static fr.meritis.first.dto.dtomapper.UserDTOMapper.toUser;
import static fr.meritis.first.utils.ExceptionUtils.processError;
import static java.time.LocalDateTime.now;
import static java.util.Map.of;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.security.authentication.UsernamePasswordAuthenticationToken.unauthenticated;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;

@RestController
@RequestMapping(path = "/user")
@RequiredArgsConstructor
public class UserResource {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final RoleService roleService;
    private final HttpServletRequest request;
    private final HttpServletResponse response;

    @PostMapping("/login")
    public ResponseEntity<HttpResponse> login(@RequestBody @Valid LoginForm loginForm){
        Authentication authentication = authenticate(loginForm.getEmail(), loginForm.getPassword());
        UserDTO user = getAuthenticateUser(authentication);
        return user.isUsingMfa() ? sendVerificationCode(user) : sendResponse(user);
    }

    private UserDTO getAuthenticateUser(Authentication authentication) {
        return ((UserPrincipal) authentication.getPrincipal()).getUserDTO();
    }

    @PostMapping("/register")
    public ResponseEntity<HttpResponse> saveUser(@RequestBody @Valid User user){
        UserDTO userDTO = userService.createUser(user);
        return ResponseEntity.created(getUri()).body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(of("user", userDTO))
                        .status(CREATED)
                        .statusCode(CREATED.value())
                        .build()
        );

    }

    @GetMapping("/profile")
    public ResponseEntity<HttpResponse> profile(Authentication authentication){
        UserDTO userDTO = userService.getUserByEmail(authentication.getName());
        System.out.println(authentication);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(of("user", userDTO))
                        .message("Profile Retrieved")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );

    }

    @GetMapping("/verify/code/{email}/{code}")
    public ResponseEntity<HttpResponse> verifyCode(@PathVariable("email") String email, @PathVariable("code") String code){
        UserDTO userDTO = userService.verifyCode(email, code);
        return ResponseEntity.created(getUri()).body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(of("user", userDTO, "access_token", tokenProvider.createAccessToken(getUserPrincipal(userDTO)),
                                "refresh_token", tokenProvider.createRefreshToken(getUserPrincipal(userDTO))))
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );

    }

    private URI getUri() {
        return URI.create(fromCurrentContextPath().path("/user/get/<userId>").toUriString());
    }

    private ResponseEntity<HttpResponse> sendResponse(UserDTO user) {
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(of("user", user, "access_token", tokenProvider.createAccessToken(getUserPrincipal(user)), "refresh_token", tokenProvider.createRefreshToken(getUserPrincipal(user))))
                        .message("Login success")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    @RequestMapping("/error")
    public ResponseEntity<HttpResponse> handleError(HttpServletRequest request){
        return ResponseEntity.badRequest().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .reason("There is no mapping for a " + request.getMethod() + " request for this  path on the server")
                        .status(BAD_REQUEST)
                        .statusCode(BAD_REQUEST.value())
                        .build()
        );

    }

    private UserPrincipal getUserPrincipal(UserDTO user) {
        return new UserPrincipal(toUser(userService.getUserByEmail(user.getEmail())), roleService.getRoleByUserID(user.getId()));
    }

    private ResponseEntity<HttpResponse> sendVerificationCode(UserDTO user) {
    userService.sendVerificationCode(user);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(of("user", user))
                        .message("Verification code send")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    private Authentication authenticate(String email, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(unauthenticated(email, password));
            return authentication;
        } catch (Exception exception) {
            processError(request, response, exception);
            throw new ApiException(exception.getMessage());
        }
    }

}
