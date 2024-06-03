export default class AuthChecker {
    static hasAuth() {
        const accessToken = localStorage.getItem('access_token');
        return accessToken !== undefined && accessToken.startsWith('Bearer ');
    }
}
