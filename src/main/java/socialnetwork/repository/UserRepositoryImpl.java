package socialnetwork.repository;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import socialnetwork.model.Friendship;
import socialnetwork.model.Message;
import socialnetwork.model.aggregation.FriendshipsMax;
import socialnetwork.model.aggregation.MessagesCount;
import socialnetwork.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private ReactiveMongoOperations reactiveMongoOperations;

    @Override
    public User insert(User user) {
        return mongoOperations.save(user);
    }

    @Override
    public Flux<User> insertAll(Collection<User> users) {
        return reactiveMongoOperations.insertAll(users);
    }

    @Override
    public Flux<User> findAllUsers() {
        return reactiveMongoOperations.findAll(User.class);
    }

    @Override
    public User findUser(String login) {
        Query query = new Query().addCriteria(Criteria.where("login").is(login));
        return mongoOperations.findOne(query, User.class);
    }

    @Override
    public Mono<Message> insertMessage(String login, Message message) {
        Query query = new Query().addCriteria(Criteria.where("login").is(login));
        Update update = new Update().addToSet("messages", message);
        mongoOperations.updateFirst(query, update, User.class);
        return reactiveMongoOperations.save(message);
    }

    @Override
    public Flux<Message> findMessages(String login) {
        Query query = new Query().addCriteria(Criteria.where("author").is(login));
        return reactiveMongoOperations.tail(query, Message.class);
    }

    @Override
    public void insertFriendship(String login, Friendship friendship) {
        Query query = new Query().addCriteria(Criteria.where("login").is(login));
        Update update = new Update().addToSet("friendships", friendship);
        mongoOperations.updateFirst(query, update, User.class);
    }

    @Override
    public void insertMovie(String login, String movie) {
        Query query = new Query().addCriteria(Criteria.where("login").is(login));
        Update update = new Update().addToSet("movies", movie);
        mongoOperations.updateFirst(query, update, User.class);
    }

    @Override
    public List<MessagesCount> getAverageNumberOfMessagesByDayOfWeek() {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("messages").exists(true).not().size(0)),
            Aggregation.unwind("messages"),
            Aggregation.project("login").and("messages.creationDateTime").extractDayOfWeek().as("dayOfWeek"),
            Aggregation.group("login", "dayOfWeek").count().as("msgCount"),
            Aggregation.group( "dayOfWeek").avg("msgCount").as("avgMsgCount"));
        return mongoOperations.aggregate(aggregation, User.class, MessagesCount.class).getMappedResults();
    }

    @Override
    public List<FriendshipsMax> getMaxNewFriendshipByMonth() {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("friendships").exists(true)),
            Aggregation.unwind("friendships"),
            Aggregation.project("login").and("friendships.dateTime").extractMonth().as("month"),
            Aggregation.group("login", "month").count().as("friendCount"),
            Aggregation.group( "month").max("friendCount").as("maxFriendships"));
        return mongoOperations.aggregate(aggregation, User.class, FriendshipsMax.class).getMappedResults();
    }

    @Override
    public int getMinWatchedMovieByUsersWithFriends() {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("friendships").exists(true).and("movies").exists(true)),
            Aggregation.project("movies").and("friendships").size().as("friendCount"),
            Aggregation.match(Criteria.where("friendCount").gt(0)),
            Aggregation.unwind("movies"),
            Aggregation.group("movies").count().as("watched"),
            Aggregation.group().min("watched").as("min"));
        Document document = mongoOperations.aggregate(aggregation, User.class, Document.class).getUniqueMappedResult();
        return Optional.ofNullable(document).map(doc -> doc.getInteger("min")).orElse(0);
    }
}
