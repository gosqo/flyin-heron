import DomCreate from "../dom/DomCreate.js"

export class ValidCheck {
    static email;
    static password;
    static passwordCheck;
    static nickname;

    static checkSubmitCleared() {
        const submitButton = document.querySelector("#submit-form-btn");

        submitButton.disabled = this.isAllValidated() ? false : true;
    }

    static isAllValidated() {
        return ValidCheck.email === true
            && ValidCheck.password === true
            && ValidCheck.passwordCheck === true
            && ValidCheck.nickname === true
            && UniqueCheck.isUniqueEmail === true
            && UniqueCheck.isUniqueNickname === true;
    }
}

export class SignUpRegex {
    static email = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/;
    static password = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[A-Za-z\d~!@#$%^&*()+{}|:"<>?`=\[\]-_\\;']{8,20}$/;
    static nickname = /^[0-9a-zA-Z가-힣]{2,20}$/;
}

export class UniqueCheck {
    static isUniqueEmail;
    static isUniqueNickname;

    addPresentEmailEvent() {
        this.addEvent("email", UniqueCheck.isUniqueEmail);
    }

    addPresentNicknameEvent() {
        this.addEvent("nickname", UniqueCheck.isUniqueNickname);
    }

    addEvent(targetName, isUniqueInput) {
        const targetButton = document.querySelector(`#is-present-${targetName}-button`);

        targetButton.addEventListener("click", async (event) => {
            event.preventDefault();
            const targetInput = document.querySelector(`input[name="${targetName}"]`);

            Messenger.removeMessageIfPresent(targetInput, "CheckIsPresent");

            const isUniqueOnServer = await this.checkIsPresent(targetInput);
            if (isUniqueOnServer !== true) alert(isUniqueOnServer.message);

            Messenger.addResultIsPresent(targetInput, isUniqueOnServer, isUniqueInput);
            ValidCheck.checkSubmitCleared();
        });
    }

    async checkIsPresent(targetElement) {
        const capitalizedName = this.capitalize(targetElement);
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

            if (response.status === 200) return true;

            return response.json();
        } catch (error) {
            console.error("Error: ", error);
        }
    }

    // TODO move to StringUtility
    capitalize(targetElement) {
        const originName = targetElement.name;

        // const originName = "origin";
        const firstCharacter = originName[0].toUpperCase();
        const restCharacters = originName.substring(1, originName.length);
        const capitalizedName = firstCharacter + restCharacters;
        // console.log(capitalizedName);

        return capitalizedName;
    }
}

// 반복되는 호출, 전역에서 일관된 값을 참조. static properties.
export class Messenger {
    static presentEmailMessage = "이미 가입한 이메일 주소입니다.";
    static uniqueEmailMessage = "사용 가능한 이메일 주소입니다.";

    static {
        Object.freeze(this);
    }

    static removeMessageIfPresent(target, appendingId) {
        const targetElement = document.querySelector(`#${target.name}${appendingId}`);
        if (targetElement) targetElement.remove();
    }

    // isPresent 에서 isUnique 로 전환 중. 관련 기능 클라언트, 서버 수정 필요.
    static addResultIsPresent(target, isUnique, serverMessage, isUnique) {
        this.removeMessageIfPresent(target, "ResultIsPresent");

        const resultMessage = DomCreate.small(
            `${target.name}ResultIsPresent`, null, serverMessage
        );

        Messenger.colorMessage(isUnique, resultMessage);
        Messenger.flag(isUnique, isUnique)

        target.closest("div").nextElementSibling.nextElementSibling.append(resultMessage);
    }

    static colorMessage(isPresent, message) {
        message.style.color = isPresent ? "red" : "green";
    }

    static flag(isPresent, isUnique) {
        isUnique = !isPresent;
    }
}
