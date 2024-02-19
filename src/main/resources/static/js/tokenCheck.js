const token = sessionStorage.getItem('jwt');

if (token) {
    console.log('token exist.');
}
