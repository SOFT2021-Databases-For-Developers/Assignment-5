package main.java.dk.cphbusiness.mrv.twitterclone.impl;


import main.java.dk.cphbusiness.mrv.twitterclone.contract.PostManagement;
import main.java.dk.cphbusiness.mrv.twitterclone.dto.Post;
import main.java.dk.cphbusiness.mrv.twitterclone.util.Time;
import redis.clients.jedis.Jedis;

import java.util.*;

public class PostManagementImpl implements PostManagement {
    private Jedis jedis;
    private Time time;

    public PostManagementImpl(Jedis jedis, Time time) {
        this.jedis = jedis;
        this.time = time;
    }

    private boolean checkIfExists(String username)
    {
        String redisCheck = jedis.hget("@" + username, "username");
        if(redisCheck == null)
        {
            return false;
        }
        return true;
    }

    @Override
    public boolean createPost(String username, String message) {
        if(!checkIfExists(username))
        {
            return false;
        }
        Map<String, String> post = new HashMap<>();
        post.put("" + time.getCurrentTimeMillis(), message);
        try{
            jedis.hset("post:" + username, post);
            return true;
        } catch (Exception e)
        {

        }
        return false;
    }

    @Override
    public List<Post> getPosts(String username) {
        if(!checkIfExists(username))
        {
            return null;
        }
        Map<String, String> posts = jedis.hgetAll("post:" + username);
        ArrayList<Post> list = new ArrayList<>();
        for(var  i: posts.entrySet())
        {
            list.add(new Post(Long.parseLong(i.getKey()), i.getValue()));
        }
        return list;
    }

    @Override
    public List<Post> getPostsBetween(String username, long timeFrom, long timeTo) {
        if(!checkIfExists(username))
        {
            return null;
        }
        Map<String, String> posts = jedis.hgetAll("post:" + username);
        ArrayList<Post> list = new ArrayList<>();
        for(var  i: posts.entrySet())
        {
            Long t = Long.parseLong(i.getKey());
            if(t >=timeFrom && t <= timeTo)
            {
                list.add(new Post(t, i.getValue()));
            }
        }
        return list;
    }
}
