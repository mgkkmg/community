package fixture;

import com.gym.modulecore.core.user.model.entity.UserEntity;

public class UserEntityFixture {

    public static UserEntity get(String userName, String password, Long userId) {
        UserEntity result = new UserEntity();
        result.setId(userId);
        result.setUserName(userName);
        result.setPassword(password);

        return result;
    }
}
