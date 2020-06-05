package com.fujitsu.cloudlab.order.service;

import com.fujitsu.cloudlab.commons.constants.AppConstants;
import com.fujitsu.cloudlab.commons.exception.ApiException;
import com.fujitsu.cloudlab.order.json.model.OrderResponse;
import com.fujitsu.cloudlab.order.orm.model.entity.OrderData;
import com.fujitsu.cloudlab.order.repository.OrderDataRepository;
import com.google.gson.Gson;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderRetrieverService {

  @Autowired OrderDataRepository orderDataRepository;

  private static Gson gson = new Gson();

  public OrderResponse getOrderDetails(String orderId) throws ApiException {

    Optional<OrderData> orderData = orderDataRepository.findOrderByOrderId(orderId);
    if (orderData.isPresent()) {
      OrderData order = orderData.get();
      OrderResponse orderResponse =  gson.fromJson(order.getOrderData(), OrderResponse.class);
	  if(orderResponse.getFormOfPayment().getPaymentMethod().getPaymentCard() != null) {
		  String cardNumber = orderResponse.getFormOfPayment().getPaymentMethod().getPaymentCard().getCardNum();
		  StringBuffer sb = new StringBuffer();;
		  sb.append(cardNumber.substring(0, 3));
		  sb.append(cardNumber.substring(13, 16));
		  orderResponse.getFormOfPayment().getPaymentMethod().getPaymentCard().setCardNum(sb.toString());
	  }
      if(order.getOrderStatus().equals(AppConstants.DELETED))
    	  orderResponse.setOrderDeletedUtcTs(order.getOrderDeletedUtcTs().toString());
      
      return orderResponse;
    } else {
      throw new ApiException(AppConstants.ORD404, "Order not found", "Order not found in DB", null);
    }
  }
}
