window.addEventListener('load', () => {
    if (localStorage.getItem('access_token')) {
        const logoutButton = document.querySelector('#logoutButton');
    
        if (logoutButton) {
            logoutButton.addEventListener('click', (event) => {
                event.preventDefault();
    
                if (logoutConfirm()) fetchLogout();
                else return;
    
            });
        }
    }
});

function logoutConfirm() {
    const confirmation = confirm('로그아웃 하시겠습니까?');
    return confirmation;
}

async function fetchLogout() {
    const url = '/api/v1/auth/logout';
    let requestInit = {
        headers: {
            'Authorization': localStorage.getItem('refresh_token'),
        },
        method: 'POST',
    };

    try {
        const data = await fetchWithToken(url, requestInit);

        if (data.status === 200) {
            localStorage.removeItem('access_token');
            localStorage.removeItem('refresh_token');

            alert('로그아웃했습니다.');

            location = '/';

        } else {
            alert('안전한 서비스 이용을 위해 강제 로그아웃을 진행합니다.');

            if (localStorage.getItem('access_token')) 
                localStorage.removeItem('access_token');
            if (localStorage.getItem('refresh_token')) 
                localStorage.removeItem('refresh_token');

            location.reload();
        }
    } catch (error) {
        console.error("Error: ", error);
    }
}
