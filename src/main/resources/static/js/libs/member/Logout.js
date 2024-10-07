export default class Logout {
    static logoutConfirm() {
        return confirm("로그아웃 하시겠습니까?");
    }

    static logout() {
        const url = "/api/v1/auth/logout";
        let options = {
            headers: {
            },
            method: "POST",
        };

        fetch(url, options)
            .then(response => { 
                console.log(response);
                return response.json(); 
            })
            .then(data => {
                console.log(data); // data.status, data.message 접근 가능.

                localStorage.removeItem('access_token');

                location.reload();
            })
            .catch(error => {
                console.error("Error: ", error);
            });
    }
}
