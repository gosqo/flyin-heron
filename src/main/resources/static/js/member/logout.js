function logoutConfirm() {
    const confirmation = confirm('로그아웃 하시겠습니까?');
    return confirmation;
}

async function fetchLogout() {
    const url = '/api/v1/auth/logout';
    const requestInit = {
        headers: {
            'Authorization': tokenOnHeader,
        },
        method: 'POST',
    };

    try {

        const response = await fetch(url, requestInit);
        console.log(response);
        console.log(response.body);

        if (response.status === 200) {

            const result = await response.text();
            console.log(result);
            sessionStorage.removeItem('jwt');

            alert('로그아웃했습니다.');

            self.location = '/';

        } else /*if (response.status === 500)*/ {
            const result = await response.json();
            console.log(result);
            alert('logout failed.');

        }

    } catch (error) {
        console.error("Error: ", error);
    }
}

logoutButton.addEventListener('click', (event) => {

    console.log(event.target);
    event.preventDefault();

    // 로그아웃 컨펌 받는다.
    if (logoutConfirm()) {

        // 컨펌 결과에 따라 퐽취, 엘스 리턴.
        fetchLogout()

    } else return;
    
});
