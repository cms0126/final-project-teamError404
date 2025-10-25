<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<footer class="bg-surface text-main border-top py-4 mt-6">
    <div class="container">
        <!-- 콘텐츠 영역 -->
        <div class="grid cols-2 gap-2">
            <!-- 좌측: 프로젝트/기술 스택 -->
            <div>
                <p class="m-0"><strong>프로젝트:</strong> 글벗</p>
                <p class="m-0"><strong>팀명:</strong> TEAM 404 NOT FOUND</p>
                <p class="m-0"><strong>Tech Stack</strong></p>
                <div class="tech-stack-animate">
                    <p class="m-0"><strong>Language:</strong> Java</p>
                    <p class="m-0"><strong>Front-end:</strong> HTML · CSS · JavaScript · Bootstrap</p>
                    <p class="m-0"><strong>Back-end:</strong> JSP · Spring Boot · JPA</p>
                    <p class="m-0"><strong>Database:</strong> OracleDB</p>
                    <p class="m-0"><strong>Search:</strong> Elasticsearch</p>
                </div>
                <span class="text-light">Powered by Aladin API</span>
            </div>

            <!-- 우측: 팀 정보 -->
            <div style="text-align:right;">
                <p class="m-0 team-leader">팀장: 최종일</p>

                <!-- 팀원 3열 -->
                <div class="team-grid">
                    <p>서덕규</p>
                    <p>신승화</p>
                    <p>문려경</p>
                    <p>강대성</p>
                    <p>오태관</p>
                    <p>최민석</p>
                </div>

                <p>© 2025 글벗. All rights reserved.</p>
                <p class="mt-2 mr-4 github-badge">
                    <a href="https://github.com/YeonHaru/finalProject"
                       target="_blank" rel="noopener noreferrer">
                        <img src="https://img.shields.io/badge/GitHub-글벗-48C9B0?logo=github&logoColor=white"
                             alt="GitHub 글벗">
                    </a>
                </p>
            </div>
        </div>
    </div>
</footer>

<script>
    document.addEventListener("DOMContentLoaded", function () {
        const techStackEl = document.querySelector('.tech-stack-animate');

        if (!techStackEl) return;

        const observer = new IntersectionObserver((entries, observer) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.classList.add('tech-stack-visible');
                    observer.unobserve(entry.target);
                }
            });
        }, {
            root: null,
            rootMargin: '0px',
            threshold: 0.3
        });

        observer.observe(techStackEl);
    });
</script>
