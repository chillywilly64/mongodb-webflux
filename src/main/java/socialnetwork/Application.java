package socialnetwork;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import socialnetwork.converter.IntToDayOfWeekConverter;
import socialnetwork.converter.IntToMonthConverter;
import socialnetwork.model.Message;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@SpringBootApplication
@EnableReactiveMongoRepositories
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    private MongoOperations mongoOperations;

    @PostConstruct
    public void createCollection() {
        mongoOperations.createCollection(Message.class, CollectionOptions.empty().size(1000).capped());
    }

    @Bean
    @Primary
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(Arrays.asList(new IntToDayOfWeekConverter(), new IntToMonthConverter()));
    }
}
