let selectedCredit = null;

document.querySelectorAll('.amount-box').forEach(box => {
    box.addEventListener('click', function() {
        document.querySelectorAll('.amount-box').forEach(b => b.classList.remove('selected'));
        this.classList.add('selected');
        selectedCredit = this.getAttribute('data-credit');
        console.log(selectedCredit);
    });
});

document.querySelector('.confirm-btn').addEventListener('click', function() {
    if (!selectedCredit) {
        alert('금액을 선택해주세요.');
        return;
    }

    fetch('/money/creditCharge', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(selectedCredit)
    })
        .then(response => {
            if (!response.ok) throw new Error('결제 요청 실패');
            return response.json();
        })
        .then(data => {
            location.href = data.result.redirectUrl;
        })
        .catch(error => {
            alert('에러 발생: ' + error.message);
        });
});

document.querySelector('.cancel-btn').addEventListener('click', function() {
    alert('결제가 취소되었습니다.');
});