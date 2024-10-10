import { DomCreate } from "../dom/DomCreate.js"
import { FormUtility } from "../common/FormUtility.js";
import { StringUtility } from "../common/StringUtility.js";

class CheckStatus {
    static PASS = "pass";
    static FAIL = "fail";
    static { Object.freeze(this) };
}

class SubmitChecker {
    static submitButton = document.querySelector("#submit-form-btn");

    static checkSubmitAvailability() {
        SubmitChecker.submitButton.disabled = isAllPassed() ? false : true;

        function isAllPassed() {
            return UniqueChecker.email.status === CheckStatus.PASS
                && UniqueChecker.nickname.status === CheckStatus.PASS
                && PatternMatcher.email.status === CheckStatus.PASS
                && PatternMatcher.password.status === CheckStatus.PASS
                && PatternMatcher.passwordCheck.status === CheckStatus.PASS
                && PatternMatcher.nickname.status === CheckStatus.PASS
                && IsSameChecker.isSamePassword.status === CheckStatus.PASS
        }
    }
}

class Regex {
    static email = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z]){0,63}@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z]){0,251}\.[a-zA-Z]{2,3}$/;
    static password = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d|.*[!@#$%^&*\(\)_+`~\-=\[\]\{\}\\\|;':",\./<>?₩])[A-Za-z\d!@#$%^&*\(\)_+`~\-=\[\]\{\}\\\|;':",\./<>?₩]{8,20}$/;
    static nickname = /^[0-9a-zA-Z가-힣]{2,20}$/;
    static { Object.freeze(this); }
}

class Messenger {
    static removeMessageIfExist(target, appendingId) {
        const name = target.name;
        const targetElement = document.querySelector(`#${name}${appendingId}`);

        if (targetElement) targetElement.remove();
    }

    static removeIsSameBothElement(object) {
        const target1 = object.element1;
        const target2 = object.element2;

        Messenger.removeMessageIfExist(target1, "IsSame");
        Messenger.removeMessageIfExist(target2, "IsSame");
    }

    static removeIsUniqueIsMatched(object) {
        const target = object.element;

        Messenger.removeMessageIfExist(target, "IsUnique");
        Messenger.removeMessageIfExist(target, "IsMatched");
    }

    static setMessageOf(object) {
        object.message = object.status === CheckStatus.PASS ? object.passMessage : object.failMessage;
    }

    static addResultMessage(target, object, appendingId) {
        const name = target.name;
        const message = object.message;
        const status = object.status;
        const messageElement = DomCreate.small(`${name}${appendingId}`, null, message);

        appendResultMessage(status, target, messageElement);

        function appendResultMessage(status, target, messageElement) {
            colorMessage(status, messageElement);
            target.closest("div").nextElementSibling.nextElementSibling.append(messageElement);

            function colorMessage(status, message) {
                message.style.color = status === CheckStatus.PASS ? "green" : "red";
            }
        }
    }
}

export class PatternMatcher {
    static email = {
        element: document.querySelector("#email")
        , passMessage: "형식에 적합한 입력입니다. 중복 확인을 해주세요."
        , failMessage: "email 형식을 확인해주세요."
        , status: CheckStatus.FAIL
        , message: ""
        , regex: Regex.email
    };
    static password = {
        element: document.querySelector("#password")
        , passMessage: "형식에 적합한 입력입니다."
        , failMessage: "비밀번호는 8 ~ 20 자리, 영문 대소문자와 숫자 혹은 특수문자를 하나 이상 조합해주세요."
        , status: CheckStatus.FAIL
        , message: ""
        , regex: Regex.password
    };
    static passwordCheck = {
        element: document.querySelector("#passwordCheck")
        , passMessage: PatternMatcher.password.passMessage
        , failMessage: PatternMatcher.password.failMessage
        , status: CheckStatus.FAIL
        , message: ""
        , regex: Regex.password
    };
    static nickname = {
        element: document.querySelector("#nickname")
        , passMessage: "형식에 적합한 입력입니다. 중복 확인을 해주세요."
        , failMessage: "닉네임은 2 ~ 20 자리, 숫자, 영/한문과 특수문자{'-', '_', '.'} 을 사용해 구성할 수 있습니다."
        , status: CheckStatus.FAIL
        , message: ""
        , regex: Regex.nickname
    };
    static fieldList = [
        PatternMatcher.email
        , PatternMatcher.password
        , PatternMatcher.passwordCheck
        , PatternMatcher.nickname
    ];

    addAllIsMatchedEvent() {
        PatternMatcher.fieldList.forEach((item) => {
            this.addIsMatchedEvent(item);
        });
    }

    addIsMatchedEvent(object) {
        const target = object.element;

        target.addEventListener("input", () => {
            this.isMatchedEventHandler(object);
        });
    }

    isMatchedEventHandler(object) {
        const target = object.element;
        Messenger.removeIsUniqueIsMatched(object);

        setStatusOf(object); // check matches internally and then set status.
        Messenger.setMessageOf(object); // object.passMessage, failMessage class Message 로 분리

        Messenger.addResultMessage(target, object, "IsMatched");
        SubmitChecker.checkSubmitAvailability();

        function setStatusOf(object) {
            object.status = matches(object) ? CheckStatus.PASS : CheckStatus.FAIL;

            function matches(object) {
                return object.regex.test(object.element.value);
            }
        }
    }
}

/**
 * isUnique 버튼 이벤트 추가, 버튼은 서버 isUnique API 통신.
 * 
 * * 해당 기능의 메세지는 모두 **서버가 보내는 메세지 사용**. 
 */
export class UniqueChecker {
    static email = {
        element: document.querySelector("#email")
        , button: document.querySelector("#is-unique-email-button")
        , status: CheckStatus.FAIL
        , message: ""
    };
    static nickname = {
        element: document.querySelector("#nickname")
        , button: document.querySelector("#is-unique-nickname-button")
        , status: CheckStatus.FAIL
        , message: ""
    };
    static fieldList = [
        UniqueChecker.email
        , UniqueChecker.nickname
    ]

    addAllIsUniqueEvent() {
        UniqueChecker.fieldList.forEach((item) => {
            this.addIsUniqueEvent(item);
        });
    }

    addIsUniqueEvent(object) {
        const button = object.button;
        const element = object.element;

        button.addEventListener("click", (event) => {
            this.isUniqueEventHandler(object, event);
        });

        element.addEventListener("input", () => {
            this.UniqueFailIfInputRevise(object);
        });
    }

    async isUniqueEventHandler(object, event) {
        event.preventDefault();
        const target = object.element;

        Messenger.removeIsUniqueIsMatched(object);

        // data from server.
        const data = await checkIsUnique(target);

        setDataToObject(data, object);

        Messenger.addResultMessage(target, object, "IsUnique");
        SubmitChecker.checkSubmitAvailability(); // end of method

        async function checkIsUnique(targetElement) {
            const capitalizedName = StringUtility.capitalize(targetElement);
            const body = FormUtility.inputToBody(targetElement);
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

        function setDataToObject(data, object) {
            statusToFlag(data, object);
            dataMessageToObject(data, object);

            function statusToFlag(data, object) {
                object.status = data.status === 200 ? CheckStatus.PASS : CheckStatus.FAIL;
            }

            function dataMessageToObject(data, object) {
                object.message = data.message;
            }
        }
    }

    UniqueFailIfInputRevise(object) {
        object.status = CheckStatus.FAIL;
    }
}

export class IsSameChecker {
    static isSamePassword = {
        element1: document.querySelector("#password")
        , element2: document.querySelector("#passwordCheck")
        , compareObject1: PatternMatcher.password
        , compareObject2: PatternMatcher.passwordCheck
        , passMessage: "비밀번호가 일치합니다."
        , failMessage: "비밀번호 입력값과 비밀번호 확인의 입력값이 일치하지 않습니다."
        , status: CheckStatus.FAIL
        , message: ""
    }

    static fieldList = [
        IsSameChecker.isSamePassword
    ];

    addAllIsSameEvent() {
        IsSameChecker.fieldList.forEach((item) => {
            this.addIsSameEvent(item);
        });
    }

    addIsSameEvent(isSameObject) {
        const applyingElement1 = isSameObject.element1;
        const applyingElement2 = isSameObject.element2;

        applyingElement1.addEventListener("input", () => {
            this.isSameEventHandler(applyingElement1, applyingElement2, isSameObject);
        });

        applyingElement2.addEventListener("input", () => {
            this.isSameEventHandler(applyingElement2, applyingElement1, isSameObject);
        });
    }

    isSameEventHandler(applyingElement, oppositeElement, isSameObject) {
        const compareObject1 = isSameObject.compareObject1;
        const compareObject2 = isSameObject.compareObject2;

        Messenger.removeIsSameBothElement(isSameObject);

        if (bothStatusNotPassed(compareObject1, compareObject2)) return;

        setStatusOf(isSameObject); // check sameness internally and then set status.
        Messenger.setMessageOf(isSameObject);
        Messenger.removeMessageIfExist(applyingElement, "IsMatched")

        if (isSameObject.status === CheckStatus.PASS) {
            Messenger.removeMessageIfExist(oppositeElement, "IsMatched")
        }

        Messenger.addResultMessage(applyingElement, isSameObject, "IsSame");
        SubmitChecker.checkSubmitAvailability(); // end of method.

        function bothStatusNotPassed(applyingObject, oppositeObject) {
            return applyingObject.status === CheckStatus.FAIL
                || oppositeObject.status === CheckStatus.FAIL;
        }

        function setStatusOf(isSameObject) {
            isSameObject.status = areSameTwoElementsIn(isSameObject) ? CheckStatus.PASS : CheckStatus.FAIL;

            function areSameTwoElementsIn(isSameObject) {
                return isSameObject.element1.value === isSameObject.element2.value;
            }
        }
    }
}
