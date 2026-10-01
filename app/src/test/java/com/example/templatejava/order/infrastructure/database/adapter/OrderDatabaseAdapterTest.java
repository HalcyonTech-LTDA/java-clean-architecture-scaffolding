package com.example.templatejava.order.infrastructure.database.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.templatejava.order.domain.model.Order;
import com.example.templatejava.order.domain.model.Order.OrderStatus;
import com.example.templatejava.order.infrastructure.database.entity.OrderMongoEntity;
import com.example.templatejava.order.infrastructure.database.mapper.OrderDatabaseMapper;
import com.example.templatejava.order.infrastructure.database.repository.SpringDataOrderRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;

class OrderDatabaseAdapterTest {

    private FakeSpringDataOrderRepository repository;
    private OrderDatabaseMapper mapper;
    private OrderDatabaseAdapter adapter;

    @BeforeEach
    void setUp() {
        repository = new FakeSpringDataOrderRepository();
        mapper = new OrderDatabaseMapper();
        adapter = new OrderDatabaseAdapter(repository, mapper);
    }

    @Test
    @DisplayName("should save order and return domain object")
    void shouldSaveOrder() {
        Order order = Order.create("ord-1", "c-1", new BigDecimal("100.00"), Instant.now());

        Order saved = adapter.save(order);

        assertThat(saved.getId()).isEqualTo("ord-1");
        assertThat(repository.findById("ord-1")).isPresent();
    }

    @Test
    @DisplayName("should find order by id and return domain object")
    void shouldFindById() {
        Order order = Order.create("ord-2", "c-1", new BigDecimal("75.00"), Instant.now());
        adapter.save(order);

        Optional<Order> found = adapter.findById("ord-2");

        assertThat(found).isPresent();
        assertThat(found.get().getCustomerId()).isEqualTo("c-1");
    }

    @Test
    @DisplayName("should find orders by status and created before threshold")
    void shouldFindCreatedBefore() {
        Instant past = Instant.now().minusSeconds(100);
        Order order = Order.create("ord-3", "c-1", new BigDecimal("25.00"), past);
        adapter.save(order);

        List<Order> found = adapter.findCreatedBefore(OrderStatus.CREATED, Instant.now());

        assertThat(found).hasSize(1);
        assertThat(found.get(0).getId()).isEqualTo("ord-3");
    }

    @Test
    @DisplayName("should throw NullPointerException when constructor argument is null")
    void shouldThrowWhenConstructorArgIsNull() {
        assertThatThrownBy(() -> new OrderDatabaseAdapter(null, mapper))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("springDataOrderRepository must not be null");

        assertThatThrownBy(() -> new OrderDatabaseAdapter(repository, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("orderDatabaseMapper must not be null");
    }

    private static class FakeSpringDataOrderRepository implements SpringDataOrderRepository {
        private final List<OrderMongoEntity> storage = new ArrayList<>();

        @Override
        public <S extends OrderMongoEntity> S save(S entity) {
            storage.removeIf(e -> e.getId().equals(entity.getId()));
            storage.add(entity);
            return entity;
        }

        @Override
        public Optional<OrderMongoEntity> findById(String id) {
            return storage.stream().filter(e -> e.getId().equals(id)).findFirst();
        }

        @Override
        public List<OrderMongoEntity> findByStatusAndCreatedAtBefore(
                String status, Instant threshold) {
            return storage.stream()
                    .filter(
                            e ->
                                    e.getStatus().equals(status)
                                            && e.getCreatedAt().isBefore(threshold))
                    .toList();
        }

        @Override
        public <S extends OrderMongoEntity> List<S> saveAll(Iterable<S> entities) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<OrderMongoEntity> findAll() {
            return new ArrayList<>(storage);
        }

        @Override
        public List<OrderMongoEntity> findAllById(Iterable<String> strings) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long count() {
            return storage.size();
        }

        @Override
        public void deleteById(String s) {
            storage.removeIf(e -> e.getId().equals(s));
        }

        @Override
        public void delete(OrderMongoEntity entity) {
            storage.remove(entity);
        }

        @Override
        public void deleteAllById(Iterable<? extends String> strings) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void deleteAll(Iterable<? extends OrderMongoEntity> entities) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void deleteAll() {
            storage.clear();
        }

        @Override
        public boolean existsById(String s) {
            return storage.stream().anyMatch(e -> e.getId().equals(s));
        }

        @Override
        public List<OrderMongoEntity> findAll(Sort sort) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Page<OrderMongoEntity> findAll(Pageable pageable) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <S extends OrderMongoEntity> S insert(S entity) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <S extends OrderMongoEntity> List<S> insert(Iterable<S> entities) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <S extends OrderMongoEntity> Optional<S> findOne(Example<S> example) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <S extends OrderMongoEntity> List<S> findAll(Example<S> example) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <S extends OrderMongoEntity> List<S> findAll(Example<S> example, Sort sort) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <S extends OrderMongoEntity> Page<S> findAll(Example<S> example, Pageable pageable) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <S extends OrderMongoEntity> long count(Example<S> example) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <S extends OrderMongoEntity> boolean exists(Example<S> example) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <S extends OrderMongoEntity, R> R findBy(
                Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
            throw new UnsupportedOperationException();
        }
    }
}
