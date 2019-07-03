package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello"); //애플리케이션 로딩 시점에 딱 하나만 생성
        EntityManager em = emf.createEntityManager(); //트랜잭션 단위로 생성
        EntityTransaction tx = em.getTransaction(); //데이터를 변경하는 모든 작업은 트랜잭션 안에서 일어나야 한다
        tx.begin();

        try {
            // 생성
//            Member member = new Member();
//            member.setId(3L);
//            member.setName("Hello A");
//            em.persist(member); // 영속

            // 조회
//            Member findMember = em.find(Member.class, 3L);
//            Member findMember2 = em.find(Member.class, 3L);

//            System.out.println(findMember == findMember2);

            // 수정
//            findMember.setName("Hello BABO");

            // 검색
//            List<Member> result = em.createQuery("select m from Member as m", Member.class) // 대상이 테이블이 아닌 객체
//                    .setFirstResult(1)
//                    .setMaxResults(10)
//                    .getResultList();
//
//            for (Member member : result) {
//                System.out.println("..." + member.getName());
//            }


            // flush (영속성 컨텍스트를 비우는 게 아니라, 디비와 동기화 하는 것)
//            Member member = new Member(10L, "member10");
//            em.persist(member);
//            em.flush(); // sql 실행됨
//            System.out.println("===============");


            // detach
//            Member member = em.find(Member.class, 1L);
//            member.setName("CCC");
//
//            em.detach(member);
//
//            Member member2 = em.find(Member.class, 1L);

            // getReference
//            Member member = new Member();
//            member.setUsername("m1");
//            em.persist(member);
//
//            em.flush();
//            em.clear();
//
//            Member ref = em.getReference(Member.class, member.getId());
////            Member m1 = em.find(Member.class, member.getId());
////            System.out.println(m1.getClass()); //proxy 객체
////            System.out.println(m1 == ref); // 한 트랜잭션 안에서, 같은 id라면 무조건 같아야 함
//
//            System.out.println(emf.getPersistenceUnitUtil().isLoaded(ref)); // find m1을 했기때문에 true


            // 지연로딩
//            Team team = new Team();
//            team.setName("t2");
//            em.persist(team); // CASCADE 안 되어 있기 때문에 따로 저장
//
//            Member member1 = new Member();
//            member1.setUsername("m2");
//            member1.setTeam(team);
//            em.persist(member1);
//
//            em.flush();
//            em.clear();
//
//            Member m = em.find(Member.class, member1.getId());
//            System.out.println(m.getTeam().getName());

            // 값 타입
//            Address address = new Address("c", "s", "z");
//
//            Member member = new Member();
//            member.setUsername("member1");
//            member.setAddress(address);
//            em.persist(member);
//
//            Member member2 = new Member();
//            member2.setUsername("member2");
//            member2.setAddress(address);
//            em.persist(member2);
//
//            Member m = em.find(Member.class, member.getId());


            // 값 타입 비교
//            Address address1 = new Address("c", "s", "z");
//            Address address2 = new Address("c", "s", "z");
//
//            System.out.println(address1 == address2); // false
//            System.out.println(address1.equals(address2));


            // 값 타입 컬렉션
//            Member member = new Member();
//            member.setUsername("MEMBER 호잇~");
//            member.setAddress(new Address("city", "street", "zipcode"));
//
//            member.getFavoriteFoods().add("치킨");
//            member.getFavoriteFoods().add("족발");
//            member.getFavoriteFoods().add("막창");
//
//            member.getAddressHistory().add(new Address("city1", "street1", "zipcode1"));
//            member.getAddressHistory().add(new Address("city2", "street2", "zipcode2"));
//            member.getAddressHistory().add(new Address("city3", "street3", "zipcode3"));
//
//            em.persist(member);
//
//            em.flush();
//            em.clear();
//
//            System.out.println("======================");
//            Member findMember = em.find(Member.class, member.getId()); // 값 컬렉션을 가져오는 쿼리가 아님. 컬렉션들 지연 로딩.
//
//            System.out.println("======================");
//            List<Address> addressHistory = findMember.getAddressHistory();
//            for (Address a : addressHistory) {
//                System.out.println(a.getCity()); // 이때 컬렉션 가져오는 쿼리 실행~
//            }
//
//            // 값 타입 컬렉션의 수정: 컬렉션에서 아예 없애고 새로운 값을 추가해야지, 수정하면 안 됨
//            addressHistory.remove(1);
//            addressHistory.add(new Address("test", "test", "test"));
//
//            // equals를 재정의했다면 test 사라질 것. 근데 어드레스 다 삭제하고 다 새로 집어넣는 쿼리...
//            findMember.getAddressHistory().remove(new Address("test", "test", "test"));

            // 값 컬렉션 대체
            Member member = new Member();
            member.setUsername("HELL WORLD");

            member.getAddressHistory().add(new AddressEntity("c1", "s1", "z1"));
            member.getAddressHistory().add(new AddressEntity("c2", "s2", "z2"));

            em.persist(member);


            tx.commit(); // 디비에 저장
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();
    }
}
