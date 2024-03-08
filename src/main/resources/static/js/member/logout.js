if (localStorage.getItem('access_token')) {
    const logoutButton = document.querySelector('#logoutButton');

    logoutButton.addEventListener('click', (event) => {

        event.preventDefault();

        // 로그아웃 컨펌 받는다.
        if (logoutConfirm()) {

            // 컨펌 결과에 따라 퐽취, 엘스 리턴.
            fetchLogout()

        } else return;
        
    });

    function logoutConfirm() {
        const confirmation = confirm('로그아웃 하시겠습니까?');
        return confirmation;
    }

    async function fetchLogout() {
        const url = '/api/v1/auth/logout';
        const requestInit = {
            headers: {
                'Authorization': localStorage.getItem('refresh_token'),
            },
            method: 'POST',
        };

        try {

            const data = await fetchWithToken(url, requestInit);
            console.log(data);

            if (data.statusCode === 200) {

                localStorage.removeItem('access_token');
                localStorage.removeItem('refresh_token');

                alert('로그아웃했습니다.');

                self.location = '/';

            } else {

                alert('logout failed.');

            }

        } catch (error) {
            console.error("Error: ", error);
        }
    }
}