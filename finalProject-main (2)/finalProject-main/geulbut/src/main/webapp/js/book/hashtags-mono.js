(function (window, document) {
    'use strict';

    const $  = (sel, ctx=document) => ctx.querySelector(sel);
    const $$ = (sel, ctx=document) => Array.from(ctx.querySelectorAll(sel));

    function updateCounts() {
        const bookRows = $$('#ht-table-book tbody tr');
        const tagRows  = $$('#ht-table-tag  tbody tr');
        const nBook = bookRows.length;
        const nTag  = tagRows.length;
        const bookBadge = $('#ht-count-book');
        const tagBadge  = $('#ht-count-tag');
        if (bookBadge) bookBadge.textContent = String(nBook);
        if (tagBadge)  tagBadge.textContent  = String(nTag);
    }

    function wireEnterToSubmit() {
        // 엔터 키로 각 입력 필드 근처 버튼 실행 (서버 검색 그대로 사용)
        const form = document.querySelector('.ht-searchgrid');
        if (!form) return;
        form.addEventListener('keydown', (e) => {
            if (e.key !== 'Enter') return;
            const target = e.target;
            if (!(target instanceof HTMLInputElement)) return;
            e.preventDefault();
            // 포커스가 책 검색이면 book 버튼, 태그 검색이면 tag 버튼
            const isBook = target.id === 'qBook';
            const btn = isBook
                ? form.querySelector('button[name="target"][value="book"]')
                : form.querySelector('button[name="target"][value="tag"]');
            btn?.click();
        });
    }

    function init() {
        updateCounts();
        wireEnterToSubmit();
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})(window, document);
