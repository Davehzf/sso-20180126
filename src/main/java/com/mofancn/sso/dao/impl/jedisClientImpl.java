package com.mofancn.sso.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.mofancn.sso.dao.jedisClient;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class jedisClientImpl implements jedisClient {

	@Autowired
	private JedisPool JedisPool;

	@Override
	public String get(String key) {
		// TODO Auto-generated method stub
		Jedis jedis = JedisPool.getResource();
		String result = jedis.get(key);
		jedis.close();
		return result;
	}

	@Override
	public String set(String key, String value) {
		// TODO Auto-generated method stub
		Jedis jedis = JedisPool.getResource();
		String string = jedis.set(key, value);
		jedis.close();
		return string;
	}

	@Override
	public String hget(String hkey, String key) {
		// TODO Auto-generated method stub
		Jedis jedis = JedisPool.getResource();
		String string = jedis.hget(hkey, key);
		jedis.close();
		return string;
	}

	@Override
	public long hset(String hkey, String key, String value) {
		// TODO Auto-generated method stub
		Jedis jedis = JedisPool.getResource();
		long hset = jedis.hset(hkey, key, value);
		jedis.close();
		return hset;
	}

	@Override
	public long expire(String key, int value) {
		// TODO Auto-generated method stub
		Jedis jedis = JedisPool.getResource();
		long expire = jedis.expire(key, value);
		jedis.close();
		return expire;
	}

	@Override
	public long incr(String key) {
		// TODO Auto-generated method stub
		Jedis jedis = JedisPool.getResource();
		long incr = jedis.incr(key);
		jedis.close();
		return incr;
	}

	@Override
	public long ttl(String key) {
		// TODO Auto-generated method stub
		Jedis jedis = JedisPool.getResource();
		long ttl = jedis.ttl(key);
		jedis.close();
		return ttl;
	}

	@Override
	public long del(String key) {
		// TODO Auto-generated method stub
		Jedis jedis = JedisPool.getResource();
		long result = jedis.del(key);
		jedis.close();
		return result;
	}

	@Override
	public long hdel(String hkey, String key) {
		// TODO Auto-generated method stub
		Jedis jedis = JedisPool.getResource();
		long result = jedis.hdel(hkey, key);
		jedis.close();
		return result;
	}

}
