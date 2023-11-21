package fr.meritis.first.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {
        private Long id;
        private String firstname;
        private String lastname;
        private String email;
        private String password;
        private String address;
        private String phone;
        private String title;
        private String bio;
        private String imageUrl;
        private boolean enabled;
        private boolean isNotLocked;
        private boolean isUsingMfa;
        private LocalDateTime createdAt;
}
