package cn.nispring.rail12306.service;

import cn.nispring.rail12306.entity.UserEntity;
import cn.nispring.rail12306.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void testSignInOut() {
        when(userMapper.selectByUsername(anyString())).thenReturn(new UserEntity(
                1L,
                "test",
                "$2a$10$FgrLCj8oKJUkSHoa1Rrm6O5W850Gnv5xkguq030JdS5g69NL4n5i2",
                null,
                null,
                null
        ));

        UserEntity entity = userService.signin("test", "123456");
        assertNotNull(entity);
        String token = entity.getSessionToken();
        assertNotNull(token);

        userService.signout(token);
    }
}
