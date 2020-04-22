# Lock

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

## Pessimistic Lock과 Optimistic Lock
- Optimistic Lock은 엔티티의 버전 프로퍼티 '값을 체크'하는 반면, Pessimistic Lock은 데이터베이스 레벨에서 엔티티를 락 거는 방식이다.  
- Optimistic Lock은 데이터의 수정, 삭제가 빈번하게 일어나지 않은 경우에 적절하다.  
- Pessimistic Lock은 Optimistic Lock보다 데이터 무결성을 보장하지만 성능에 문제가 있을 수 있다.