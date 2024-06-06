const email = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z]){0,63}@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z]){0,251}\.[a-zA-Z]{2,3}$/;
const password = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d|.*[!@#$%^&*\(\)_+`~\-=\[\]\{\}\\\|;':",\./<>?₩])[A-Za-z\d!@#$%^&*\(\)_+`~\-=\[\]\{\}\\\|;':",\./<>?₩]{8,20}$/;

const idsPass = [
    "heron@vong.com"
]

const idsFail = [
    "aaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbB@aaaaa.com"
    , "aa@aaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbB.com"
    , "aa@aaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaaaabbbbBaaa.com"
]

const passwordsPass = [
    "paSsword!"
    , "paSsword()"
    , "pA!@#$%^&*()_+"
    , "pA-=`~[]{}\\|"
    , "pA|;':\",./<>?₩"
    , "Abc12345"
    , "StrongPwd2022!!"
    , "2PassWord"
    , "p2aSsWord@#"
    , "****pA****"
    , "asdfAsdf&7"
]

const passwordsFail = [
    "password1"
    , "PASSWORD1"
    , "P@ssw0rd1234567890123"
    , "12345678"
    , "*&^*&$^#&"
    , "  &*( &*( "
    , "asdf Asd f&7" // \s space
    , "ASDasADSsd"
]

const pass = "true";
const fail = "false";

function test(regex, datum, expect) {
    result = regex.test(datum)
    console.log(`"${result}"`, "expected", expect, "datum:", datum);
}

idsPass.forEach(id => {
    test(email, id, pass);
});

idsFail.forEach(id => {
    test(email, id, fail);
});

console.log(password + "\n");

passwordsPass.forEach(item => {
    test(password, item, pass);
});

console.log("==============");

passwordsFail.forEach(item => {
    test(password, item, fail);
});
