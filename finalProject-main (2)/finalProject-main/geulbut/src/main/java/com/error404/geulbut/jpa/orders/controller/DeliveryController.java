package com.error404.geulbut.jpa.orders.controller;

import com.error404.geulbut.jpa.orders.dto.OrdersDto;
import com.error404.geulbut.jpa.orders.service.OrdersService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class DeliveryController {

    private final OrdersService ordersService;

    // ✅ 최종 경로: /orders/{orderId}/delivery
    @GetMapping("/{orderId}/delivery")
    public String delivery(@PathVariable Long orderId, Model model, Principal principal) {
        var dv = ordersService.buildDeliveryView(orderId);
        model.addAttribute("delivery", dv);

        String userId = (principal != null) ? principal.getName() : dv.getOrdersDto().getUserId();
        List<OrdersDto> history = ordersService.getDeliveredHistory(userId, 10, orderId);
        model.addAttribute("history", history);

        return "orders/track/delivery_info";
    }

    // /orders/delivery -> 마이페이지로
    @GetMapping("/delivery")
    public String deliveryRedirect() {
        return "redirect:/mypage";
    }

    // /orders/delivery?orderId=123 -> /orders/123/delivery 로 리다이렉트
    @GetMapping(value = "/delivery", params = "orderId")
    public String deliveryWithParam(@RequestParam Long orderId) {
        return "redirect:/orders/" + orderId + "/delivery";
    }
}
