export default class AuthChecker {
    static hasAuth() {
        // localStorage.getItem(item) 함수로 접근 시, Object 반환, 해당 Object 비어 있으면 null.
        // localStorage.access_token 으로 접근 시, Storage 객체 하위 접근 시, 정의되어있지 않으면 undefined.
        const accessToken = localStorage.getItem('access_token');

        return accessToken !== null && accessToken.startsWith("Bearer ");
    }
    
    static redirectToHome() {
        
            alert("접근 권한이 없습니다.");
            location.replace("/");
            return;
        
    }
}
