document.addEventListener("DOMContentLoaded", () => {
    const form = document.querySelector("form");

    if (!form) return; // form이 없으면 아무것도 안함

    form.addEventListener("submit", function (e) {
        e.preventDefault(); // 기본 제출 막기

        if (confirm("의뢰를 등록하시겠습니까?")) {
            alert("의뢰가 등록되었습니다.");
            form.submit(); // 진짜 제출
        }
    });
});