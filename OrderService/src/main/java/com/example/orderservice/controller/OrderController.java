package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.jpa.OrderEntity;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.vo.RequestOrder;
import com.example.orderservice.vo.ResponseOrder;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order-service")
public class OrderController {
  Environment env;
  OrderService orderService;

  @Autowired
  public OrderController(Environment env, OrderService orderService) {
    this.env = env;
    this.orderService = orderService;
  }

  @GetMapping("/health_check")
  public String status(HttpServletRequest request) {
    return String.format("It's Working in User Service on Port %s", request.getServerPort());

  }

  @GetMapping(value="/{userId}/orders")
  public ResponseEntity<List<ResponseOrder>> getOrder(@PathVariable("userId") String userId) {
    Iterable<OrderEntity> orderList = orderService.getOrdersByUserId(userId);

    List<ResponseOrder> result = new ArrayList<>();
    orderList.forEach(v -> {
      result.add(new ModelMapper().map(v, ResponseOrder.class));
    });
    return ResponseEntity.status(HttpStatus.OK).body(result);
  }

  @PostMapping(value = "/{userId}/orders")
  public ResponseEntity<ResponseOrder> createOrder(@PathVariable("userId") String userId,
      @RequestBody RequestOrder orderDetails) {

    ModelMapper modelMapper = new ModelMapper();
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

    OrderDto orderDto = modelMapper.map(orderDetails, OrderDto.class);
    orderDto.setUserId(userId);
    OrderDto createDto = orderService.createOrder(orderDto);
    ResponseOrder returnValue = modelMapper.map(createDto, ResponseOrder.class);

    return ResponseEntity.status(HttpStatus.CREATED).body(returnValue);
  }

}
