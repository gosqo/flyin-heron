import Fetcher from "../common/Fetcher.js"
import DomCreate from "../dom/DomCreate.js";

export default class TestJwt {
    async testJwt() {
        const accessToken = localStorage.getItem("access_token")
        const url = "/tokenValidationTest";
        let options = {
            headers: {
                "Authorization": accessToken,
            }
        };

        try {
            const data = await Fetcher.withAuth(url, options);

            if (data === undefined) return;

            const paragraph = DomCreate.paragraph(null, null, `${data.email} / ${data.expiration}`);
            document.querySelector("#test-jwt-area").append(paragraph);

        } catch (error) {
            console.error("Error " + error);
        }
    }
}
