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

    static inputToBody(element) {
        const key = element.name;
        const value = element.value;
        const body = {};
        body[key] = value;

        return body;
    }
}
