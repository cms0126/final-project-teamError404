package com.error404.geulbut.jpa.users.controller;

import com.error404.geulbut.common.MapStruct;
import com.error404.geulbut.jpa.carts.dto.CartDto;
import com.error404.geulbut.jpa.carts.service.CartService;
import com.error404.geulbut.jpa.orders.dto.OrdersDto;
import com.error404.geulbut.jpa.orders.service.OrdersService;
import com.error404.geulbut.jpa.users.dto.UserMypageDto;
import com.error404.geulbut.jpa.users.entity.Users;
import com.error404.geulbut.jpa.users.service.UsersService;
import com.error404.geulbut.jpa.wishlist.dto.WishlistDto;
import com.error404.geulbut.jpa.wishlist.service.WishlistService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/mypage")
public class MypageController {

    private final UsersService usersService;
    private final WishlistService wishlistService;
    private final CartService cartService;
    private final MapStruct mapStruct;
    private final OrdersService ordersService;

    @Value("${portone.imp_code}")
    private String impCode;

    /** 📌 마이페이지 메인 */
    @GetMapping
    public String mypage(Model model,
                         HttpServletRequest request) {
        String loginUserId = getLoginUserId();
        if (loginUserId == null) {
            return "redirect:/login";
        }

        //  사용자 정보
        Users user = usersService.getUserById(loginUserId);
        if (user != null) {
            UserMypageDto dto = mapStruct.toMypageDto(user);
            model.addAttribute("user", dto);
            model.addAttribute("canChangePassword", user.getProvider() == Users.AuthProvider.LOCAL);
            model.addAttribute("pointBalance", user.getPoint() == null ? 0L : user.getPoint());
        }

        //  위시리스트
        List<WishlistDto> wishlist = wishlistService.getWishlist(loginUserId);
        model.addAttribute("wishlist", wishlist);

        //  장바구니
        List<CartDto> cartList = cartService.getCartList(loginUserId);
        long  cartTotal = cartList.stream()
                .mapToLong(CartDto::getTotalPrice)
                .sum();
        model.addAttribute("cart", cartList);
        model.addAttribute("cartTotal", cartTotal);

        //  주문 내역
        List<OrdersDto> orders = ordersService.getUserOrders(loginUserId);
        model.addAttribute("orders", orders);

        //  덕규 : 라스트오더아이디 세션 자동채우기(세션에 없을때만)
        var session = request.getSession();
        Object last = session.getAttribute("lastOrderId");
        if (last == null && orders != null && !orders.isEmpty()) {
            Long latestId = orders.stream()
                    .max(Comparator.comparing(OrdersDto::getOrderId))
                    .map(OrdersDto::getOrderId)
                    .orElse(null);
            if (latestId != null) {
                session.setAttribute("lastOrderId", latestId);
            }
        }

        //  사용자 누적금액, 등급 내역
        long total = user.getTotalPurchase() == null ? 0L : user.getTotalPurchase();
        long nextSilver = 100_000L;
        long nextGold = 300_000L;

        String nextTier = (total < nextSilver) ? "SILVER" : (total < nextGold ? "GOLD" : null);
        long toNext     = (nextTier == null) ? 0L : (nextTier.equals("SILVER") ? (nextSilver - total) : (nextGold - total));

        //  진행률 (현재 티어 기준)
        int progressPct;
        if (total >= nextGold) {
            progressPct = 100;
        } else if (total >= nextSilver) {
            long num = (total - nextSilver) * 100;
            long den = (nextGold - nextSilver);
            long v = Math.min(100L, Math.max(0L, num / den));
            progressPct = (int) v;
        } else {
            long num = total * 100;
            long den = nextSilver;
            long v = Math.min(100L, Math.max(0L, num / den));
            progressPct = (int) v;
        }
        model.addAttribute("totalPurchase", total);
        model.addAttribute("nextTier", nextTier);
        model.addAttribute("amountToNext", toNext);
        model.addAttribute("progressPct", progressPct);
        model.addAttribute("impCode", impCode);

        return "users/mypage/mypage";
    }

    /** 📌 비밀번호 변경 */
    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPw,
                                 @RequestParam String newPw,
                                 @RequestParam String confirmPw,
                                 Model model,
                                 HttpServletRequest request) {
        String loginUserId = getLoginUserId();
        if (loginUserId == null) return "redirect:/login";

        try {
            usersService.changePassword(loginUserId, currentPw, newPw, confirmPw);
            model.addAttribute("successMsg", "비밀번호가 변경되었습니다.");
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMsg", e.getMessage());
        }

        // 다시 mypage 데이터를 채워서 forward
        return mypage(model, request);
    }

    /** 📌 로그인 사용자 아이디 가져오기 */
    private String getLoginUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails userDetails) {
                return userDetails.getUsername();
            }
        }
        return null;
    }

//    덕규 추가 : 임시비번로그인 사용자 -> 바로 비번변경할수있게
    @GetMapping("/password/change")
    public String showChangePassword(Model model, HttpServletRequest request) {
        String loginUserId = getLoginUserId();
        if (loginUserId == null) return "redirect:/login";

        model.addAttribute("forceChangePw", true);
        return mypage(model, request);
    }

}
