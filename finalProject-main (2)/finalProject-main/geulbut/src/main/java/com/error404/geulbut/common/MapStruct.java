package com.error404.geulbut.common;

import com.error404.geulbut.es.searchAllBooks.dto.SearchAllBooksDto;
import com.error404.geulbut.es.searchAllBooks.entity.SearchAllBooks;
import com.error404.geulbut.jpa.authors.dto.AuthorsDto;
import com.error404.geulbut.jpa.authors.entity.Authors;
import com.error404.geulbut.jpa.bookhashtags.dto.BookHashtagsDto;
import com.error404.geulbut.jpa.bookhashtags.entity.BookHashtags;
import com.error404.geulbut.jpa.books.dto.BooksDto;
import com.error404.geulbut.jpa.books.entity.Books;
import com.error404.geulbut.jpa.carts.dto.CartDto;
import com.error404.geulbut.jpa.carts.entity.Cart;
import com.error404.geulbut.jpa.categories.dto.CategoriesDto;
import com.error404.geulbut.jpa.categories.entity.Categories;
import com.error404.geulbut.jpa.hashtags.dto.HashtagsDto;
import com.error404.geulbut.jpa.hashtags.entity.Hashtags;
import com.error404.geulbut.jpa.orderitem.dto.OrderItemDto;
import com.error404.geulbut.jpa.orderitem.entity.OrderItem;
import com.error404.geulbut.jpa.orders.dto.OrdersDto;
import com.error404.geulbut.jpa.orders.entity.Orders;
import com.error404.geulbut.jpa.publishers.dto.PublishersDto;
import com.error404.geulbut.jpa.publishers.entity.Publishers;
import com.error404.geulbut.jpa.reviews.dto.ReviewsDto;
import com.error404.geulbut.jpa.users.dto.*;
import com.error404.geulbut.jpa.reviews.entity.Reviews;
import com.error404.geulbut.jpa.users.entity.Users;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MapStruct {
    //    jpa
//    종일
//    Authors <-> AuthorsDto
    @Mapping(target = "createdAt", source = "createdAt")
    AuthorsDto toDto(Authors authors);

    Authors toEntity(AuthorsDto authorsDto);

    //    더티체킹: 수정시 사용
    void updateFromDto(AuthorsDto authorsDto, @MappingTarget Authors authors);

    //    Categories <-> CategoriesDto
    @Mapping(target = "createdAt", source = "createdAt")
    CategoriesDto toDto(Categories categories);

    Categories toEntity(CategoriesDto categoriesDto);

    void updateFromDto(CategoriesDto categoriesDto, @MappingTarget Categories categories);

    //    Hashtags <-> HashtagsDto
    HashtagsDto toDto(Hashtags hashtags);

    Hashtags toEntity(HashtagsDto hashtagsDto);

    void updateFromDto(HashtagsDto hashtagsDto, @MappingTarget Hashtags hashtags);

    //    Publishers <-> PublishersDto
    @Mapping(target = "createdAt", source = "createdAt")
    PublishersDto toDto(Publishers publishers);

    Publishers toEntity(PublishersDto publishersDto);

    void updateFromDto(PublishersDto publishersDto, @MappingTarget Publishers publishers);

    //    덕규9/11
//    로그인 매핑
    UsersLoginDto toDto(Users users);

    Users toEntity(UsersLoginDto usersLoginDto);

    //    회원가입 DTO -> Users (boolean -> char 명시적 매핑추가)
    @Mappings({
            @Mapping(target = "postNotifyAgree", expression = "java(usersSignupDto.isPostNotifyAgree() ? 'Y' : 'N')"),
            @Mapping(target = "promoAgree", expression = "java(usersSignupDto.isPromoAgree() ? 'Y' : 'N')")
    })
    Users toEntity(UsersSignupDto usersSignupDto);

    //    OAuth 업서트 DTO -> Users (부분 업데이트용)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromOAuth(UsersOAuthUpsertDto usersOAuthUpsertDto, @MappingTarget Users entity);

    //    ElasticSearch
    SearchAllBooksDto toDto(SearchAllBooks searchAllBooks);
    SearchAllBooks toEntity(SearchAllBooksDto searchAllBooksDto);

    //    더티체킹: 수정시 사용
    void updateFromDto(SearchAllBooksDto searchAllBooksDto, @MappingTarget SearchAllBooks searchAllBooks);

    //    String -> AuthProvider 매핑 (대소문자/공백 방어)
    default Users.AuthProvider toProvider(String provider) {
        if (provider == null) return null;
        return Users.AuthProvider.valueOf(provider.trim().toUpperCase());
    }

//  Books <-> BooksDto
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "authorId", source = "author.authorId")
    @Mapping(target = "authorName", source = "author.name")
    @Mapping(target = "publisherId", source = "publisher.publisherId")
    @Mapping(target = "publisherName", source = "publisher.name")
    @Mapping(target = "categoryId", source = "category.categoryId")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "hashtags", source = "hashtags", qualifiedByName = "mapHashtagsToNames")
    @Mapping(target = "rating", source = "rating")
    @Mapping(target = "reviewCount", source = "reviewCount")
    BooksDto toDto(Books books);

    @Named("mapHashtagsToNames")
    default List<String> mapHashtagsToNames(Set<Hashtags> hashtags) {
        if (hashtags == null || hashtags.isEmpty()) return List.of();
        return hashtags.stream()
                .map(Hashtags::getName)
                .sorted()
                .toList();
    }

    @Mapping(target = "author.authorId", source = "authorId")
    @Mapping(target = "publisher.publisherId", source = "publisherId")
    @Mapping(target = "category.categoryId", source = "categoryId")
    @Mapping(target = "hashtags", ignore = true)
    @Mapping(target = "rating", source = "rating")
    @Mapping(target = "reviewCount", source = "reviewCount")
    Books toEntity(BooksDto booksDto);

    @Mapping(target = "hashtags", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "publisher", ignore = true)
    @Mapping(target = "category", ignore = true)
    void updateFromDto(BooksDto dto, @MappingTarget Books books);

    // Reviews

    @Mapping(target = "reviewId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user.userId", source = "userId")
    @Mapping(target = "book.bookId", source = "bookId")
    Reviews toEntity(ReviewsDto dto);

    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "bookId", source = "book.bookId")
    ReviewsDto toDto(Reviews entity);



    //  Mypage DTO 변환 승화
    @Mappings({
            @Mapping(source = "joinDate", target = "joinDate", dateFormat = "yyyy-MM-dd"),
            @Mapping(source = "name", target = "userName"),   // 엔티티 필드가 name일 경우
            @Mapping(source = "phone", target = "phone"),
            @Mapping(source = "address", target = "address")
    })
    UserMypageDto toMypageDto(Users users);

    // Orders 매핑
    @Mapping(target = "orderId", ignore = true)
    @Mapping(target = "memo", ignore = true)
    @Mapping(target = "recipient", ignore = true)
    @Mapping(target = "status", expression = "java(dto.getStatus() != null ? dto.getStatus() : \"PENDING\")")
    Orders toEntity(OrdersDto dto);

    @Mapping(
            target = "createdAt",
            source = "createdAt" // LocalDateTime 그대로 전달
    )
    @Mapping(target = "items", source = "items")
    @Mapping(target = "userName", expression = "java(entity.getUser() != null ? entity.getUser().getName() : null)")
    @Mapping(
            target = "deliveredAtFormatted",
            expression = "java(entity.getDeliveredAt() == null ? null : entity.getDeliveredAt().format(java.time.format.DateTimeFormatter.ofPattern(\"yyyy-MM-dd (E) HH:mm\")))"
    )
    OrdersDto toDto(Orders entity);

//    주문내역쪽 리스트 매핑
    List<OrderItemDto> toOrderItemDtos(List<OrderItem> items);

    // OrderItem 매핑
    @Mapping(target = "orderedItemId", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "book", ignore = true)
    OrderItem toEntity(OrderItemDto dto);

    @Mapping(target = "bookId", source = "book.bookId")
    @Mapping(target = "title", source = "book.title")
    @Mapping(target = "price", source = "book.price")
    @Mapping(target = "imageUrl", source = "book.imgUrl")
    OrderItemDto toDto(OrderItem entity);

//    cart
    @Mapping(target = "bookId", source = "book.bookId")
    @Mapping(target = "title", source = "book.title")
    @Mapping(target = "price", source = "book.price")
    @Mapping(target = "discountedPrice", source = "book.discountedPrice")
    @Mapping(target = "imgUrl", source = "book.imgUrl")
    @Mapping(
            target = "totalPrice",
            expression = "java(java.util.Objects.requireNonNullElse(cart.getBook().getDiscountedPrice(), cart.getBook().getPrice()) * cart.getQuantity())"
    )
    CartDto toDto(Cart cart);

    Cart toEntity(CartDto dto);

    List<CartDto> toCartDtos(List<Cart> carts);


    // BookHashtags <-> BookHashtagsDto
    BookHashtagsDto toDto(BookHashtags bookHashtags);

    BookHashtags toEntity(BookHashtagsDto dto);

    void updateFromDto(BookHashtagsDto dto, @MappingTarget BookHashtags entity);

    // 관리자용 DTO
    UsersDto toAdminDto(Users users);
    Users toAdminEntity(UsersDto usersDto);
    void updateFromAdminDto(UsersDto usersDto, @MappingTarget Users users);
}
