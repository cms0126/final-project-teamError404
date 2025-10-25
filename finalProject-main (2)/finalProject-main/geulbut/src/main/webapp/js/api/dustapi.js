document.addEventListener('DOMContentLoaded', () => {
    const ticker = document.getElementById('dust-ticker');
    if (!ticker) {
        console.error("Ticker element not found!");
        return;
    }

    fetch('http://192.168.30.37:8080/dustApi')
        .then(res => res.json())
        .then(data => {
            const items = Array.isArray(data) ? data : [data];

            if (!items.length) {
                ticker.textContent = "데이터 없음";
                return;
            }

            let idx = 0;

            function showNext() {
                const item = items[idx];
                const district = item.districtName || item.key || "지역";
                // grade가 null이면 "데이터 없음"으로 표시
                const issue = item.issueGbn || item.value || item.grade || "데이터 없음";
                const val = item.issueVal || item.val || "-";

                ticker.textContent = `${district}: ${issue} (${val})`;

                ticker.classList.remove("warning", "danger");
                if (issue === "경보") {
                    ticker.classList.add("danger");
                } else if (issue === "주의보") {
                    ticker.classList.add("warning");
                }

                idx = (idx + 1) % items.length;
            }

            showNext();
            setInterval(showNext, 3000);
        })
        .catch(err => {
            console.error("API 호출 실패:", err);
            ticker.textContent = "API 호출 실패";
        });
});
