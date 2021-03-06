package cui.shibing.freeread.service.impl;

import cui.shibing.freeread.dao.UserDao;
import cui.shibing.freeread.model.User;
import cui.shibing.freeread.model.UserInfo;
import cui.shibing.freeread.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public User getUserByName(String userName) {
        if (!StringUtils.isEmpty(userName)) {
            return userDao.selectByUserName(userName);
        }
        return null;
    }

    @Override
    @Transactional
    public int deleteUserByName(String userName) {
        if (!StringUtils.isEmpty(userName)) {
            return userDao.deleteUserByName(userName);
        }
        return 0;
    }

    @Override
    @Transactional
    public int updateUserByName(User user) {
        if (user != null && !StringUtils.isEmpty(user.getUserName())) {
            return userDao.updateUser(user);
        }
        return 0;
    }

    @Override
    @Transactional
    public boolean registerUser(String userName, String password) {
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
            return false;
        }
        User existUser = userDao.selectByUserName(userName);
        if (existUser != null) {
            return false;
        }
        User user = new User();
        user.setUserName(userName);
        user.setUserPass(BCrypt.hashpw(password, BCrypt.gensalt()));
        user.setUserRole(UserService.DEFAULT_ROLE);
        return userDao.insertUser(user) == 1;
    }

    @Override
    public UserInfo getUserInfo(String userName) {
        if (StringUtils.isEmpty(userName)) {
            return null;
        }
        return userDao.selectUserInfoByUserName(userName);
    }

    @Override
    @Transactional
    public boolean updateUserInfo(String userName, UserInfo userInfo) {
        if (StringUtils.isEmpty(userName) || userInfo == null || StringUtils.isEmpty(userInfo.getUserInfoId()))
            return false;

        return userDao.updateUserInfo(userInfo) == 1;

    }

    @Override
    @Transactional
    public boolean insertUserInfo(UserInfo userInfo) {
        if (userInfo == null) {
            return false;
        }
        return userDao.insertUserInfo(userInfo) == 1;
    }

    @Override
    @Transactional
    public boolean updateUserEmail(String userName, String email) {
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(email)) {
            return false;
        }
        UserInfo userInfo = userDao.selectUserInfoByUserName(userName);
        if (userInfo == null) {
            userInfo = new UserInfo();
            String userInfoId = UUID.randomUUID().toString().replaceAll("-", "");
            userInfo.setUserInfoId(userInfoId);
            userInfo.setUserEmail(email);
            User currentUser = userDao.selectByUserName(userName);
            currentUser.setUserInfoId(userInfoId);
            return userDao.insertUserInfo(userInfo) == 1 && userDao.updateUser(currentUser) == 1;
        } else {
            userInfo.setUserEmail(email);
            return userDao.updateUserInfo(userInfo) == 1;
        }
    }
}
