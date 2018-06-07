package com.hand.bdss.web.common.util;

import com.hand.bdss.web.common.vo.ConfigInfo;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


public class GetRedisConnUtils {
	public static final String URL = "10.211.55.138";
	public static final int POST = 6379;
	
	private static JedisPool pool = null;  
	  
    /** 
     * 构建redis连接池 
     *  
     * @param ip 
     * @param port 
     * @return JedisPool 
     */  
    public static JedisPool getPool() {  
        if (pool == null) {  
            JedisPoolConfig config = new JedisPoolConfig();  
            //控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；  
            //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。  
            config.setMaxTotal(50);  
              
            //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。  
            config.setMaxIdle(5);  
              
            //表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；  
            config.setMaxWaitMillis(1000 * 100);  
              
            //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；  
            config.setTestOnBorrow(true);  
            pool = new JedisPool(config, URL, POST);  
        }  
        return pool;  
    }  
  
    /** 
     * 返还到连接池 
     *  
     * @param pool  
     * @param redis 
     */  
   
	@SuppressWarnings("deprecation")
	public static void returnResource(JedisPool pool, Jedis redis) {  
        if (redis != null) {  
            //返回链接池  
            pool.returnResource(redis);  
        }  
    }  
  
    /** 
     * 添加数据 
     *  
     * @param configInfo 
     * @return 
     */ 
	@SuppressWarnings("deprecation")
    public static void set(ConfigInfo configInfo){  
        JedisPool pool = null;  
        Jedis jedis = null;  
        try {  
            //从jedisPool中获取jedis实例  
            pool = getPool();             
            jedis = pool.getResource();  
            jedis.append(configInfo.getCode(), configInfo.getConfigInfo());  
        } catch (Exception e) {  
            //有exception,也要将jedis返回给jedisPool   
            //释放redis对象  
            pool.returnBrokenResource(jedis);  
            e.printStackTrace();  
        } finally {  
            //用完jedis 返还到连接池  
            returnResource(pool, jedis);  
        }  
    }  

}
