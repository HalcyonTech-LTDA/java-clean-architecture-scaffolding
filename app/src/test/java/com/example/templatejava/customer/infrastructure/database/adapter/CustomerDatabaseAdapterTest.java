package com.example.templatejava.customer.infrastructure.database.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.templatejava.customer.domain.model.Customer;
import com.example.templatejava.customer.domain.model.Customer.CustomerStatus;
import com.example.templatejava.customer.infrastructure.database.entity.CustomerMongoEntity;
import com.example.templatejava.customer.infrastructure.database.mapper.CustomerDatabaseMapper;
import com.example.templatejava.customer.infrastructure.database.repository.SpringDataCustomerRepository;
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

class CustomerDatabaseAdapterTest {

    private FakeSpringDataCustomerRepository repository;
    private CustomerDatabaseMapper mapper;
    private CustomerDatabaseAdapter adapter;

    @BeforeEach
    void setUp() {
        repository = new FakeSpringDataCustomerRepository();
        mapper = new CustomerDatabaseMapper();
        adapter = new CustomerDatabaseAdapter(repository, mapper);
    }

    @Test
    @DisplayName("should save customer and return domain object")
    void shouldSaveCustomer() {
        Customer customer = Customer.create("c-1", "John", "john@example.com", Instant.now());

        Customer saved = adapter.save(customer);

        assertThat(saved.getId()).isEqualTo("c-1");
        assertThat(saved.getName()).isEqualTo("John");
        assertThat(repository.findById("c-1")).isPresent();
    }

    @Test
    @DisplayName("should find customer by id and return domain object")
    void shouldFindById() {
        Customer customer = Customer.create("c-2", "Jane", "jane@example.com", Instant.now());
        adapter.save(customer);

        Optional<Customer> found = adapter.findById("c-2");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Jane");
    }

    @Test
    @DisplayName("should find customer by email and return domain object")
    void shouldFindByEmail() {
        Customer customer = Customer.create("c-3", "Bob", "bob@example.com", Instant.now());
        adapter.save(customer);

        Optional<Customer> found = adapter.findByEmail("bob@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Bob");
    }

    @Test
    @DisplayName("should find customers by status and return domain objects")
    void shouldFindByStatus() {
        Customer customer =
                Customer.create("c-4", "Status User", "status@example.com", Instant.now());
        adapter.save(customer);

        List<Customer> found = adapter.findByStatus(CustomerStatus.PENDING_BUREAU_ENRICHMENT);

        assertThat(found).hasSize(1);
        assertThat(found.get(0).getId()).isEqualTo("c-4");
    }

    @Test
    @DisplayName("should throw NullPointerException when constructor argument is null")
    void shouldThrowWhenConstructorArgIsNull() {
        assertThatThrownBy(() -> new CustomerDatabaseAdapter(null, mapper))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("springDataRepository must not be null");

        assertThatThrownBy(() -> new CustomerDatabaseAdapter(repository, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("customerDatabaseMapper must not be null");
    }

    private static class FakeSpringDataCustomerRepository implements SpringDataCustomerRepository {
        private final List<CustomerMongoEntity> storage = new ArrayList<>();

        @Override
        public <S extends CustomerMongoEntity> S save(S entity) {
            storage.removeIf(e -> e.getId().equals(entity.getId()));
            storage.add(entity);
            return entity;
        }

        @Override
        public Optional<CustomerMongoEntity> findById(String id) {
            return storage.stream().filter(e -> e.getId().equals(id)).findFirst();
        }

        @Override
        public Optional<CustomerMongoEntity> findByEmail(String email) {
            return storage.stream().filter(e -> e.getEmail().equals(email)).findFirst();
        }

        @Override
        public List<CustomerMongoEntity> findByStatus(String status) {
            return storage.stream().filter(e -> e.getStatus().equals(status)).toList();
        }

        @Override
        public <S extends CustomerMongoEntity> List<S> saveAll(Iterable<S> entities) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<CustomerMongoEntity> findAll() {
            return new ArrayList<>(storage);
        }

        @Override
        public List<CustomerMongoEntity> findAllById(Iterable<String> strings) {
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
        public void delete(CustomerMongoEntity entity) {
            storage.remove(entity);
        }

        @Override
        public void deleteAllById(Iterable<? extends String> strings) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void deleteAll(Iterable<? extends CustomerMongoEntity> entities) {
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
        public List<CustomerMongoEntity> findAll(Sort sort) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Page<CustomerMongoEntity> findAll(Pageable pageable) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <S extends CustomerMongoEntity> S insert(S entity) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <S extends CustomerMongoEntity> List<S> insert(Iterable<S> entities) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <S extends CustomerMongoEntity> Optional<S> findOne(Example<S> example) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <S extends CustomerMongoEntity> List<S> findAll(Example<S> example) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <S extends CustomerMongoEntity> List<S> findAll(Example<S> example, Sort sort) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <S extends CustomerMongoEntity> Page<S> findAll(
                Example<S> example, Pageable pageable) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <S extends CustomerMongoEntity> long count(Example<S> example) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <S extends CustomerMongoEntity> boolean exists(Example<S> example) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <S extends CustomerMongoEntity, R> R findBy(
                Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
            throw new UnsupportedOperationException();
        }
    }
}
