// 분야 선택 페이지 함수
function selectCategory(category) {
    // 선택한 카테고리로 대화 페이지 이동
    window.location.href = `/recommend/chat?category=${category}`;
}

// 대화 페이지가 로드될 때 실행
document.addEventListener('DOMContentLoaded', function() {
    // 대화 페이지인지 확인
    if (typeof CATEGORY !== 'undefined') {
        initChatPage();
    }
    
    // 채팅 아이콘 클릭 이벤트 (메인 페이지와 동일)
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

            document.body.appendChild(form);
            form.submit();
        });
    }
});

// 대화 페이지 초기화
function initChatPage() {
    const messageInput = document.getElementById('messageInput');
    const charCount = document.getElementById('charCount');
    const sendBtn = document.getElementById('sendBtn');
    
    // 카테고리별 추천 질문 표시
    displaySuggestions();
    
    // 입력 필드 이벤트
    if (messageInput) {
        messageInput.addEventListener('input', function() {
            updateCharCount();
            autoResize();
        });
        
        messageInput.addEventListener('keydown', function(e) {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                sendMessage();
            }
        });
    }
    
    // 문자 수 업데이트
    function updateCharCount() {
        const count = messageInput.value.length;
        charCount.textContent = `${count}/50`;
        
        if (count > 50) {
            charCount.style.color = '#e74c3c';
        } else {
            charCount.style.color = '#999';
        }
        
        // 전송 버튼 활성화/비활성화
        sendBtn.disabled = count === 0 || count > 50;
    }
    
    // 텍스트 영역 자동 크기 조절
    function autoResize() {
        messageInput.style.height = 'auto';
        messageInput.style.height = Math.min(messageInput.scrollHeight, 120) + 'px';
    }
}

// 카테고리별 추천 질문 표시
function displaySuggestions() {
    const suggestionsList = document.getElementById('suggestionsList');
    if (!suggestionsList) return;
    
    const suggestions = getSuggestionsByCategory(CATEGORY);
    
    suggestionsList.innerHTML = '';
    suggestions.forEach(suggestion => {
        const item = document.createElement('div');
        item.className = 'suggestion-item';
        item.textContent = suggestion;
        item.addEventListener('click', () => {
            document.getElementById('messageInput').value = suggestion;
            updateCharCount();
        });
        suggestionsList.appendChild(item);
    });
}

// 카테고리별 추천 질문 데이터
function getSuggestionsByCategory(category) {
    const suggestions = {
        design: [
            'NFT 아트 디자인 의뢰하고 싶어요.',
            '웹사이트 UI 디자인 의뢰하고 싶어요.',
            '브랜드 로고 디자인을 만들고 싶어요.'
        ],
        programming: [
            '웹사이트 개발을 의뢰하고 싶어요.',
            '모바일 앱 개발이 필요해요.',
            'API 개발 및 연동이 필요해요.'
        ],
        video: [
            '유튜브 영상 편집을 맡기고 싶어요.',
            '홍보영상 제작이 필요해요.',
            '제품 소개 영상을 만들고 싶어요.'
        ],
        legal: [
            '법인 세무 상담이 필요해요.',
            '계약서 검토를 의뢰하고 싶어요.',
            '근로계약서 작성을 도와주세요.'
        ],
        translation: [
            '영어 문서 번역이 필요해요.',
            '비즈니스 회의 통역을 의뢰하고 싶어요.',
            '웹사이트 현지화 작업이 필요해요.'
        ]
    };
    
    return suggestions[category] || [];
}

// 메시지 전송
async function sendMessage() {
    const messageInput = document.getElementById('messageInput');
    const message = messageInput.value.trim();
    
    if (!message || message.length > 50) {
        return;
    }
    
    // 사용자 메시지 추가
    addMessage(message, 'user');
    
    // 입력 필드 초기화
    messageInput.value = '';
    updateCharCount();
    messageInput.style.height = 'auto';
    
    // 로딩 메시지 표시
    const loadingId = addLoadingMessage();
    
    try {
        // AI 응답 요청
        const response = await fetch('/recommend/suggest', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `category=${encodeURIComponent(CATEGORY)}&message=${encodeURIComponent(message)}`
        });
        
        const data = await response.json();
        
        // 로딩 메시지 제거
        removeLoadingMessage(loadingId);
        
        if (data.success) {
            addMessage(data.message, 'ai');
            
            // 전문가 정보가 있으면 전문가 카드 추가
            if (data.expertInfo) {
                addExpertCard(data.expertInfo);
            }
        } else {
            addMessage('죄송합니다. 오류가 발생했습니다. 다시 시도해주세요.', 'ai');
        }
        
    } catch (error) {
        // 로딩 메시지 제거
        removeLoadingMessage(loadingId);
        
        console.error('Error:', error);
        addMessage('네트워크 오류가 발생했습니다. 다시 시도해주세요.', 'ai');
    }
}

// 메시지 추가
function addMessage(content, type) {
    const chatMessages = document.getElementById('chatMessages');
    
    const messageDiv = document.createElement('div');
    messageDiv.className = `${type}-message`;
    
    const contentDiv = document.createElement('div');
    contentDiv.className = 'message-content';
    
    // 줄바꿈 처리
    contentDiv.innerHTML = content.replace(/\n/g, '<br>');
    
    messageDiv.appendChild(contentDiv);
    chatMessages.appendChild(messageDiv);
    
    // 스크롤을 맨 아래로
    chatMessages.scrollTop = chatMessages.scrollHeight;
}

// 로딩 메시지 추가
function addLoadingMessage() {
    const chatMessages = document.getElementById('chatMessages');
    
    const messageDiv = document.createElement('div');
    messageDiv.className = 'ai-message';
    
    const contentDiv = document.createElement('div');
    contentDiv.className = 'message-content loading-message';
    contentDiv.innerHTML = `
        <span>답변을 준비하고 있어요</span>
        <div class="loading-dots">
            <span></span>
            <span></span>
            <span></span>
        </div>
    `;
    
    messageDiv.appendChild(contentDiv);
    chatMessages.appendChild(messageDiv);
    
    // 스크롤을 맨 아래로
    chatMessages.scrollTop = chatMessages.scrollHeight;
    
    return messageDiv;
}

// 로딩 메시지 제거
function removeLoadingMessage(loadingElement) {
    if (loadingElement && loadingElement.parentNode) {
        loadingElement.parentNode.removeChild(loadingElement);
    }
}

// 문자 수 업데이트 (전역 함수)
function updateCharCount() {
    const messageInput = document.getElementById('messageInput');
    const charCount = document.getElementById('charCount');
    const sendBtn = document.getElementById('sendBtn');
    
    if (messageInput && charCount && sendBtn) {
        const count = messageInput.value.length;
        charCount.textContent = `${count}/50`;
        
        if (count > 50) {
            charCount.style.color = '#e74c3c';
        } else {
            charCount.style.color = '#999';
        }
        
        sendBtn.disabled = count === 0 || count > 50;
    }
}

// 전문가 카드 추가
function addExpertCard(expertInfo) {
    const chatMessages = document.getElementById('chatMessages');
    
    const messageDiv = document.createElement('div');
    messageDiv.className = 'ai-message';
    
    const contentDiv = document.createElement('div');
    contentDiv.className = 'message-content expert-card';
    
    const cardHTML = `
        <div class="expert-info">
            <div class="expert-header">
                <div class="expert-avatar">
                    👨‍💼
                </div>
                <div class="expert-details">
                    <h3 class="expert-name">${expertInfo.name}</h3>
                    <p class="expert-school">${expertInfo.school} • ${expertInfo.major}</p>
                </div>
            </div>
            
            <div class="expert-description">
                <p><strong>✨ AI 추천 이유:</strong></p>
                <p>${expertInfo.recommendation}</p>
            </div>
            
            <div class="expert-stats">
                <div class="stat-item">
                    <span class="stat-label">💰 급여</span>
                    <span class="stat-value">${expertInfo.salary}만원 ${expertInfo.negoYn ? '(협의가능)' : ''}</span>
                </div>
                <div class="stat-item">
                    <span class="stat-label">📊 완료 의뢰</span>
                    <span class="stat-value">${expertInfo.completedRequestCount}건</span>
                </div>
            </div>
            
            <div class="expert-contact">
                <p><strong>📧 연락처:</strong> ${expertInfo.email}</p>
            </div>
            
            <div class="expert-actions">
                <button class="profile-btn" onclick="window.open('${expertInfo.profileUrl}', '_blank')">
                    🔗 프로필 상세보기
                </button>
            </div>
        </div>
    `;
    
    contentDiv.innerHTML = cardHTML;
    messageDiv.appendChild(contentDiv);
    chatMessages.appendChild(messageDiv);
    
    // 스크롤을 맨 아래로
    chatMessages.scrollTop = chatMessages.scrollHeight;
}

// 랜덤 문자열 생성 (채팅방 이름용)
function getRandomString(length) {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    let result = '';
    for (let i = 0; i < length; i++) {
        result += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    return result;
}