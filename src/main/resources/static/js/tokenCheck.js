const token = sessionStorage.getItem('jwt');

if (token) {
    console.log('token exist.');
} else {
    console.log('no token exist.');
}
