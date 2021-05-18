package main.java.dk.cphbusiness.mrv.twitterclone.impl;


import main.java.dk.cphbusiness.mrv.twitterclone.contract.UserManagement;
import main.java.dk.cphbusiness.mrv.twitterclone.dto.UserCreation;
import main.java.dk.cphbusiness.mrv.twitterclone.dto.UserOverview;
import main.java.dk.cphbusiness.mrv.twitterclone.dto.UserUpdate;
import redis.clients.jedis.Jedis;

import java.util.*;

public class UserManagementImpl implements UserManagement {

    private Jedis jedis;

    public UserManagementImpl(Jedis jedis) {
        this.jedis = jedis;
    }

    private boolean checkIfExists(String username)
    {
        String redisCheck = jedis.hget("@" + username, "username");
        if(redisCheck == null) {
            return false;
        }
        return true;
    }

    @Override
    public boolean createUser(UserCreation userCreation) {
        if(checkIfExists(userCreation.username))
        {
            return false;
        }
        Map<String, String> user = new HashMap<>();
        user.put("username", userCreation.username);
        user.put("passwordHash", userCreation.passwordHash);
        user.put("firstname", userCreation.firstname);
        user.put("lastname", userCreation.lastname);
        user.put("birthday", userCreation.birthday);
        try{
            jedis.hset("@" + userCreation.username, user);
            return true;
        } catch (Exception e)
        {
        }

        return false;
    }

    @Override
    public UserOverview getUserOverview(String username) {
        if(!checkIfExists(username))
        {
            return null;
        }
        UserOverview userOverview = new UserOverview();
        userOverview.username = username;
        userOverview.firstname = jedis.hget("@"+username, "firstname");
        userOverview.lastname = jedis.hget("@"+username, "lastname");
        userOverview.numFollowers = Math.toIntExact(jedis.llen("followers:" + username));
        userOverview.numFollowing = Math.toIntExact(jedis.llen("following:" + username));
        return userOverview;
    }

    @Override
    public boolean updateUser(UserUpdate userUpdate) {
        if(!checkIfExists(userUpdate.username))
        {
            return false;
        }
        Map<String, String> user = jedis.hgetAll("@"+userUpdate.username);
        if(userUpdate.firstname != null)
            user.replace("firstname", userUpdate.firstname);
        if(userUpdate.lastname != null)
            user.replace("lastname", userUpdate.lastname);
        if(userUpdate.birthday != null)
            user.replace("birthday", userUpdate.birthday);
        try {
            jedis.hset("@" + userUpdate.username, user);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean followUser(String username, String usernameToFollow) {
        if(!checkIfExists(username))
        {
            return false;
        }
        if(!checkIfExists(usernameToFollow))
        {
            return false;
        }
        try {
            jedis.lpush("followers:" + usernameToFollow, username);
            jedis.lpush("following:" + username, usernameToFollow);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean unfollowUser(String username, String usernameToUnfollow) {
        if(!checkIfExists(username))
        {
            return false;
        }
        try {
            jedis.lrem("following:" + username, 1, usernameToUnfollow);
            jedis.lrem("followers:" + usernameToUnfollow, 1, username);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public Set<String> getFollowedUsers(String username) {
        if(!checkIfExists(username))
        {
            return null;
        }
        Long length = jedis.llen("following:" + username);
        List<String> following = jedis.lrange("following:" + username, 0, length - 1);
        Set<String> converted = new HashSet<>(following);
        return converted;
    }

    @Override
    public Set<String> getUsersFollowing(String username) {
        if(!checkIfExists(username))
        {
            return null;
        }
        Long length = jedis.llen("followers:" + username);
        List<String> followed_by = jedis.lrange("followers:" + username, 0, length - 1);
        Set<String> converted = new HashSet<>(followed_by);
        return converted;
    }

}
