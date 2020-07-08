package socialnetwork.model.aggregation;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.Month;

@Data
public class FriendshipsMax {
    @Id
    private Month month;
    private int maxFriendships;
}
