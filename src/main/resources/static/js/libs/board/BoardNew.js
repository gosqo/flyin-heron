import { Fetcher } from "../common/Fetcher.js";
import FormUtility from "../common/FormUtility.js";
import { DomCreate } from "../dom/DomCreate.js";

export class BoardNew {
    static present() {
        addButtons();

        function addButtons() {
            const buttonsArea = document.querySelector("#buttons-area");

            addSubmitButton(buttonsArea);
            addResetButton(buttonsArea);
            addCancelButton(buttonsArea);

            function addCancelButton(buttonsArea) {
                const cancelButton = DomCreate.button("cancel-btn", "btn btn-secondary", "Cancel");
                const confirmCancel = function () {
                    return confirm("작성을 취소하시겠습니까?\n 확인을 클릭 시, 작성 내용을 저장하지 않고 이전 페이지로 이동합니다.");
                }

                cancelButton.addEventListener("click", () => {
                    if (confirmCancel()) {
                        FormUtility.emptyInputs();
                        history.back();
                    }
                });
                buttonsArea.append(cancelButton);
            }

            function addResetButton(buttonsArea) {
                const resetButton = DomCreate.button("reset-btn", "btn btn-secondary", "Reset");
                const confirmReset = function () {
                    return confirm("작성하신 내용을 지우시겠습니까?");
                }

                resetButton.addEventListener("click", () => {
                    if (confirmReset())
                        FormUtility.emptyInputs();
                });
                buttonsArea.append(resetButton);
            }

            function addSubmitButton(buttonsArea) {
                const submitButton = DomCreate.button("submit-btn", "btn btn-primary", "Submit");
                submitButton.addEventListener("click", async () => {
                    registerBoard();
                });
                buttonsArea.append(submitButton);

                async function registerBoard() {
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
                        const data = await Fetcher.withAuth(url, options);

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
            }
        }
    }
}
