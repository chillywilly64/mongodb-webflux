package socialnetwork.repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import socialnetwork.model.Friendship;
import socialnetwork.model.Message;
import socialnetwork.model.User;
import socialnetwork.model.aggregation.FriendshipsMax;
import socialnetwork.model.aggregation.MessagesCount;

import java.util.Collection;
import java.util.List;

public interface UserRepository {
    User insert(User user);

    Flux<User> insertAll(Collection<User> users);

    Flux<User> findAllUsers();

    User findUser(String login);

    Mono<Message> insertMessage(String login, Message message);

    Flux<Message> findMessages(String login);

    void insertFriendship(String login, Friendship friendship);

    void insertMovie(String login, String movie);

    List<MessagesCount> getAverageNumberOfMessagesByDayOfWeek();

    List<FriendshipsMax> getMaxNewFriendshipByMonth();

    int getMinWatchedMovieByUsersWithFriends();
}
