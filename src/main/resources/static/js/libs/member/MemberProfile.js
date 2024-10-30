export class MemberProfile {
    static renderProfileImage(profileImageData, profileImageContainer, profileImage) {
        if (profileImageData === null) {
            profileImageContainer.remove();
            return;
        }

        profileImage.src = profileImageData.referencePath;
    }
}