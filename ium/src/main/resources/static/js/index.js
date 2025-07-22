// 현재 활성화된 카테고리
let currentCategory = 'all';

// 카테고리 필터링 함수
function filterByCategory(category) {
    currentCategory = category;
    
    // 카테고리 버튼 활성화 상태 변경
    document.querySelectorAll('.category-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    document.querySelector(`[data-category="${category}"]`).classList.add('active');
    
    // 의뢰 카드 필터링
    const requestCards = document.querySelectorAll('.request-card');
    requestCards.forEach(card => {
        if (category === 'all' || card.dataset.category === category) {
            card.style.display = 'flex';
        } else {
            card.style.display = 'none';
        }
    });
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
    
    searchButton.addEventListener('click', performSearch);
    searchInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            performSearch();
        }
    });
});

function performSearch() {
    const searchTerm = document.querySelector('.search-input').value.trim();
    if (searchTerm) {
        window.location.href = `/search?q=${encodeURIComponent(searchTerm)}`;
    }
}

// 의뢰 카드 클릭
document.addEventListener('click', function(e) {
    if (e.target.closest('.request-card')) {
        const card = e.target.closest('.request-card');
        const requestId = card.dataset.requestId || '1';
        window.location.href = `/request/${requestId}`;
    }
});

// 채팅방 이동
function getRandomString(length) {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    let result = '';
    for (let i = 0; i < length; i++) {
        result += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    return result;
}

document.querySelector('.chat-icon').addEventListener('click', function() {
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