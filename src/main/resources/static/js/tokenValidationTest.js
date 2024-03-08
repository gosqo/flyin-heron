if (localStorage.getItem('access_token')) {
    const tokenValidationButton = document.querySelector('#jwtValidationButton');

    tokenValidationButton.addEventListener('click', (event) => {
        event.preventDefault();
        // console.log(event.target);
        tokenValidationCheck();
    });

    /**
     * 요청 헤더에 액세스 토큰을 담아 요청합니다.
     * 요청이 성공적이라면, document.body 에 응답 객체를 추가하여 보여줍니다.
     * 요청에 실패하면 콘솔에 예외를 출력합니다.
     */
    async function tokenValidationCheck() {
        const url = '/tokenValidationTest';
        let options = {
            headers: {
                'Authorization': localStorage.getItem('access_token'),
            }
        };

        try {

            // url, options 를 담아 fetchWithToken 함수 호출.
            // 함수 호출이 성공적이라면 json 형태의 응답 객체를 data 에 할당.
            const data = await fetchWithToken(url, options);

            // data 객체의 요소를 사용해 해당 함수(tokenValidationTest)의 역할을 수행.
            const paragraph = document.createElement('p');
            document.body.append(paragraph);
            paragraph.textContent = data.email + " " + data.expiration;

        // 오류 발생 시 콘솔 프린트.
        } catch (error) {
            console.error('Error' + error);
        }
    }
}
