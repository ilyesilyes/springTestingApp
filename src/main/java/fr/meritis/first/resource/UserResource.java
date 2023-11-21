package fr.meritis.first.resource;

import fr.meritis.first.domain.HttpResponse;
import fr.meritis.first.domain.User;
import fr.meritis.first.dto.UserDTO;
import fr.meritis.first.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Map;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping(path = "/user")
@RequiredArgsConstructor
public class UserResource {
    private final UserService userService;
@PostMapping("/register")
    public ResponseEntity<HttpResponse> saveUser(@RequestBody @Valid User user){
        UserDTO userDTO = userService.createUser(user);
        return ResponseEntity.created(getUri()).<HttpResponse>body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(Map.of("user", userDTO))
                        .status(CREATED)
                        .statusCode(CREATED.value())
                        .build()
        );

    }

    private URI getUri() {
        return URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/get/<userId>").toUriString());
    }
}
