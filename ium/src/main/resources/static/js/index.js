// 카테고리 필터링 함수 (서버 사이드)
function filterByCategory(category) {
    const searchInput = document.querySelector('.search-input');
    const currentSearch = searchInput ? searchInput.value.trim() : '';
    let url = '/';
    
    const params = new URLSearchParams();
    
    if (category !== 'all') {
        params.append('category', category);
    }
    
    if (currentSearch) {
        params.append('search', currentSearch);
    }
    
    if (params.toString()) {
        url += '?' + params.toString();
    }
    
    window.location.href = url;
}

// 의뢰하기 버튼 클릭
function redirectToRequestForm() {
    window.location.href = '/request/new';
}

// AI 매칭 버튼 클릭
function redirectToAIMatching() {
    window.location.href = '/recommend';
}

// 검색 기능
document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.querySelector('.search-input');
    const searchButton = document.querySelector('.search-button');
    
    if (searchButton) {
        searchButton.addEventListener('click', performSearch);
    }
    
    if (searchInput) {
        searchInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                performSearch();
            }
        });
        
        // 검색어가 있다면 input에 설정
        if (typeof currentSearch !== 'undefined' && currentSearch) {
            searchInput.value = currentSearch;
        }
    }
    
    // 페이지 로드 시 현재 카테고리 버튼 활성화
    if (typeof currentCategory !== 'undefined' && currentCategory) {
        const activeBtn = document.querySelector(`[data-category="${currentCategory}"]`);
        if (activeBtn) {
            document.querySelectorAll('.category-btn').forEach(btn => {
                btn.classList.remove('active');
            });
            activeBtn.classList.add('active');
        }
    }
});

function performSearch() {
    const searchInput = document.querySelector('.search-input');
    if (!searchInput) return;
    
    const searchTerm = searchInput.value.trim();
    let url = '/';
    
    const params = new URLSearchParams();
    
    // 현재 활성화된 카테고리 유지
    if (typeof currentCategory !== 'undefined' && currentCategory && currentCategory !== 'all') {
        params.append('category', currentCategory);
    }
    
    if (searchTerm) {
        params.append('search', searchTerm);
    }
    
    if (params.toString()) {
        url += '?' + params.toString();
    }
    
    window.location.href = url;
}

// 검색어와 카테고리 필터 초기화
function clearSearchAndFilter() {
    window.location.href = '/';
}

// 의뢰 카드 클릭 처리는 HTML에서 직접 처리하도록 변경됨

// 채팅방 이동
function getRandomString(length) {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    let result = '';
    for (let i = 0; i < length; i++) {
        result += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    return result;
}

// 채팅 아이콘이 있는 경우에만 이벤트 리스너 추가
document.addEventListener('DOMContentLoaded', function() {
    const chatIcon = document.querySelector('.chat-icon');
    if (chatIcon) {
        chatIcon.addEventListener('click', function() {
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = '/chat/createRoom';

            const nameInput = document.createElement('input');
            nameInput.type = 'hidden';
            nameInput.name = 'name';
            nameInput.value = getRandomString(10);
            form.appendChild(nameInput);

            const targetUserInput = document.createElement('input');
            targetUserInput.type = 'hidden';
            targetUserInput.name = 'targetUser';
            targetUserInput.value = 'admin@test.com'; // admin user email
            form.appendChild(targetUserInput);

            document.body.appendChild(form);
            form.submit();
        });
    }
});

// 가격 포맷팅 함수 (이미 서버에서 처리되므로 필요시에만 사용)
function formatPrice(price) {
    return price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',') + '원~';
}
