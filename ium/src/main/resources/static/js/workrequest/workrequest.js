// 채팅방 이동
function getRandomString(length) {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    let result = '';
    for (let i = 0; i < length; i++) {
        result += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    return result;
}

document.querySelector('.work-request-chat-icon').addEventListener('click', function () {
    let targetUser = document.querySelector('.work-request-chat-icon').dataset.targetUser;

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
    targetUserInput.value = targetUser;
    form.appendChild(targetUserInput);

    document.body.appendChild(form);
    form.submit();
});
