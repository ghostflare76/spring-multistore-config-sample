package com.gmind7.bakery.config;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.gmind7.bakery.config.handler.CacheConfigurationFactory;
import com.google.code.ssm.config.AddressProvider;
import com.google.code.ssm.config.DefaultAddressProvider;
import com.google.code.ssm.providers.CacheClientFactory;
import com.google.code.ssm.providers.CacheConfiguration;
import com.google.code.ssm.providers.spymemcached.MemcacheClientFactoryImpl;
import com.google.code.ssm.spring.SSMCache;
import com.google.code.ssm.spring.SSMCacheManager;

@Configuration
@EnableCaching
public class SSMSpringCacheConfig {
	
	@Inject
	private Environment environment;
	
	@Autowired
	private CacheConfigurationFactory[] cacheConfigurationFactory;
	
	@Bean(name="cacheManager")
	public SSMCacheManager cacheManager() throws Exception{
		
		SSMCacheManager ssmCacheManager = new SSMCacheManager();
		
		Set<SSMCache> cachePool = new HashSet<SSMCache>();
		
		for (CacheConfigurationFactory cacheFactory : this.cacheConfigurationFactory) {
			Set<SSMCache> cacheMap = cacheFactory.ssmCaches();
			Iterator<SSMCache> iterator = cacheMap.iterator();
			while(iterator.hasNext()){
				cachePool.add((SSMCache)iterator.next());
			}
        }
		ssmCacheManager.setCaches(cachePool);
		
		return ssmCacheManager;
	}
	
	@Bean
	public CacheClientFactory cacheClientFactory(){
		return new MemcacheClientFactoryImpl();
	}
	
	@Bean
	public AddressProvider addressProvider(){
		DefaultAddressProvider defaultAddressProvider = new DefaultAddressProvider();
		defaultAddressProvider.setAddress(environment.getRequiredProperty("memcached.addresses"));
		return defaultAddressProvider; 
	}
	
	@Bean
	public CacheConfiguration configuration(){
		CacheConfiguration cacheConfiguration = new CacheConfiguration();
		cacheConfiguration.setConsistentHashing(true);
		return cacheConfiguration;
	}
}