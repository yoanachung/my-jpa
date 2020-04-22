# Lock

## Transaction
- 원자성 Atomicity: 트랜잭션 내에서 실행된 작업들은 모두 성공하든가 모두 실패해야 한다.
- 일관성 Consistency: 일관성 있는 데이터베이스 상태를 유지해야 한다.
- 격리성 Isolation: 동시에 실행되는 트랜잭션들이 서로에게 영향을 미치지 않아야 한다. (ex. 동시에 같은 데이터를 수정하지 못해야 한다.)
- 지속성 Durability: 트랜잭션을 성공적으로 끝내면 그 결과가 기록되어야 한다. 중간에 문제가 발생하면 로그 등을 사용해 복구할 수 있어야 한다.

### 트랜잭션의 격리 수준
- 격리성을 완벽하게 보장하려면 트랜잭션을 차례대로 실행해야 하지만 그러면 처리 성능이 나빠지므로 적절한 격리 수준을 선택해야 한다.
- ANSI 표준은 격리 수준을 `READ UNCOMMITTED`, `READ COMMITTED`, `REPEATABLE READ`, `SERIALIZABLE` 4단계로 정의하였다.
- Spring의 트랜잭션 기본 격리 수준은 Isolation.DEFAULT - 사용 데이터베이스의 기본 격리 수준이다. 그러므로 데이터베이스를 바꿀 때 격리 수준 변화를 주의해야 한다.
- MySQL의 기본 격리 수준은 REPEATABLE READ이다.
- 일부 중요 로직에서 더 높은 격리 수준이 필요하면 데이터베이스 트랜잭션이 제공하는 잠금 기능을 사용한다.

#### 격리 수준의 문제점
- DIRTY READ  
커밋하지 않은 데이터를 읽는다. 트랜잭션2가 DIRTY READ한 데이터를 사용하는데 트랜잭션1이 롤백되면 데이터 정합성에 문제가 생길 수 있다.
- NON-REPEATABLE READ  
반복해서 같은 데이터를 읽을 수 없다. 트랜잭션1이 조회 중인데 트랜잭션2가 데이터를 수정하고 커밋하면 트랜잭션1이 다시 조회했을 때 수정된 데이터가 조회된다.
- PHANTOM READ  
반복해서 조회시 결과 집합이 달라진다. 트랜잭션1이 회원 리스트를 조회 중인데 트랜잭션2가 회원을 하나 추가하면 트랜잭션1이 다시 회원 리스트를 조회했을 때 회원 하나가 추가되어 조회된다.

|격리 수준|DIRTY READ|NON-REPEATABLE READ|PHANTOM READ|
|---|---|---|---|
|READ UNCOMMITTED|  O  |  O  |  O  |
|READ COMMITTED  |     |  O  |  O  |
|REPEATABLE READ |     |     |  O  |
|SERIALIZABLE    |     |     |     |

## Pessimistic Lock
동일한 데이터를 동시에 수정할 가능성이 높다고 비관적으로 보는 잠금

### JPA에서 정의하는 pessimistic lock modes
- `PESSIMISTIC_READ`  
데이터가 update, delete 되지 않는다.
- `PESSIMISTIC_WRITE`  
데이터가 read, update, delete 되지 않는다.
- `PESSIMISTIC_FORCE_INCREMENT`  
데이터는 read, update, delete 되지 않고, 버전엔티티의 버전을 하나 증가시킨다.


### 사용하기
- find(..., ..., LockModeType)
```
entityManager.find(Member.class, memberId, LockModeType.PESSIMISTIC_READ);
```

- Query.setLockMode()
```
Query query = entityManager.createQuery("from Member where memberId = :memberId");
query.setParameter("memberId", memberId);
query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
query.getResultList();
```

- find() 결과에 lock
```
Member member = entityManager.find(Member.class, memberId);
entityManager.lock(member, LockModeType.PESSIMISTIC_WRITE);
```

- spring data jpa
```
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("from Member where memberId = :memberId")
```


## Optimistic Lock
데이터 갱신시 경합이 발생하지 않을 것이라고 낙관적으로 보는 잠금
데이터 수정시 버전이 달라졌으면 `OptmisticLockException`를 발생시키고, 그렇지 않으면 데이터를 수정하며 버전을 증가시킨다.

### JPA에서 정의하는 optimistic lock modes
- `OPTIMISTIC(READ)`  
버전 프로퍼티를 갖는 모든 엔티티에 read 락을 건다. 더티체킹 및 non-repeatable read가 일어나지 않는다.
- `OPTIMISTIC_FORCE_INCREMENT(WRITE)`  
버전 프로퍼티를 갖는 모든 엔티티에 optimistic 락을 걸고, 버전엔티티의 버전을 하나 증가시킨다. 
엔티티를 수정하지 않아도 트랜잭션을 커밋할 때 update 쿼리를 사용해 버전 정보를 강제로 증가시킨다. 엔티티를 수정하면 수정시 버전 update가 발생해 총 2번의 버전 증가가 나타날 수 있다.

### 사용하기
- 엔티티는 @Version 프로퍼티 한 개를 가진다.
- 버전 프로퍼티의 데이터 타입은 int, Integer, long, Long, short, Short, TimeStamp이어야 한다.
```
@Entity
public class Member {

    @Id
    private Long id;
    
    @Version
    private Integer version;
}
```

- find(..., ..., LockModeType)
```
entityManager.find(Member.class, memberId, LockModeType.OPTIMISTIC);
```

- Query.setLockMode()
```
Query query = entityManager.createQuery("from Member where memberId = :memberId");
query.setParameter("memberId", memberId);
query.setLockMode(LockModeType.OPTIMISTIC_INCREMENT);
query.getResultList();
```

- find() 결과에 lock
```
Member member = entityManager.find(Member.class, memberId);
entityManager.lock(member, LockModeType.OPTIMISTIC);
```

### 주의사항
- 벌크연산은 버전을 무시하므로 버전필드를 강제로 증가시켜야 한다.
```
update Member m set m.name = 'updated', m.version = m.version + 1
```

## Pessimistic Lock과 Optimistic Lock
- Optimistic Lock은 엔티티의 버전 프로퍼티 값을 체크-데이터베이스가 제공하는 락 기능이 아니라 JPA가 제공하는 버전 기능이다-하는 반면, 
Pessimistic Lock은 데이터베이스 레벨에서 엔티티를 락 거는 방식이다.  
- Optimistic Lock은 데이터의 수정, 삭제가 빈번하게 일어나지 않은 경우에 적절하다.  
- Pessimistic Lock은 Optimistic Lock보다 데이터 무결성을 보장하지만 성능에 문제가 있을 수 있다.