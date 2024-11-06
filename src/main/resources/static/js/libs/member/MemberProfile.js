import { DomCreate } from "../dom/DomCreate.js";
import { TokenUtility } from "../token/TokenUtility.js";
import { Fetcher } from "../common/Fetcher.js"

export class MemberProfileImage {
    static addImageUploader() {
        const profileImageContainer = document.querySelector("#profile-image-container");
        const profileImagePreviewContainer = document.querySelector("#profile-image-preview-container");
        const inputGroup = DomCreate.division("", "input-group m-auto", "");
        inputGroup.style.width = "15rem";

        const fileInput = document.createElement("input");

        fileInput.ariaLabel = "Upload";
        fileInput.id = "profile-image-input";
        fileInput.type = "file";
        fileInput.accept = "image/*";
        fileInput.classList.add("form-control");

        const formData = new FormData();

        fileInput.addEventListener("change", (event) => {
            const file = event.target.files[0];

            if (file === null || file === undefined) {
                return;
            }

            if (file.size > 5_128_000) {
                alert("크기가 5MB 이하인 이미지를 사용해주세요.");
                return;
            }

            const reader = new FileReader();
            reader.onload = (e) => {
                const previewImage = document.createElement("img");
                previewImage.classList.add("preview-image");
                previewImage.classList.add("mb-3");
                previewImage.src = e.target.result;
                previewImage.style.maxWidth = "16rem";
                previewImage.style.width = "100%";

                const existingPreview = document.querySelector(".preview-image");
                if (existingPreview) {
                    existingPreview.remove();
                }

                previewImage.classList.add("preview-image");
                profileImagePreviewContainer.append(previewImage);
            };
            reader.readAsDataURL(file);

            this.resizeImage(file, 1024, 1024, (resizedBlob) => {
                formData.set("profileImage", resizedBlob, file.name);

                console.log(formData.get("profileImage"));

                registerButton.removeAttribute("disabled");
            });
        });

        const registerButton = DomCreate.button("register-profile-image-button", "btn btn-info", "Add Profile Image");
        registerButton.disabled = "true";

        registerButton.addEventListener("click", async () => {
            registerButton.disabled = "true";

            const file = formData.get("profileImage");

            if (file === null || file === undefined) {
                alert("프로필 이미지로 사용할 파일을 업로드 해주세요.")
                return;
            }

            const parsed = TokenUtility.parseJwt(localStorage.getItem("access_token"));
            const memberId = parsed.id;

            const url = `/api/v1/member/${memberId}/profile/image`
            const options = {
                method: "POST",
                headers: {
                    "Authorization": localStorage.getItem("access_token")
                },
                body: formData
            };

            try {
                const data = await Fetcher.withAuth(url, options);
                if (data.status !== 201) {
                    alert(data.message);
                    return;
                }

                alert(data.message);
            } catch (error) {
                console.error("Error:", error);
                alert("이미지 업로드 중 오류가 발생했습니다.");
            }

            const existingPreview = document.querySelector(".preview-image");
            if (existingPreview) {
                existingPreview.remove();
            }
        });

        inputGroup.append(fileInput, registerButton);
        profileImageContainer.append(inputGroup);
    }

    static resizeImage(file, maxWidth, maxHeight, callback) {
        const img = new Image();
        img.onload = () => {
            let width = img.width;
            let height = img.height;
            if (width > maxWidth || height > maxHeight) {
                if (width > height) {
                    height *= maxWidth / width;
                    width = maxWidth;
                } else {
                    width *= maxHeight / height;
                    height = maxHeight;
                }
            }
            const canvas = document.createElement("canvas");
            canvas.width = width;
            canvas.height = height;
            canvas.getContext("2d").drawImage(img, 0, 0, width, height);
            canvas.toBlob(callback, file.type, 0.7); // 이미지 품질 설정
        };
        img.src = URL.createObjectURL(file);
    }


    static renderProfileImage(profileImageData, profileImageContainer, profileImage) {
        if (profileImageData === null) {
            profileImageContainer.remove();
            return;
        }

        profileImage.src = profileImageData.referencePath;
    }
}
