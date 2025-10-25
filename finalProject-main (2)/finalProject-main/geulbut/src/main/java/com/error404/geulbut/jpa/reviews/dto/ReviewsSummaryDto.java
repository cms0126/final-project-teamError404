package com.error404.geulbut.jpa.reviews.dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReviewsSummaryDto {
    private long total;          // 총 리뷰 수
    private double avg;          // 평균 평점 (소수 1~2자리)
    private long c1, c2, c3, c4, c5; // 평점별 개수

    public double p1() { return pct(c1); }
    public double p2() { return pct(c2); }
    public double p3() { return pct(c3); }
    public double p4() { return pct(c4); }
    public double p5() { return pct(c5); }

    private double pct(long c) { return total == 0 ? 0d : (c * 100.0 / total); }
}
