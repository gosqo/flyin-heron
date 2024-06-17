import FormUtility from "../common/FormUtility.js";
import TokenUtility from "../token/TokenUtility.js";

export class LoginDOM {
    addSubmitEvent() {
        const submitButton = document.querySelector("#submit-form-btn");
        submitButton.addEventListener("click", async (event) => {
            event.preventDefault();

            const body = FormUtility.formToBody();
            this.registerMember(body);
        });
    }

    async registerMember(body) {
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

            TokenUtility.saveTokens(data)
            alert("로그인했습니다.");
            location.replace("/");
        } catch (error) {
            console.error("Error: ", error);
        }
    }
}
