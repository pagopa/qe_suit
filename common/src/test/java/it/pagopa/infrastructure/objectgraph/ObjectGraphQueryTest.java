package it.pagopa.infrastructure.objectgraph;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ObjectGraphQueryTest {
    private ObjectGraphDecomposer decomposer;

    @BeforeEach
    void setUp() {
        decomposer = new DefaultObjectGraphDecomposer(new JacksonObjectDecomposer(new ObjectMapper()));
    }

    @Test
    void rootQueryResolvesRootNode() {
        ObjectGraph graph = decomposer.decompose(new CustomerPayload());
        assertTrue(graph.find(ObjectGraphQuery.root()).isRoot());
    }

    @Test
    void propertyThenIndexThenPropertyResolvesNode() throws Exception {
        ObjectGraph graph = decomposer.decompose(new CustomerPayload());
        Method customers = CustomerPayload.class.getMethod("getCustomers");
        Method name = Customer.class.getMethod("getName");
        Node node = graph.find(ObjectGraphQuery.root().property(customers).index(1).property(name));
        assertEquals("/customers/1/name", node.path().toString());
    }

    @Test
    void sameMethodInDifferentBranchesResolvesDistinctNodes() throws Exception {
        AddressedPayload payload = new AddressedPayload(new Address("Rome"), new Address("Milan"));
        ObjectGraph graph = decomposer.decompose(payload);
        Method billing = AddressedPayload.class.getMethod("getBillingAddress");
        Method shipping = AddressedPayload.class.getMethod("getShippingAddress");
        Method city = Address.class.getMethod("getCity");
        Node billingCity = graph.find(ObjectGraphQuery.root().property(billing).property(city));
        Node shippingCity = graph.find(ObjectGraphQuery.root().property(shipping).property(city));
        assertNotEquals(billingCity.path(), shippingCity.path());
        assertEquals("/billingAddress/city", billingCity.path().toString());
        assertEquals("/shippingAddress/city", shippingCity.path().toString());
    }

    @Test
    void unresolvedQueryFailsFast() throws Exception {
        ObjectGraph graph = decomposer.decompose(new CustomerPayload());
        Method billing = AddressedPayload.class.getMethod("getBillingAddress");
        assertThrows(ObjectGraphException.class, () -> graph.find(ObjectGraphQuery.root().property(billing)));
    }

    @Test
    void jacksonRenamedPropertyKeepsCanonicalPointer() throws Exception {
        ObjectGraph graph = decomposer.decompose(new RenamedPayload("abc"));
        Method getter = RenamedPayload.class.getMethod("getCode");
        Node node = graph.find(ObjectGraphQuery.root().property(getter));
        assertEquals("/json_code", node.path().toString());
    }

    @Test
    void queryIsImmutableAndValueLike() throws Exception {
        Method customers = CustomerPayload.class.getMethod("getCustomers");
        ObjectGraphQuery root = ObjectGraphQuery.root();
        ObjectGraphQuery query = root.property(customers);
        assertTrue(root.isRoot());
        assertTrue(!query.isRoot());
        assertEquals(query, ObjectGraphQuery.root().property(customers));
    }

    static class CustomerPayload {
        private final List<Customer> customers = List.of(new Customer("a"), new Customer("b"), new Customer("c"));

        public List<Customer> getCustomers() {
            return customers;
        }
    }

    static class Customer {
        private final String name;

        Customer(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    static class AddressedPayload {
        private final Address billingAddress;
        private final Address shippingAddress;

        AddressedPayload(Address billingAddress, Address shippingAddress) {
            this.billingAddress = billingAddress;
            this.shippingAddress = shippingAddress;
        }

        public Address getBillingAddress() {
            return billingAddress;
        }

        public Address getShippingAddress() {
            return shippingAddress;
        }
    }

    static class Address {
        private final String city;

        Address(String city) {
            this.city = city;
        }

        public String getCity() {
            return city;
        }
    }

    static class RenamedPayload {
        private final String code;

        RenamedPayload(String code) {
            this.code = code;
        }

        @JsonProperty("json_code")
        public String getCode() {
            return code;
        }
    }
}
