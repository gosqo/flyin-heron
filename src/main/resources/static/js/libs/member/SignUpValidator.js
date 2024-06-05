import DomCreate from "../dom/DomCreate.js"
import { StringUtility } from "../common/StringUtility.js";

class CheckStatus {
    static PASS = "pass";
    static FAIL = "fail";
    static { Object.freeze(this) };
}

class SubmitChecker {
    static submitButton = document.querySelector("#submit-form-btn");

    static changeSubmitAvailability() {
        SubmitChecker.submitButton.disabled = ValidChecker.isAllValidated() ? false : true;
    }

    static isAllValidated() {
        return UniqueChecker.email === CheckStatus.PASS
            && UniqueChecker.nickname === CheckStatus.PASS
            && ValidChecker.email === CheckStatus.PASS
            && ValidChecker.password === CheckStatus.PASS
            && ValidChecker.passwordCheck === CheckStatus.PASS
            && ValidChecker.nickname === CheckStatus.PASS
    }
}

class SignUpRegex {
    static email = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z]){0,63}@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z]){0,251}\.[a-zA-Z]{2,3}$/;
    static password = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d|.*[\W\S])[A-Za-z\d|\W\S]{8,20}$/;
    static nickname = /^[0-9a-zA-Z가-힣]{2,20}$/;
    static { Object.freeze(this); }
}

export class ValidChecker {
    static email/* CheckStatus*/;
    static password/* CheckStatus*/;
    static passwordCheck/* CheckStatus*/;
    static nickname/* CheckStatus*/;

    addValidEmailEvent() {
        this.addIsValidEvent("email", ValidChecker.email);
    }

    addIsValidEvent(targetName, toCheck) {
        const target = document.querySelector(`#${targetName}`);
        target.addEventListener("input", () => {
            toCheck = SignUpRegex.email.test(target.value) ? CheckStatus.PASS : CheckStatus.FAIL;
        });
    }
}
Event
/**
 * isUnique 버튼 이벤트 추가, 서버 isUnique API 통신.
 */
export class UniqueChecker {
    static email/* CheckStatus*/;
    static nickname/* CheckStatus*/;

    static UniqueState = {
        PASS: "pass"
        , FAIL: "false"
    }

    addUniqueEmailEvent() {
        this.addIsUniqueEvent("email", UniqueChecker.email);
    }

    addUniqueNicknameEvent() {
        this.addIsUniqueEvent("nickname", UniqueChecker.nickname);
    }

    addIsUniqueEvent(targetName, toCheck) {
        const targetButton = document.querySelector(`#is-unique-${targetName}-button`);
        targetButton.addEventListener("click", async (event) => {
            event.preventDefault();
            const target = document.querySelector(`input[name="${targetName}"]`);

            Messenger.Utility.removeMessageIfExist(target, "ResultIsUnique");

            const data = await this.checkIsUnique(target);

            Messenger.Utility.addResultIsUnique(target, data);
            this.flag(data.status, toCheck)
            SubmitChecker.changeSubmitAvailability();
        });
    }

    async checkIsUnique(targetElement) {
        const capitalizedName = StringUtility.capitalize(targetElement);
        const url = `/api/v1/member/isUnique${capitalizedName}`;
        const options = {
            headers: {
                "Content-Type": "application/json",
            },
            method: "POST",
            body: JSON.stringify({ "email": targetElement.value })
        };

        try {
            const response = await fetch(url, options);
            return response.json();
        } catch (error) {
            console.error("Error: ", error);
        }
    }

    flag(status, toCheck) {
        toCheck = (status === 200) ? CheckStatus.PASS : CheckStatus.FAIL;
    }
}

// 반복되는 호출, 전역에서 일관된 값을 참조. static properties.
export class Messenger {
    static addResultIsUnique(target, data) {
        Messenger.Utility.removeMessageIfExist(target, "ResultIsUnique");

        const resultMessage =
            DomCreate.small(`${target.name}ResultIsUnique`, null, data.message);

        Messenger.Utility.addResultMessage(data.status, target, resultMessage);
    }

    static Utility = class {
        static removeMessageIfExist(target, appendingId) {
            const targetElement = document.querySelector(`#${target.name}${appendingId}`);
            if (targetElement) targetElement.remove();
        }

        static addResultMessage(status, target, resultMessage) {
            MessengerUtility.colorMessage(status, resultMessage);
            target.closest("div").nextElementSibling.nextElementSibling.append(resultMessage);
        }

        static colorMessage(status, message) {
            message.style.color = status === 200 ? "green" : "red";
        }
    }
}
