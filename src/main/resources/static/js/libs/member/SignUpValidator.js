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
        SubmitChecker.submitButton.disabled = SubmitChecker.isAllPassed() ? false : true;
    }

    static isAllPassed() {
        return UniqueChecker.email.status === CheckStatus.PASS
            && UniqueChecker.nickname.status === CheckStatus.PASS
            && ValidChecker.email.status === CheckStatus.PASS
            && ValidChecker.password.status === CheckStatus.PASS
            && ValidChecker.passwordCheck.status === CheckStatus.PASS
            && ValidChecker.nickname.status === CheckStatus.PASS
            && ValidChecker.isSamePassword.status === CheckStatus.PASS
    }
}

class Regex {
    static email = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z]){0,63}@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z]){0,251}\.[a-zA-Z]{2,3}$/;
    static password = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d|.*[\W\S])[A-Za-z\d|\W\S]{8,20}$/;
    static nickname = /^[0-9a-zA-Z가-힣]{2,20}$/;
    static { Object.freeze(this); }
}

class Messenger {
    static addResultMessage(target, data, appendingId) {
        const messageElement = DomCreate.small(`${target.name}${appendingId}`, null, data.message);
        Messenger.Utility.appendResultMessage(data.status, target, messageElement);
    }

    static Utility = class {
        static removeMessageIfExist(target, appendingId) {
            const targetElement = document.querySelector(`#${target.name}${appendingId}`);
            if (targetElement) targetElement.remove();
        }

        static appendResultMessage(status, target, messageElement) {
            Messenger.Utility.colorMessage(status, messageElement);
            target.closest("div").nextElementSibling.nextElementSibling.append(messageElement);
        }

        static colorMessage(status, message) {
            message.style.color = status === CheckStatus.PASS ? "green" : "red";
        }
    }

    static Messages = class {
        static email = {
            matched: "형식에 적합한 입력입니다. 중복 확인을 해주세요."
            , unmatched: "email 형식을 확인해주세요."
        };
        static password = {
            matched: "형식에 적합한 입력입니다."
            , unmatched: "비밀번호는 8 ~ 20 자리, 영문 대소문자와 숫자 혹은 특수문자를 하나 이상 조합해주세요."
        };
        static passwordCheck = {
            matched: "형식에 적합한 입력입니다."
            , unmatched: "비밀번호는 8 ~ 20 자리, 영문 대소문자와 숫자 혹은 특수문자를 하나 이상 조합해주세요."
        };
        static nickname = {
            matched: "형식에 적합한 입력입니다. 중복 확인을 해주세요."
            , unmatched: "닉네임은 2 ~ 20 자리, 숫자, 영/한문과 특수문자{'-', '_', '.'} 을 사용해 구성할 수 있습니다."
        };
        static isSamePassword = {
            matched: "비밀번호가 일치합니다."
            , unmatched: "비밀번호 입력값과 비밀번호 확인의 입력값이 일치하지 않습니다."
        };
    }
}

export class ValidChecker {
    static email = {
        element: document.querySelector("#email")
        , matched: Messenger.Messages.email.matched
        , unmatched: Messenger.Messages.email.unmatched
        , status: CheckStatus.FAIL
        , message: ""
        , regex: Regex.email
    };
    static password = {
        element: document.querySelector("#password")
        , matched: Messenger.Messages.password.matched
        , unmatched: Messenger.Messages.password.unmatched
        , status: CheckStatus.FAIL
        , message: ""
        , regex: Regex.password
    };
    static passwordCheck = {
        element: document.querySelector("#passwordCheck")
        , matched: Messenger.Messages.passwordCheck.matched
        , unmatched: Messenger.Messages.passwordCheck.unmatched
        , status: CheckStatus.FAIL
        , message: ""
        , regex: Regex.password
    };
    static nickname = {
        element: document.querySelector("#nickname")
        , matched: Messenger.Messages.nickname.matched
        , unmatched: Messenger.Messages.nickname.unmatched
        , status: CheckStatus.FAIL
        , message: ""
        , regex: Regex.nickname
    };
    static isSamePassword = {
        element1: document.querySelector("#password")
        , element2: document.querySelector("#passwordCheck")
        , matched: Messenger.Messages.isSamePassword.matched
        , unmatched: Messenger.Messages.isSamePassword.unmatched
        , status: CheckStatus.FAIL
        , message: ""
    }

    addValidEmailEvent() {
        this.addIsValidEvent("email", ValidChecker.email);
    }

    addValidPasswordEvent() {
        this.addIsValidEvent("password", ValidChecker.password);
    }

    addValidPasswordCheckEvent() {
        this.addIsValidEvent("passwordCheck", ValidChecker.passwordCheck);
    }

    addValidNicknameEvent() {
        this.addIsValidEvent("nickname", ValidChecker.nickname);
    }

    addIsSamePasswordEvent() {
        this.addIsSameEvent(ValidChecker.isSamePassword)
    }

    addIsSameEvent(object) {
        object.element1.addEventListener("input", () => {
            Messenger.Utility.removeMessageIfExist(object.element1, "ResultIsSame");
            Messenger.Utility.removeMessageIfExist(object.element2, "ResultIsSame");

            if (ValidChecker.password.status === CheckStatus.FAIL
                || ValidChecker.passwordCheck.status === CheckStatus.FAIL
            ) return;

            this.checkSame(object.element1, object.element2, object);
            this.setMessageOf(object);

            Messenger.Utility.removeMessageIfExist(object.element1, "ResultIsValid")

            if (object.status === CheckStatus.PASS) {
                Messenger.Utility.removeMessageIfExist(object.element2, "ResultIsValid")
            }

            Messenger.addResultMessage(object.element1, object, "ResultIsSame");
            SubmitChecker.changeSubmitAvailability;
        });

        object.element2.addEventListener("input", () => {
            Messenger.Utility.removeMessageIfExist(object.element1, "ResultIsSame");
            Messenger.Utility.removeMessageIfExist(object.element2, "ResultIsSame");

            if (ValidChecker.password.status === CheckStatus.FAIL
                || ValidChecker.passwordCheck.status === CheckStatus.FAIL
            ) return;

            this.checkSame(object.element1, object.element2, object);
            this.setMessageOf(object);

            Messenger.Utility.removeMessageIfExist(object.element2, "ResultIsValid")

            if (object.status === CheckStatus.PASS) {
                Messenger.Utility.removeMessageIfExist(object.element1, "ResultIsValid")
            }

            Messenger.addResultMessage(object.element2, object, "ResultIsSame");
            SubmitChecker.changeSubmitAvailability();
        });
    }

    checkSame(target1, target2, object) {
        object.status = target1.value === target2.value ? CheckStatus.PASS : CheckStatus.FAIL;
    }

    addIsValidEvent(targetName, object) {
        const target = document.querySelector(`#${targetName}`);
        target.addEventListener("input", () => {
            Messenger.Utility.removeMessageIfExist(target, "ResultIsUnique");
            Messenger.Utility.removeMessageIfExist(target, "ResultIsValid");

            this.checkValueValid(target, object);
            this.setMessageOf(object); // object.matched, unmatched class Message 로 분리

            Messenger.addResultMessage(target, object, "ResultIsValid");
            SubmitChecker.changeSubmitAvailability();
        });
    }

    setMessageOf(object) {
        object.message = object.status === CheckStatus.PASS ? object.matched : object.unmatched;
    }

    checkValueValid(target, object) {
        object.status = object.regex.test(target.value) ? CheckStatus.PASS : CheckStatus.FAIL;
    }
}

/**
 * isUnique 버튼 이벤트 추가, 서버 isUnique API 통신.
 */
export class UniqueChecker {
    static email = {
        inputElement: document.querySelector("#email")
        , status: CheckStatus.FAIL
    };
    static nickname = {
        inputElement: document.querySelector("#nickname")
        , status: CheckStatus.FAIL
    };

    addUniqueEmailEvent() {
        this.addIsUniqueEvent("email", UniqueChecker.email);
        this.UniqueFailIfReviseInput(UniqueChecker.email);
    }

    addUniqueNicknameEvent() {
        this.addIsUniqueEvent("nickname", UniqueChecker.nickname);
        this.UniqueFailIfReviseInput(UniqueChecker.nickname);
    }

    addIsUniqueEvent(targetName, toCheck) {
        const targetButton = document.querySelector(`#is-unique-${targetName}-button`);
        targetButton.addEventListener("click", async (event) => {
            event.preventDefault();
            const target = document.querySelector(`input[name="${targetName}"]`);

            Messenger.Utility.removeMessageIfExist(target, "ResultIsUnique");
            Messenger.Utility.removeMessageIfExist(target, "ResultIsValid");

            const data = await this.checkIsUnique(target);

            this.statusToFlag(data);
            toCheck.status = data.status;

            Messenger.addResultMessage(target, data, "ResultIsUnique");
            SubmitChecker.changeSubmitAvailability();
        });
    }

    UniqueFailIfReviseInput(object) {
        object.inputElement.addEventListener("input", () => {
            object.status = CheckStatus.FAIL;
        });
    }

    statusToFlag(data) {
        data.status = data.status === 200 ? CheckStatus.PASS : CheckStatus.FAIL;
    }

    async checkIsUnique(targetElement) {
        const capitalizedName = StringUtility.capitalize(targetElement);
        // TODO FormUtility.inputToBody 생성할 것.
        let body;
        if (targetElement.name === "email") {
            body = { email: targetElement.value };
        } else if (targetElement.name === "nickname") {
            body = { nickname: targetElement.value };
        }
        const url = `/api/v1/member/isUnique${capitalizedName}`;
        const options = {
            headers: {
                "Content-Type": "application/json",
            },
            method: "POST",
            body: JSON.stringify(body)
        };

        try {
            const response = await fetch(url, options);
            return response.json();
        } catch (error) {
            console.error("Error: ", error);
        }
    }
}

// 반복되는 호출, 전역에서 일관된 값을 참조. static properties.

