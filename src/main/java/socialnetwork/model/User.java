package socialnetwork.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Data
public class User {
    @Id
    private String login;
    private String name;
    private String password;
    private List<Message> messages;
    private List<String> movies;
    private List<AudioTrack> audioTracks;
    private List<Friendship> friendships;
}
