document.addEventListener("DOMContentLoaded", () => {
    const matchForm = document.querySelector("#matchForm");

    if (matchForm) {
        matchForm.addEventListener("submit", function (e) {
            e.preventDefault(); // 기본 제출 막고
            if (confirm("정말 이 의뢰를 수주하시겠습니까?")) {
                matchForm.submit(); // 바로 submit만
            }
        });
    }
});