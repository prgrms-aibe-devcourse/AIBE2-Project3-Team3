document.addEventListener('DOMContentLoaded', function () {
    const cancelBtn = document.querySelector('.cancel-button');
    if (cancelBtn) {
        cancelBtn.addEventListener('click', function () {
            if (!confirm("정말 취소하시겠습니까?")) return;

            // form 만들어서 POST로 제출
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = `/workrequest/${cancelBtn.dataset.id}/cancel`;
            document.body.appendChild(form);
            form.submit();
        });
    }
});