package com.error404.geulbut.jpa.admin.controller;

import com.error404.geulbut.common.ErrorMsg;
import com.error404.geulbut.jpa.admin.service.AdminOrdersService;
import com.error404.geulbut.jpa.orders.dto.OrdersDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin/orders")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrdersController {

    private final AdminOrdersService adminOrdersService;
    private final ErrorMsg errorMsg;

    // 주문 목록 페이지(페이징 + 필터)
    @GetMapping
    public String listOrdersPage(Model model,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 @RequestParam(required = false) String status,
                                 @RequestParam(required = false) String userId) {

        // 서비스에서 필터 + 페이징 처리 (4개의 인자 전달)
        Page<OrdersDto> ordersPage = adminOrdersService.getAllOrdersWithItems(
                userId != null && !userId.isEmpty() ? userId : null,
                status != null && !status.isEmpty() ? status : null,
                page,
                size
        );

        model.addAttribute("ordersPage", ordersPage);
        model.addAttribute("status", status);
        model.addAttribute("userId", userId);
        return "admin/admin_orders_list";
    }

    // 단일 주문 조회
    @GetMapping("/{orderId}")
    @ResponseBody
    public OrdersDto getOrderById(@PathVariable Long orderId) {
        return adminOrdersService.getOrderById(orderId);
    }

    // 전체 주문 조회(items + books 포함, 필터 없이)
    @GetMapping("/all")
    @ResponseBody
    public List<OrdersDto> getAllOrdersWithItemsNoFilter() {
        // userId, status, page, size 없이 전체 조회
        return adminOrdersService.getAllOrdersWithItems(null, null, 0, Integer.MAX_VALUE)
                .getContent();
    }

    // 주문 상태 변경
    @PutMapping("/{orderId}/status")
    @ResponseBody
    public OrdersDto changeStatus(@PathVariable Long orderId, @RequestBody StatusDto dto) {
        return adminOrdersService.updateOrderStatus(orderId, dto.getStatus());
    }

    // DTO 클래스
    public static class StatusDto {
        private String status;
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }


    // 신규 주문 생성
    @PostMapping("/create")
    @ResponseBody
    public OrdersDto createOrder(@RequestBody OrdersDto dto) {
        return adminOrdersService.createOrder(dto);
    }

}
