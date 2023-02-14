package org.acme.service;

import org.acme.client.CustomerClient;
import org.acme.client.ProductClient;
import org.acme.dto.CustomerDTO;
import org.acme.dto.OrderDTO;
import org.acme.entity.OrderEntity;
import org.acme.repository.OrderRepository;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class OrderService {

    @Inject
    private OrderRepository orderRepository;

    @Inject
    @RestClient
    private CustomerClient customerClient;

    @Inject
    @RestClient
    private ProductClient productClient;

    public List<OrderDTO> getAllOrders() {
        List<OrderDTO> orders = new ArrayList<>();

        orderRepository.findAll().stream().forEach(item -> {
            orders.add(mapEntityToDTO(item));
        });

        return orders;
    }

    public void saveNewOrder(OrderDTO orderDTO) {
        CustomerDTO customerDTO = customerClient.getCustomerById(orderDTO.getCustomerId());

        if (customerDTO.getName().equals(orderDTO.getCustomerName()) && productClient.getProductById(orderDTO.getProductId()) != null) {
            orderRepository.persist(mapDTOToEntity(orderDTO));
        } else {
            throw new NotFoundException();
        }

    }

    private OrderDTO mapEntityToDTO(OrderEntity order) {
        OrderDTO orderDTO = new OrderDTO();

        orderDTO.setOrderValue(order.getOrderValue());
        orderDTO.setCustomerId(order.getCustomerId());
        orderDTO.setCustomerName(order.getCustomerName());
        orderDTO.setProductId(order.getProductId());

        return orderDTO;
    }

    private OrderEntity mapDTOToEntity(OrderDTO orderDTO) {
        OrderEntity orderEntity = new OrderEntity();

        orderEntity.setOrderValue(orderDTO.getOrderValue());
        orderEntity.setCustomerName(orderDTO.getCustomerName());
        orderEntity.setCustomerId(orderDTO.getCustomerId());
        orderEntity.setProductId(orderDTO.getProductId());

        return orderEntity;
    }


}
