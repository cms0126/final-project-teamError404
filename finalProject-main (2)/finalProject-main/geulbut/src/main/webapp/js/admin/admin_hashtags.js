// /js/admin/admin_hashtags.js
document.addEventListener('DOMContentLoaded', function () {
    const ctx = (typeof window.ctx !== 'undefined' && window.ctx) ? window.ctx : '';

    const hashtagModal = document.getElementById('hashtagModal');
    const modalTitle = document.getElementById('modalTitle');
    const hashtagNameInput = document.getElementById('hashtagName');
    const modalSaveBtn = document.getElementById('modalSaveBtn');
    const modalCloseBtn = document.getElementById('modalCloseBtn');

    const btnAddHashtag = document.getElementById('btnAddHashtag');
    const tableBody = document.getElementById('hashtagsTable')?.querySelector('tbody');
    const keywordInput = document.getElementById('keyword');
    const searchForm = document.getElementById('searchForm');

    const booksModal = document.getElementById('booksModal');
    const booksModalClose = document.getElementById('booksModalClose');
    const booksList = document.getElementById('booksList');
    const booksModalTitle = document.getElementById('booksModalTitle');
    const bookSearchForm = document.getElementById('bookSearchForm');
    const bookKeywordInput = document.getElementById('bookKeyword');
    const booksPager = document.getElementById('booksPager');
    const booksModalFooter = document.getElementById('booksModalFooter');

    const BOOKS_SAVE_BTN_ID = 'booksModalSaveBtn';
    const PAGE_SIZE = 20;

    let paging = { page: 1, size: PAGE_SIZE, total: 0, pages: 0 };
    let allBooksCache = [];
    let originalLinkedIds = new Set();
    let selectedBookIds  = new Set();
    let currentEditId = null;
    let currentManageHashtagId = null;

    // toast
    function showToast(message, type = 'success') {
        const toast = document.createElement('div');
        toast.textContent = message;
        toast.className = `toast ${type}`;
        document.body.appendChild(toast);
        requestAnimationFrame(() => toast.classList.add('show'));
        setTimeout(() => {
            toast.classList.remove('show');
            setTimeout(() => toast.remove(), 300);
        }, 2000);
    }

    // 모달 열기
    function openModal(edit = false, name = '', id = null) {
        if (!hashtagModal) return;
        hashtagModal.style.display = 'flex';
        hashtagModal.setAttribute('aria-hidden','false');
        hashtagNameInput.value = name;
        currentEditId = id;
        modalTitle.textContent = edit ? '해시태그 수정' : '해시태그 등록';
    }

    // 모달 닫기
    function closeModal() {
        if (!hashtagModal) return;
        hashtagModal.style.display = 'none';
        hashtagModal.setAttribute('aria-hidden','true');
        hashtagNameInput.value = '';
        currentEditId = null;
    }

    // 도서 모달 열기
    function openBooksModal(title) {
        if (!booksModal) return;
        booksModal.style.display = 'flex';
        booksModal.setAttribute('aria-hidden','false');
        booksModalTitle.textContent = title;
        if (booksList) booksList.innerHTML = '';
    }

    // 도서 모달 닫기
    function closeBooksModal() {
        if (!booksModal) return;
        booksModal.style.display = 'none';
        booksModal.setAttribute('aria-hidden','true');
        if (booksList) booksList.innerHTML = '';
        if (booksPager) booksPager.innerHTML = '';
        currentManageHashtagId = null;
        const saveBtn = document.getElementById(BOOKS_SAVE_BTN_ID);
        if (saveBtn) saveBtn.remove();
        if (bookKeywordInput) bookKeywordInput.value = '';
        allBooksCache = [];
        originalLinkedIds = new Set();
        selectedBookIds = new Set();
        paging = { page: 1, size: PAGE_SIZE, total: 0, pages: 0 };
    }

    // 버튼 바인딩
    btnAddHashtag?.addEventListener('click', () => openModal());
    modalCloseBtn?.addEventListener('click', closeModal);
    booksModalClose?.addEventListener('click', closeBooksModal);

    // 저장
    modalSaveBtn?.addEventListener('click', () => {
        const name = (hashtagNameInput?.value || '').trim();
        if (!name) { showToast('해시태그 이름을 입력해주세요', 'error'); return; }
        const url = currentEditId ? `${ctx}/admin/hashtags/${currentEditId}` : `${ctx}/admin/hashtags`;
        const method = currentEditId ? 'PUT' : 'POST';
        fetch(url, {
            method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name })
        })
            .then(res => { if (!res.ok) throw new Error('요청 실패'); return res.json(); })
            .then(() => { closeModal(); showToast(currentEditId ? '해시태그가 수정되었습니다' : '해시태그가 등록되었습니다'); setTimeout(() => location.reload(), 800); })
            .catch(err => { console.error(err); showToast(currentEditId ? '수정에 실패했습니다' : '등록에 실패했습니다', 'error'); });
    });

    // 테이블 클릭 이벤트 위임
    tableBody?.addEventListener('click', async (e) => {
        const tr = e.target.closest('tr');
        if (!tr) return;
        const hashtagId = tr.dataset.id;

        // 삭제
        if (e.target.classList.contains('btnDelete') || e.target.classList.contains('btn-delete')) {
            if (!confirm('정말 삭제하시겠습니까?')) return;
            try {
                const res = await fetch(`${ctx}/admin/hashtags/${hashtagId}`, { method: 'DELETE' });
                if (!res.ok) throw new Error(await res.text() || '삭제 중 오류 발생');
                const success = await res.json();
                if (success) { showToast('해시태그가 삭제되었습니다'); setTimeout(() => location.reload(), 800); }
                else showToast('삭제에 실패했습니다', 'error');
            } catch (err) { console.error(err); showToast(`삭제에 실패했습니다: ${err.message}`, 'error'); }
        }

        // 수정
        if (e.target.classList.contains('btnEdit') || e.target.classList.contains('btn-edit')) {
            const name = tr.children[1]?.textContent || '';
            openModal(true, name, hashtagId);
        }

        // 이름 클릭 → 등록 도서 보기
        if (e.target.classList.contains('hashtag-name')) {
            try {
                const res = await fetch(`${ctx}/admin/hashtags/${hashtagId}/books`);
                if (!res.ok) throw new Error('도서를 불러오는 중 오류 발생');
                const books = await res.json();
                openBooksModal(`해시태그 [${e.target.textContent}] 등록 도서`);
                if (!books || books.length === 0) booksList.innerHTML = '<p>등록된 도서가 없습니다.</p>';
                else {
                    const ul = document.createElement('ul');
                    books.forEach(b => { const li = document.createElement('li'); li.textContent = `${b.title} (${b.isbn})`; ul.appendChild(li); });
                    booksList.appendChild(ul);
                }
            } catch (err) { console.error(err); showToast(`도서를 불러오는 중 오류 발생: ${err.message}`, 'error'); }
        }

        // 도서 관리
        if (e.target.classList.contains('btn-manage-books')) {
            try { currentManageHashtagId = hashtagId; openBooksModal(`해시태그 [${tr.children[1]?.textContent || ''}] 도서 관리`); await loadBooks(hashtagId, ''); }
            catch (err) { console.error(err); showToast(`도서 관리 중 오류 발생: ${err.message}`, 'error'); }
        }
    });

    // 해시태그 검색
    searchForm?.addEventListener('submit', e => {
        e.preventDefault();
        const keyword = (keywordInput?.value || '').trim();
        let url = `${ctx}/admin/hashtags`;
        if (keyword) url += `?keyword=${encodeURIComponent(keyword)}`;
        location.href = url;
    });

    // 모달 내 도서 검색
    bookSearchForm?.addEventListener('submit', function (e) {
        e.preventDefault();
        if (!currentManageHashtagId) { showToast('먼저 관리할 해시태그를 선택하세요', 'error'); return; }
        const kw = (bookKeywordInput?.value || '').trim();
        loadBooks(currentManageHashtagId, kw);
    });

    // 도서 로드
    async function loadBooks(hashtagId, keyword = '') {
        if (!hashtagId) return;
        try {
            const resAll = await fetch(`${ctx}/admin/books/all?keyword=${encodeURIComponent(keyword)}`);
            if (!resAll.ok) throw new Error('도서를 불러오는 중 오류 발생');
            allBooksCache = await resAll.json();
            const resLinked = await fetch(`${ctx}/admin/hashtags/${hashtagId}/books`);
            if (!resLinked.ok) throw new Error('연결된 도서를 불러오는 중 오류 발생');
            const linkedBooks = await resLinked.json();
            originalLinkedIds = new Set(linkedBooks.map(b => Number(b.bookId)));
            selectedBookIds   = new Set(originalLinkedIds);
            paging.total = allBooksCache.length;
            paging.pages = Math.max(1, Math.ceil(paging.total / paging.size));
            paging.page  = 1;
            renderBooksPage();
            ensureBooksSaveButton();
        } catch (err) { console.error(err); showToast(err.message || '도서 로드 중 오류', 'error'); }
    }

    // 도서 페이지 렌더
    function renderBooksPage() {
        const start = (paging.page - 1) * paging.size;
        const end   = Math.min(start + paging.size, paging.total);
        const pageItems = allBooksCache.slice(start, end);
        booksList.innerHTML = '';
        const ul = document.createElement('ul');
        pageItems.forEach(book => {
            const idNum = Number(book.bookId);
            const li = document.createElement('li');
            const checkbox = document.createElement('input');
            checkbox.type = 'checkbox';
            checkbox.value = idNum;
            checkbox.className = 'book-checkbox';
            checkbox.checked = selectedBookIds.has(idNum);
            checkbox.addEventListener('change', (ev) => { ev.stopPropagation(); checkbox.checked ? selectedBookIds.add(idNum) : selectedBookIds.delete(idNum); });
            li.appendChild(checkbox);
            li.appendChild(document.createTextNode(` ${book.title} (${book.isbn})`));
            ul.appendChild(li);
        });
        booksList.appendChild(ul);
        if (booksPager) renderBooksPager();
    }

    // 도서 페이저 렌더
    function renderBooksPager() {
        booksPager.innerHTML = '';
        const group = document.createElement('div');
        group.className = 'btn-group';
        booksPager.appendChild(group);
        const prev = document.createElement('a');
        prev.href = '#';
        prev.className = 'btn btn-secondary btn-nav';
        prev.setAttribute('aria-label', '이전');
        prev.textContent = '«';
        if (paging.page === 1) prev.setAttribute('aria-disabled', 'true');
        group.appendChild(prev);
        const total = paging.pages;
        const cur   = paging.page;
        const windowSize = 7;
        let start = Math.max(1, cur - 2);
        let end   = Math.min(total, start + windowSize - 1);
        start     = Math.max(1, Math.min(start, end - windowSize + 1));
        for (let i = start; i <= end; i++) {
            const a = document.createElement('a');
            a.href = '#';
            a.className = 'btn btn-secondary page-btn' + (i === cur ? ' active' : '');
            if (i === cur) a.setAttribute('aria-current', 'page');
            a.dataset.page = String(i);
            a.textContent = String(i);
            group.appendChild(a);
        }
        const next = document.createElement('a');
        next.href = '#';
        next.className = 'btn btn-secondary btn-nav';
        next.setAttribute('aria-label', '다음');
        next.textContent = '»';
        if (paging.page === total || total === 0) next.setAttribute('aria-disabled', 'true');
        group.appendChild(next);
    }

    // 도서 페이저 클릭
    booksPager?.addEventListener('click', (e) => {
        const btn = e.target.closest('a.btn');
        if (!btn) return;
        e.preventDefault();
        e.stopPropagation();
        if (btn.hasAttribute('aria-disabled')) return;
        if (btn.classList.contains('btn-nav')) {
            paging.page += btn.textContent.includes('«') ? -1 : 1;
            if (paging.page >= 1 && paging.page <= paging.pages) renderBooksPage();
            return;
        }
        if (btn.classList.contains('page-btn')) {
            const p = Number(btn.dataset.page || '1');
            if (p >= 1 && p <= paging.pages && p !== paging.page) { paging.page = p; renderBooksPage(); }
        }
    });

    // 도서 저장 버튼 생성 및 핸들러
    function ensureBooksSaveButton(){
        let saveBtn = document.getElementById(BOOKS_SAVE_BTN_ID);
        if (!saveBtn){
            saveBtn = document.createElement('button');
            saveBtn.id = BOOKS_SAVE_BTN_ID;
            saveBtn.type = 'button';
            saveBtn.textContent = '저장';
            saveBtn.className = 'btn btn-primary btn--liquid-glass';
            const container = (booksModalFooter || booksList.parentElement);
            const closeBtn  = document.getElementById('booksModalClose');
            if (container && closeBtn && closeBtn.parentElement === container) container.insertBefore(saveBtn, closeBtn);
            else container.appendChild(saveBtn);
        }

        saveBtn.onclick = async () => {
            saveBtn.disabled = true;
            try {
                const toAdd = [];
                const toRemove = [];
                selectedBookIds.forEach(id => { if (!originalLinkedIds.has(id)) toAdd.push(id); });
                originalLinkedIds.forEach(id => { if (!selectedBookIds.has(id)) toRemove.push(id); });
                for (const id of toAdd) { const r = await fetch(`${ctx}/admin/hashtags/${currentManageHashtagId}/books/${id}`, { method: 'POST' }); if (!r.ok) console.error('추가 실패', id); }
                for (const id of toRemove) { const r = await fetch(`${ctx}/admin/hashtags/${currentManageHashtagId}/books/${id}`, { method: 'DELETE' }); if (!r.ok) console.error('삭제 실패', id); }
                showToast('도서 연결이 업데이트되었습니다');
                closeBooksModal();
                setTimeout(() => location.reload(), 800);
            } catch (err) { console.error(err); showToast('도서 연결 업데이트 중 오류 발생', 'error'); }
            finally { saveBtn.disabled = false; }
        };
    }

    // ESC 키로 모달 닫기
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') {
            if (hashtagModal && hashtagModal.style.display === 'flex') closeModal();
            if (booksModal && booksModal.style.display === 'flex') closeBooksModal();
        }
    });
});
