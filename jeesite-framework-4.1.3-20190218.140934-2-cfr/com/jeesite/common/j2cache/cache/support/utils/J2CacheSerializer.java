/*	
 * Decompiled with CFR 0.139.	
 * 	
 * Could not load the following classes:	
 *  net.oschina.j2cache.util.SerializationUtils	
 *  org.springframework.data.redis.serializer.RedisSerializer	
 *  org.springframework.data.redis.serializer.SerializationException	
 */	
package com.jeesite.common.j2cache.cache.support.utils;	
	
import java.io.IOException;	
import net.oschina.j2cache.util.SerializationUtils;	
import org.springframework.data.redis.serializer.RedisSerializer;	
import org.springframework.data.redis.serializer.SerializationException;	
	
public class J2CacheSerializer	
implements RedisSerializer<Object> {	
    public byte[] serialize(Object t) throws SerializationException {	
        try {	
            return SerializationUtils.serialize((Object)t);	
        }	
        catch (IOException a2) {	
            a2.printStackTrace();	
            return null;	
        }	
    }	
	
    public Object deserialize(byte[] bytes) throws SerializationException {	
        try {	
            return SerializationUtils.deserialize((byte[])bytes);	
        }	
        catch (IOException a2) {	
            a2.printStackTrace();	
            return null;	
        }	
    }	
}	
	
