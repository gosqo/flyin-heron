import FormUtility from "../common/FormUtility.js";

export class SignUpDOM {
    submitButton = document.querySelector("#submit-form-btn");

    addSubmitEvent() {
        this.submitButton.addEventListener("click", async (event) => {
            event.preventDefault();
            this.registerMember();
        });
    }

    async registerMember() {
        const body = FormUtility.formToBody();
        const url = "/api/v1/member";
        const options = {
            headers: {
                "Content-Type": "application/json",
            },
            method: "POST",
            body: JSON.stringify(body)
        };

        try {
            const response = await fetch(url, options);

            if (!response.ok) {
                const data = await response.json();
                alert(data.message);
                return;
            }

            const data = await response.text();
            alert(data);
            location.replace("/login");
        } catch (error) {
            console.error(error);
        }
    }
}