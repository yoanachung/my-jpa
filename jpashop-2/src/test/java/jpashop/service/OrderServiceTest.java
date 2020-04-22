package jpashop.service;

import jpashop.domain.Address;
import jpashop.domain.Member;
import jpashop.domain.Order;
import jpashop.domain.OrderStatus;
import jpashop.domain.item.Book;
import jpashop.exception.NotEnoughStockException;
import jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember(String name, String city, String street, String zipcode) {
        Member member = new Member();
        member.setName(name);
        member.setAddress(new Address(city, street, zipcode));
        em.persist(member);
        return member;
    }

    @Test
    public void 상품주문() throws Exception {
        //given
        Member member = createMember("회원1", "서울", "가", "123");
        Book book = createBook("책1", 1000, 10);

        //when
        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.ORDER, getOrder.getStatus(), "상품 주문시 상태는 ORDER");
        assertEquals(1, getOrder.getOrderItems().size(), 1, "주문한 상품 종류 수가 일치해야 한다");
        assertEquals(1000 * orderCount, getOrder.getTotalPrice(), "주문 가격은 가격 * 수량이다");
        assertEquals(8, book.getStockQuantity(), "주문 수량만큼 아이템의 재고가 줄어들어야 한다");
    }

    @Test
    public void 주문취소() throws Exception {
        //given
        Member member = createMember("회원1", "서울", "가", "123");
        Book book = createBook("책1", 1000, 10);

        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //when
        orderService.cancalOrder(orderId);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.CANCEL, getOrder.getStatus(), "주문 취소시 상태는 CANCEL이다");
        assertEquals(book.getStockQuantity(), 10, "주문 취소시 주문 수량만큼 아이템의 재고가 늘어나야 한다");
    }

    @Test
    public void 상품주문_재고수량초과() throws Exception {
        //given
        Member member = createMember("회원1", "서울", "가", "123");
        Book book = createBook("책1", 1000, 10);

        //when
        int orderCount = 12;

        //then
        assertThrows(NotEnoughStockException.class, () -> {orderService.order(member.getId(), book.getId(), orderCount);});
    }
}