// fetch(`/workrequest/${REQUEST_ID}/matched`, {
//     method: 'POST'
// })
//     .then(res => res.text())
//     .then(result => {
//         if (result === "already") {
//             alert("이미 수주하신 의뢰입니다.");
//         } else {
//             location.href = `/workrequest/${REQUEST_ID}/matched`;
//         }
//     })
//     .catch(() => alert("수주 중 오류가 발생했습니다."));