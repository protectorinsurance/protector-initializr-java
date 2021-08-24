package no.protector.initializr.web.configuration.security;

import org.ehcache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

public class EhUserCache implements UserCache, InitializingBean {
    private static final Logger LOG = LoggerFactory.getLogger(EhUserCache.class);

    private Cache<String, UserDetails> cache;

    public EhUserCache(Cache<String, UserDetails> cache) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("User cache initialized.");
        }
        this.cache = cache;
    }

    public Cache<String, UserDetails> getCache() {
        return cache;
    }

    public void setCache(Cache<String, UserDetails> cache) {
        this.cache = cache;
    }

    @Override
    public UserDetails getUserFromCache(String username) {
        UserDetails userDetails = cache.get(username);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Cache hit: {}; username: {}", (userDetails != null), username);
        }
        return userDetails;
    }

    @Override
    public void putUserInCache(UserDetails user) {
        var key = user.getPassword();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Cache put: {}", key);
        }
        cache.put(key, user);
    }

    @Override
    public void removeUserFromCache(String username) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Cache remove: {}", username);
        }
        cache.remove(username);
    }

    @Override
    public void afterPropertiesSet()
    {
        Assert.notNull(cache, "cache mandatory");
    }
}
