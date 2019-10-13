package com.salil.shawrtn.repository;

import com.salil.shawrtn.config.UrlCacheConfig;
import com.salil.shawrtn.domainobject.UrlDO;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlRepository extends MongoRepository<UrlDO, String> {

    @Cacheable(value = UrlCacheConfig.CACHE_SHAWRTN_ME)
    UrlDO findByKey(Long key);

    UrlDO findByLongUrl(String url);

    @CachePut(value = UrlCacheConfig.CACHE_SHAWRTN_ME, key = "#url.keyCode")
    UrlDO save(UrlDO url);
}
