package fixture;

import com.gym.modulecore.core.post.model.entity.PostEntity;
import com.gym.modulecore.core.user.model.entity.UserEntity;

public class PostEntityFixture {

    public static PostEntity get(String userName, Long postId, Long userId) {
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setUserName(userName);

        PostEntity result = new PostEntity();
        result.setUser(user);
        result.setId(postId);
        return result;
    }
}
