package io.choerodon.app.service;

import io.choerodon.api.dto.BaseUserDTO;
import io.choerodon.domain.BaseUser;
import io.choerodon.domain.LoginVM;
import org.springframework.cache.annotation.Cacheable;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

/*
 * @program: springboot
 * @author: syun
 * @create: 2019-09-16 17:26
 */
public interface AuthenticationService {

    boolean sendCode(String phone);

    boolean sendChangePWDCode(String phone);

    Boolean sendPwdChange(String username);

    Boolean register(BaseUserDTO user, String code);

    /**
     * 用户登陆
     * @param loginVM
     * @return 访问凭证
     */
    Map<String, Object> auth(LoginVM loginVM);

    /**
     * 刷新凭证
     * @param username
     * @param token
     * @return
     */
    Map<String, Object> refreshToken(String username, String token);

    /**
     * 检测用户是否存在
     * @param username
     * @return
     */
    Boolean isUsernameExist(String username);

    Boolean createUser(BaseUserDTO userDTO);

    boolean deleteUser(Integer id);

    boolean deleteUser(List<Integer> ids);

    /**
     * 通过验证码修改密码
     * @param username
     * @param newPassword
     * @param code
     * @return
     */
    boolean changePassword(String username, String newPassword, String code);

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    boolean updateUser(BaseUser user);

    /**
     * 通过用户名获取用户信息
     * @param username
     * @return
     */
    BaseUser obtainUser(String username);

    /**
     * 通过 username 获取用户基本信息以及身份信息
     */
    BaseUser obtainUserByUsername(String username);
}
