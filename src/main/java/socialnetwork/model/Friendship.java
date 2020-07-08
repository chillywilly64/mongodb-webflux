package socialnetwork.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Friendship {
    private String user;
    private LocalDateTime dateTime;
}
