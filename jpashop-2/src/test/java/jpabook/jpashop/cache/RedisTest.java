package jpabook.jpashop.cache;

import jpabook.jpashop.cache.domain.Point;
import jpabook.jpashop.cache.domain.PointRedisRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RedisTest {

	@Autowired
	private PointRedisRepository pointRedisRepository;

	@AfterEach
	public void tearDown() {
		pointRedisRepository.deleteAll();
	}

	@Test
	@DisplayName("기본 등록, 조회 기능")
	public void createAndRead() {
		//given
		String id = "zeeyo";
		LocalDateTime refreshTime = LocalDateTime.of(2020, 3, 24, 15, 10);
		Point point = Point.builder()
				.id(id)
				.amount(1000L)
				.refreshTime(refreshTime)
				.build();

		//when
		pointRedisRepository.save(point);

		//then
		Point savedPoint = pointRedisRepository.findById(id).get();
		assertThat(savedPoint.getAmount()).isEqualTo(1000L);
		assertThat(savedPoint.getRefreshTime()).isEqualTo(refreshTime);
	}

	@Test
	@DisplayName("기본 수정 기능")
	public void update() {
		//given
		String id = "zeeyo";
		LocalDateTime refreshTime = LocalDateTime.of(2020, 3, 24, 15, 20);
		Point point = Point.builder()
				.id(id)
				.amount(1000L)
				.refreshTime(refreshTime)
				.build();

		pointRedisRepository.save(point);

		//when
		Point savedPoint = pointRedisRepository.findById(id).get();
		savedPoint.refresh(2000L, LocalDateTime.of(2020, 3, 24, 15, 30));
		pointRedisRepository.save(savedPoint);

		//then
		Point refreshPoint = pointRedisRepository.findById(id).get();
		assertThat(refreshPoint.getAmount()).isEqualTo(2000L);
	}
}