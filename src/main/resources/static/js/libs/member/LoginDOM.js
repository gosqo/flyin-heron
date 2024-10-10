import { FormUtility } from "../common/FormUtility.js";
import { TokenUtility } from "../token/TokenUtility.js";

export class LoginDOM {
    addSubmitEvent() {
        const submitButton = document.querySelector("#submit-form-btn");

        submitButton.addEventListener("click", (event) => {
            this.submitEventHandler(event);
        });
    }

    async submitEventHandler(event) {
        event.preventDefault();

        const body = FormUtility.formToBody();
        const data = await this.requestAuthenticate(body);

        if (data === undefined) return;

        TokenUtility.saveToken(data)
        alert("환영합니다.");
        location.replace("/");
    }

    async requestAuthenticate(body) {
        const url = "/api/v1/auth/authenticate";
        const options = {
            headers: {
                "Content-Type": "application/json",
            },
            method: "POST",
            body: JSON.stringify(body),
        };

        try {
            const response = await fetch(url, options);
            const data = await response.json();

            if (!response.ok) {
                alert(data.message);
                return;
            }

            return data;
        } catch (error) {
            console.error("Error: ", error);
        }
    }
}
