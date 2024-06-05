export class StringUtility {
    static capitalize(element) {
        const originName = element.name;

        // const originName = "origin";
        const firstCharacter = originName[0].toUpperCase();
        const restCharacters = originName.substring(1, originName.length);
        const capitalizedName = firstCharacter + restCharacters;
        // console.log(capitalizedName);

        return capitalizedName;
    }
}