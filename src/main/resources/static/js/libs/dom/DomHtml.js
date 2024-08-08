export class DomHtml {
    /**
     * 링크가 필요한 본문에 사용.
     * 정규표현식으로 본문 내 링크를 찾고 <a></a> 태그로 감싼 후 parentNode.innerHTML 로 본문 채우기.
     */
    static addHyperLink(node) {
        if (node && node.textContent) {
            const urlPattern = /(https?:\/\/[^\s]+)/g;
            const content = node.textContent;
            const modifiedInHtml = content.replace(urlPattern, '<a href="$1" target="_blank">$1</a>');
        
            node.innerHTML = modifiedInHtml;
        }
    }
}
