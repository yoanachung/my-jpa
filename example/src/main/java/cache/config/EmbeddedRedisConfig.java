package cache.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * 내장 redis 설정
 */
@Profile("local") //profile이 local일때만 활성화
@Configuration
public class EmbeddedRedisConfig {

	@Value("${spring.redis.port}")
	private int port;

	private RedisServer redisServier;

	@PostConstruct
	public void startRedisServer() {
		redisServier = new RedisServer(port);
		redisServier.start();
	}

	@PreDestroy
	public void stopRedisServer() {
		if (redisServier != null) {
			redisServier.stop();
		}
	}
}
