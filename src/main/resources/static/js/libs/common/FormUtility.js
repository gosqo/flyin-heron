export default class FormUtility {
    static formToBody() {
        const form = document.querySelector("#form");
        const formData = new FormData(form);
        const body = {};
        formData.forEach((value, key) => {
            body[key] = value;
        });

        return body;
    }
}