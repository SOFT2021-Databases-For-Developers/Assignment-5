package main.java.dk.cphbusiness.mrv.twitterclone.contract;


import main.java.dk.cphbusiness.mrv.twitterclone.dto.UserCreation;
import main.java.dk.cphbusiness.mrv.twitterclone.dto.UserOverview;
import main.java.dk.cphbusiness.mrv.twitterclone.dto.UserUpdate;

import java.util.List;
import java.util.Set;

public interface UserManagement {
    boolean createUser(UserCreation userCreation);
    UserOverview getUserOverview(String username);
    boolean updateUser(UserUpdate userUpdate);
    boolean followUser(String username, String usernameToFollow);
    boolean unfollowUser(String username, String usernameToUnfollow);
    Set<String> getFollowedUsers(String username);
    Set<String> getUsersFollowing(String username);
}

