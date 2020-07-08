package socialnetwork.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;
import socialnetwork.model.Friendship;
import socialnetwork.model.Message;
import socialnetwork.model.aggregation.FriendshipsMax;
import socialnetwork.model.aggregation.MessagesCount;
import socialnetwork.model.User;
import socialnetwork.repository.UserRepositoryImpl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserRepositoryImpl userRepository;

    @GetMapping("{login}")
    public String getAllUsers(@PathVariable String login, Model model) {
        User user = userRepository.findUser(login);
        model.addAttribute("user", user);
        return "user";
    }

    @GetMapping(path = "{login}/messages", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public @ResponseBody Flux<Message> getUsers(@PathVariable String login) {
        return userRepository.findMessages(login).delayElements(Duration.ofMillis(250)).onBackpressureDrop();
    }

    @PostMapping("add")
    public @ResponseBody void addUser(@RequestBody User user) {
        userRepository.insert(user);
    }

    @PostMapping("{login}/messages")
    public @ResponseBody void addMessage(@PathVariable String login, @RequestBody Message message) {
        if (message.getCreationDateTime() == null) {
            message.setCreationDateTime(LocalDateTime.now());
        }
        userRepository.insertMessage(login, message).subscribe();
    }

    @PostMapping("{login}/addFriend")
    public @ResponseBody void addFriend(@PathVariable String login, @RequestBody Friendship friend) {
        if (friend.getDateTime() == null) {
            friend.setDateTime(LocalDateTime.now());
        }
        userRepository.insertFriendship(login, friend);
    }

    @GetMapping("{login}/addFriend")
    public @ResponseBody void addFriend(@PathVariable String login, @RequestParam String friend) {
        Friendship friendship = new Friendship();
        friendship.setUser(friend);
        friendship.setDateTime(LocalDateTime.now());
        userRepository.insertFriendship(login, friendship);
    }

    @GetMapping("{login}/addMovie")
    public @ResponseBody void addMovie(@PathVariable String login, @RequestParam String movie) {
        userRepository.insertMovie(login, movie);
    }

    @GetMapping("avgMessages")
    public @ResponseBody List<MessagesCount> getAvgMessages() {
        return userRepository.getAverageNumberOfMessagesByDayOfWeek();
    }

    @GetMapping("maxFriendships")
    public @ResponseBody List<FriendshipsMax> getMaxFriendships() {
        return userRepository.getMaxNewFriendshipByMonth();
    }

    @GetMapping("minMovie")
    public @ResponseBody int getMinMovie() {
        return userRepository.getMinWatchedMovieByUsersWithFriends();
    }
}
