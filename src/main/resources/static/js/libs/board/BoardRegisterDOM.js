import Fetcher from "../common/Fetcher.js";
import FormUtility from "../common/FormUtility.js";
import DomCreate from "../dom/DomCreate.js";

export class BoardNewDOM {
    addButtons() {
        const buttonsArea = document.querySelector("#buttons-area");

        const submitButton = DomCreate.button("submit-btn", "btn btn-primary", "Submit");
        submitButton.addEventListener("click", async () => {
            this.registerBoard();
        });
        buttonsArea.append(submitButton);

        const resetButton = DomCreate.button("reset-btn", "btn btn-secondary", "Reset");
        resetButton.addEventListener("click", () => {
            if (this.confirmReset())
                this.emptyInputs();
        });
        buttonsArea.append(resetButton);

        const cancelButton = DomCreate.button("cancel-btn", "btn btn-secondary", "Cancel");
        cancelButton.addEventListener("click", () => {
            if (this.confirmCancel()) {
                this.emptyInputs();
                history.back();
            }
        });
        buttonsArea.append(cancelButton);
    }

    async registerBoard() {
        const url = "/api/v1/board";
        let options = {
            headers: {
                "Content-Type": "application/json",
                "Authorization": localStorage.getItem("access_token")
            },
            method: "POST",
            body: JSON.stringify(FormUtility.formToBody())
        };

        try {
            const data = await Fetcher.fetchObjectWithAuth(url, options);

            if (data.message.includes("Validation")) {
                alert(data.errors[0].defaultMessage);
                return;
            }

            if (data.id === undefined) {
                alert(data.message);
                return;
            }

            alert(data.message);
            location.replace(`/board/${data.id}`);
        } catch (error) {
            console.error("Error: ", error);
        }
    }

    confirmCancel() {
        return confirm("작성을 취소하시겠습니까?\n 확인을 클릭 시, 작성 내용을 저장하지 않고 이전 페이지로 이동합니다.");
    }

    confirmReset() {
        return confirm("작성하신 내용을 지우시겠습니까?");
    }

    emptyInputs() {
        const inputs = document.querySelectorAll(".form-control");
        inputs.forEach((input) => {
            input.value = "";
        });
    }
}
