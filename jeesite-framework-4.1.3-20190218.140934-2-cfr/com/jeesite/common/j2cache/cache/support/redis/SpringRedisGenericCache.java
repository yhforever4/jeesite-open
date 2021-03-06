/*	
 * Decompiled with CFR 0.139.	
 * 	
 * Could not load the following classes:	
 *  net.oschina.j2cache.Level2Cache	
 *  org.slf4j.Logger	
 *  org.slf4j.LoggerFactory	
 *  org.springframework.dao.DataAccessException	
 *  org.springframework.data.redis.connection.RedisConnection	
 *  org.springframework.data.redis.core.RedisCallback	
 *  org.springframework.data.redis.core.RedisOperations	
 *  org.springframework.data.redis.core.RedisTemplate	
 *  org.springframework.data.redis.core.ValueOperations	
 */	
package com.jeesite.common.j2cache.cache.support.redis;	
	
import java.io.Serializable;	
import java.io.UnsupportedEncodingException;	
import java.util.ArrayList;	
import java.util.Collection;	
import java.util.Iterator;	
import java.util.List;	
import java.util.Map;	
import java.util.Set;	
import java.util.function.BiConsumer;	
import java.util.function.Consumer;	
import java.util.function.Function;	
import java.util.function.IntFunction;	
import java.util.stream.Stream;	
import net.oschina.j2cache.Level2Cache;	
import org.hyperic.sigar.FileSystem;	
import org.hyperic.sigar.pager.PageFetchException;	
import org.slf4j.Logger;	
import org.slf4j.LoggerFactory;	
import org.springframework.dao.DataAccessException;	
import org.springframework.data.redis.connection.RedisConnection;	
import org.springframework.data.redis.core.RedisCallback;	
import org.springframework.data.redis.core.RedisOperations;	
import org.springframework.data.redis.core.RedisTemplate;	
import org.springframework.data.redis.core.ValueOperations;	
	
public class SpringRedisGenericCache	
implements Level2Cache {	
    private static final Logger log = LoggerFactory.getLogger(SpringRedisGenericCache.class);	
    private String namespace;	
    private RedisTemplate<String, Serializable> redisTemplate;	
    private String region;	
	
    public boolean exists(String key) {	
        return (Boolean)this.redisTemplate.execute(redis -> redis.exists(this._key(key)));	
    }	
	
    public List<byte[]> getBytes(Collection<String> keys) {	
        return (List)this.redisTemplate.execute(redis -> {	
            byte[][] a2 = (byte[][])keys.stream().map(k2 -> this._key((String)k2)).toArray(x$0 -> new byte[x$0][]);	
            return redis.mGet(a2);	
        });	
    }	
	
    public SpringRedisGenericCache(String namespace, String region, RedisTemplate<String, Serializable> redisTemplate) {	
        if (region == null || region.isEmpty()) {	
            region = "_";	
        }	
        this.namespace = namespace;	
        this.redisTemplate = redisTemplate;	
        this.region = this.getRegionName(region);	
    }	
	
    public void setBytes(Map<String, byte[]> bytes) {	
        bytes.forEach((k2, v) -> this.setBytes((String)k2, (byte[])v));	
    }	
	
    public void clear() {	
        this.keys().stream().forEach(k2 -> this.redisTemplate.delete(k2));	
    }	
	
    private /* synthetic */ String getRegionName(String region) {	
        if (this.namespace != null && !this.namespace.isEmpty()) {	
            region = new StringBuilder().insert(0, this.namespace).append(":").append(region).toString();	
        }	
        return region;	
    }	
	
    public void setBytes(String key, byte[] bytes) {	
        this.redisTemplate.execute(redis -> {	
            redis.set(this._key(key), bytes);	
            return null;	
        });	
    }	
	
    public /* varargs */ void evict(String ... keys) {	
        int n;	
        String[] arrstring = keys;	
        int n2 = arrstring.length;	
        int n3 = n = 0;	
        while (n3 < n2) {	
            String a2 = arrstring[n];	
            this.redisTemplate.execute(redis -> redis.del((byte[][])new byte[][]{this._key(a2)}));	
            n3 = ++n;	
        }	
    }	
	
    private /* synthetic */ byte[] _key(String key) {	
        try {	
            byte[] a2 = new StringBuilder().insert(0, this.region).append(":").append(key).toString().getBytes("utf-8");	
            return a2;	
        }	
        catch (UnsupportedEncodingException a3) {	
            a3.printStackTrace();	
            byte[] a4 = new StringBuilder().insert(0, this.region).append(":").append(key).toString().getBytes();	
            return a4;	
        }	
    }	
	
    public byte[] getBytes(String key) {	
        return (byte[])this.redisTemplate.execute(redis -> redis.get(this._key(key)));	
    }	
	
    public void setBytes(String key, byte[] bytes, long timeToLiveInSeconds) {	
        if (timeToLiveInSeconds <= 0L) {	
            log.debug(String.format("Invalid timeToLiveInSeconds value : Od , skipped it.", timeToLiveInSeconds));	
            this.setBytes(key, bytes);	
            return;	
        }	
        this.redisTemplate.opsForValue().getOperations().execute(redis -> {	
            redis.setEx(this._key(key), (long)((int)timeToLiveInSeconds), bytes);	
            return null;	
        });	
    }	
	
    public void setBytes(Map<String, byte[]> bytes, long timeToLiveInSeconds) {	
        bytes.forEach((k2, v) -> this.setBytes((String)k2, (byte[])v, timeToLiveInSeconds));	
    }	
	
    public Collection<String> keys() {	
        Iterator iterator;	
        Set a2 = this.redisTemplate.keys((Object)new StringBuilder().insert(0, this.region).append(":*").toString());	
        ArrayList<String> a3 = new ArrayList<String>(a2.size());	
        Iterator iterator2 = iterator = a2.iterator();	
        while (iterator2.hasNext()) {	
            void a4;	
            String string = (String)iterator.next();	
            iterator2 = iterator;	
            a3.add((String)a4);	
        }	
        return a3;	
    }	
}	
	
