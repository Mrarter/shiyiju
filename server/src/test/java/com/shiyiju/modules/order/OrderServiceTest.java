package com.shiyiju.modules.order;

import com.shiyiju.common.exception.BusinessException;
import com.shiyiju.modules.order.dto.CreateOrderDTO;
import com.shiyiju.modules.order.entity.ArtworkSnapshotEntity;
import com.shiyiju.modules.order.entity.OrderEntity;
import com.shiyiju.modules.order.entity.ShippingAddressEntity;
import com.shiyiju.modules.order.mapper.OrderMapper;
import com.shiyiju.modules.order.service.OrderService;
import com.shiyiju.modules.order.vo.OrderDetailVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    @Test
    void shouldCreatePrimaryOrder() {
        ArtworkSnapshotEntity artwork = new ArtworkSnapshotEntity();
        artwork.setArtworkId(88L);
        artwork.setArtistId(8L);
        artwork.setArtworkNo("A-88");
        artwork.setTitle("海的呼吸");
        artwork.setCurrentPrice(new BigDecimal("2000.00"));
        artwork.setInventoryAvailable(1);
        artwork.setStatus("PUBLISHED");

        ShippingAddressEntity address = new ShippingAddressEntity();
        address.setId(6L);
        address.setUserId(1L);

        OrderEntity detail = new OrderEntity();
        detail.setOrderId(1001L);
        detail.setOrderNo("O202604061200001");
        detail.setOrderType("PRIMARY");
        detail.setOrderStatus("PENDING_PAYMENT");
        detail.setPaymentStatus("UNPAID");
        detail.setGoodsAmount(new BigDecimal("2000.00"));
        detail.setPayAmount(new BigDecimal("2000.00"));
        detail.setAddressId(6L);
        detail.setItemTitle("海的呼吸");
        detail.setArtworkId(88L);
        detail.setArtistId(8L);
        detail.setQuantity(1);
        detail.setUnitPrice(new BigDecimal("2000.00"));
        detail.setSubtotalAmount(new BigDecimal("2000.00"));

        when(orderMapper.findArtworkSnapshot(88L)).thenReturn(artwork);
        when(orderMapper.findAddress(6L, 1L)).thenReturn(address);
        when(orderMapper.decreaseArtworkInventory(88L)).thenReturn(1);
        doAnswer(invocation -> {
            OrderEntity order = invocation.getArgument(0);
            order.setOrderId(1001L);
            return null;
        }).when(orderMapper).insertOrder(any(OrderEntity.class));
        when(orderMapper.findOrderDetail(1001L, 1L)).thenReturn(detail);
        when(orderMapper.findOrderAddress(6L)).thenReturn(address);

        CreateOrderDTO request = new CreateOrderDTO();
        request.setArtworkId(88L);
        request.setAddressId(6L);

        OrderDetailVO result = orderService.createOrder(1L, request);

        assertEquals(1001L, result.getOrderId());
        assertEquals("PRIMARY", result.getOrderType());
        verify(orderMapper).insertOrderItem(eq(1001L), eq(artwork), eq(null));
        verify(orderMapper).insertCertificate(any(String.class), eq(88L), eq(1L), eq(1001L));
    }

    @Test
    void shouldRejectWhenArtworkUnavailable() {
        ArtworkSnapshotEntity artwork = new ArtworkSnapshotEntity();
        artwork.setArtworkId(88L);
        artwork.setStatus("COLLECTED");
        artwork.setInventoryAvailable(0);
        when(orderMapper.findArtworkSnapshot(88L)).thenReturn(artwork);

        CreateOrderDTO request = new CreateOrderDTO();
        request.setArtworkId(88L);
        request.setAddressId(6L);

        assertThrows(BusinessException.class, () -> orderService.createOrder(1L, request));
    }

    @Test
    void shouldThrowWhenOrderMissing() {
        when(orderMapper.findOrderDetail(100L, 1L)).thenReturn(null);
        assertThrows(BusinessException.class, () -> orderService.getOrderDetail(1L, 100L));
    }
}
