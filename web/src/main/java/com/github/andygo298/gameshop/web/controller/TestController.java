package com.github.andygo298.gameshop.web.controller;

import com.github.andygo298.gameshop.model.entity.User;
import com.github.andygo298.gameshop.model.enums.Role;
import com.github.andygo298.gameshop.model.enums.Status;
import com.github.andygo298.gameshop.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:80/gameshop")
@RequestMapping("/api")
public class TestController {

    private UserService userService;

    public TestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/testCode")
    public ResponseEntity<String> testSave() {
        User user = User.builder()
                .firstName("Andrew")
                .lastName("Loz")
                .email("andy@go.com")
                .password("qwef")
                .createdAt(LocalDateTime.now().toLocalDate())
                .role(Role.TRADER)
                .status(Status.BANNED)
                .build();
        boolean result = userService.saveActivateCode(user);
        if (result) {
            return ResponseEntity.ok("CODE save successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/testCode")
    public String test(@RequestParam String activateCode) {
        String code = userService.getActivateCode(activateCode);
        if (Objects.nonNull(code)) {
            return code;
        } else
            return "bad";
    }
}
